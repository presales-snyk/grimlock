// Copyright 2014,2015 Commonwealth Bank of Australia
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package au.com.cba.omnia.grimlock.spark.examples

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.content._
import au.com.cba.omnia.grimlock.framework.position._

import au.com.cba.omnia.grimlock.library.aggregate._
import au.com.cba.omnia.grimlock.library.squash._
import au.com.cba.omnia.grimlock.library.transform._

import au.com.cba.omnia.grimlock.spark.Matrix._

import org.apache.spark.{ SparkConf, SparkContext }

object Conditional {

  def main(args: Array[String]) {
    // Define implicit context for reading.
    implicit val spark = new SparkContext(args(0), "Grimlock Spark Demo", new SparkConf())

    // Path to data files, output folder
    val path = if (args.length > 1) args(1) else "../../data"
    val output = "spark"

    // Read the data.
    // 1/ Read the data (ignoring errors), this returns a 2D matrix (row x feature).
    val (data, _) = loadText(s"${path}/exampleConditional.txt", Cell.parse2D())

    // Get map of row id -> hair color
    // 1/ Squash the matrix keeping only hair column.
    // 2/ Compact vector into a map.
    val hair = data
      .squash(Second, KeepSlice[Position2D]("hair"))
      .compact(Over(First))

    // Get map of row id -> eye color
    // 1/ Squash the matrix keeping only eye column.
    // 2/ Compact vector into a map.
    val eye = data
      .squash(Second, KeepSlice[Position2D]("eye"))
      .compact(Over(First))

    // Get map of row id -> gender
    // 1/ Squash the matrix keeping only gender column.
    // 2/ Compact vector into a map.
    val gender = data
      .squash(Second, KeepSlice[Position2D]("gender"))
      .compact(Over(First))

    // Define function that expands based on the row id.
    def expander[P <: Position with ExpandablePosition](cell: Cell[P],
      ext: Map[Position1D, Content]): TraversableOnce[P#M] = {
      ext.get(Position1D(cell.position(First))).map { case con => cell.position.append(con.value) }
    }

    // Generate 3D matrix (hair color x eye color x gender)
    // 1/ Expand matrix with hair color.
    // 2/ Expand matrix with eye color.
    // 3/ Expand matrix with gender.
    // 4/ Keep only the 'value' column of the second dimension (dropping hair/eye/gender
    //    columns as they are now extra dimensions).
    // 5/ Squash the first dimension (row ids). As there is only one value for each
    //    hair/eye/gender triplet, any squash function can be used.
    val heg = data
      .expandWithValue(expander[Position2D], hair)
      .expandWithValue(expander[Position3D], eye)
      .expandWithValue(expander[Position4D], gender)
      .squash(Second, KeepSlice[Position5D]("value"))
      .squash(First, PreservingMaxPosition[Position4D]())

    // Define an extractor for getting data out of the gender count map.
    def extractor = ExtractWithDimension[Position2D, Content](Second).andThenPresent(_.value.asDouble)

    // Get the gender counts. Sum out hair and eye color.
    val gcount = heg
      .summarise(Along(First), Sum[Position3D, Position2D]())
      .summarise(Along(First), Sum[Position2D, Position1D]())
      .compact()

    // Get eye color conditional on gender.
    // 1/ Sum out hair color.
    // 2/ Divide each element by the gender's count to get conditional distribution.
    heg
      .summarise(Along(First), Sum[Position3D, Position2D]())
      .transformWithValue(Fraction(extractor), gcount)
      .saveAsText(s"./demo.${output}/eye.out")
      .toUnit

    // Get hair color conditional on gender.
    // 1/ Sum out eye color.
    // 2/ Divide each element by the gender's count to get conditional distribution.
    heg
      .summarise(Along(Second), Sum[Position3D, Position2D]())
      .transformWithValue(Fraction(extractor), gcount)
      .saveAsText(s"./demo.${output}/hair.out")
      .toUnit
  }
}
