// Copyright 2015 Commonwealth Bank of Australia
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

package au.com.cba.omnia.grimlock

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.content._
import au.com.cba.omnia.grimlock.framework.content.metadata._
import au.com.cba.omnia.grimlock.framework.encoding._
import au.com.cba.omnia.grimlock.framework.position._

import au.com.cba.omnia.grimlock.scalding.Matrix._

import au.com.cba.omnia.grimlock.spark.Matrix._

import com.twitter.scalding.typed.{ TypedPipe, ValuePipe }

trait TestDistribution extends TestGrimlock {

  val data1 = List(Cell(Position1D("foo"), Content(OrdinalSchema(StringCodex), "3.14")),
    Cell(Position1D("bar"), Content(OrdinalSchema(StringCodex), "6.28")),
    Cell(Position1D("bar"), Content(OrdinalSchema(StringCodex), "6.28")),
    Cell(Position1D("qux"), Content(OrdinalSchema(StringCodex), "3.14")))

  val data2 = List(Cell(Position2D("row1", "col1"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position2D("row1", "col3"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row1", "col4"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position2D("row2", "col1"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position2D("row2", "col2"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position2D("row2", "col4"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("row3", "col2"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position2D("row3", "col3"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position2D("row3", "col4"), Content(DiscreteSchema(LongCodex), 3)),
    Cell(Position2D("row4", "col1"), Content(DiscreteSchema(LongCodex), 4)),
    Cell(Position2D("row4", "col2"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position2D("row4", "col3"), Content(NominalSchema(StringCodex), "b")))

  val data3 = List(Cell(Position3D("row1", "col1", "dep1"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position3D("row1", "col3", "dep1"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row1", "col4", "dep1"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position3D("row2", "col1", "dep1"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position3D("row2", "col2", "dep1"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position3D("row2", "col4", "dep1"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position3D("row3", "col2", "dep1"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position3D("row3", "col3", "dep1"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position3D("row3", "col4", "dep1"), Content(DiscreteSchema(LongCodex), 3)),
    Cell(Position3D("row4", "col1", "dep1"), Content(DiscreteSchema(LongCodex), 4)),
    Cell(Position3D("row4", "col2", "dep1"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position3D("row4", "col3", "dep1"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position3D("row1", "col1", "dep2"), Content(NominalSchema(StringCodex), "a")),
    Cell(Position3D("row2", "col2", "dep2"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position3D("row3", "col3", "dep2"), Content(NominalSchema(StringCodex), "b")),
    Cell(Position3D("row4", "col4", "dep2"), Content(NominalSchema(StringCodex), "a")))

  val result1 = List(Cell(Position2D("bar", "6.28"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("foo", "3.14"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("qux", "3.14"), Content(DiscreteSchema(LongCodex), 1)))

  val result2 = List(Cell(Position1D("3.14"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position1D("6.28"), Content(DiscreteSchema(LongCodex), 2)))

  val result3 = List(Cell(Position2D("row1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row3", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row3", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row4", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row4", "b"), Content(DiscreteSchema(LongCodex), 1)))

  val result4 = List(Cell(Position2D("col1", "a"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("col2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col2", "b"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("col3", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col3", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col4", "b"), Content(DiscreteSchema(LongCodex), 1)))

  val result5 = List(Cell(Position2D("col1", "4"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col1", "a"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("col2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col2", "b"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("col3", "1"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col3", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col3", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col4", "2"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col4", "3"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col4", "b"), Content(DiscreteSchema(LongCodex), 1)))

  val result6 = List(Cell(Position2D("row1", "1"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "2"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row3", "3"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row3", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row3", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row4", "4"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row4", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row4", "b"), Content(DiscreteSchema(LongCodex), 1)))

  val result7 = List(Cell(Position2D("row1", "1"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row1", "a"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("row1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "2"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row2", "b"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("row3", "3"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row3", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row3", "b"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("row4", "4"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("row4", "a"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("row4", "b"), Content(DiscreteSchema(LongCodex), 1)))

  val result8 = List(Cell(Position3D("col1", "dep1", "4"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col1", "dep1", "a"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position3D("col1", "dep2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col2", "dep1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col2", "dep1", "b"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position3D("col2", "dep2", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col3", "dep1", "1"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col3", "dep1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col3", "dep1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col3", "dep2", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col4", "dep1", "2"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col4", "dep1", "3"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col4", "dep1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("col4", "dep2", "a"), Content(DiscreteSchema(LongCodex), 1)))

  val result9 = List(Cell(Position2D("col1", "a"), Content(DiscreteSchema(LongCodex), 3)),
    Cell(Position2D("col2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col2", "b"), Content(DiscreteSchema(LongCodex), 3)),
    Cell(Position2D("col3", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col3", "b"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("col4", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position2D("col4", "b"), Content(DiscreteSchema(LongCodex), 1)))

  val result10 = List(Cell(Position3D("row1", "dep1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row1", "dep1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row1", "dep2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row2", "dep1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row2", "dep1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row2", "dep2", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row3", "dep1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row3", "dep1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row3", "dep2", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row4", "dep1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row4", "dep1", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row4", "dep2", "a"), Content(DiscreteSchema(LongCodex), 1)))

  val result11 = List(Cell(Position2D("dep1", "a"), Content(DiscreteSchema(LongCodex), 4)),
    Cell(Position2D("dep1", "b"), Content(DiscreteSchema(LongCodex), 4)),
    Cell(Position2D("dep2", "a"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position2D("dep2", "b"), Content(DiscreteSchema(LongCodex), 2)))

  val result12 = List(Cell(Position3D("row1", "col1", "a"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position3D("row1", "col4", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row2", "col1", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row2", "col2", "b"), Content(DiscreteSchema(LongCodex), 2)),
    Cell(Position3D("row3", "col2", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row3", "col3", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row3", "col3", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row4", "col2", "a"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row4", "col3", "b"), Content(DiscreteSchema(LongCodex), 1)),
    Cell(Position3D("row4", "col4", "a"), Content(DiscreteSchema(LongCodex), 1)))
}

class TestScaldingHistogram extends TestDistribution {

  "A histogram" should "return its first over in 1D" in {
    toPipe(data1)
      .histogram(Over(First), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result1
  }

  it should "return its first along in 1D" in {
    toPipe(data1)
      .histogram(Along(First), Locate.AppendContentString[Position0D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result2
  }

  it should "return its first over in 2D" in {
    toPipe(data2)
      .histogram(Over(First), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result3
  }

  it should "return its first along in 2D" in {
    toPipe(data2)
      .histogram(Along(First), Locate.AppendContentString[Position1D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result4
  }

  it should "return its second over in 2D" in {
    toPipe(data2)
      .histogram(Over(Second), Locate.AppendContentString[Position1D](), true, Default())
      .toList.sortBy(_.position) shouldBe result5
  }

  it should "return its second along in 2D" in {
    toPipe(data2)
      .histogram(Along(Second), Locate.AppendContentString[Position1D](), true, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result6
  }

  it should "return its first over in 3D" in {
    toPipe(data3)
      .histogram(Over(First), Locate.AppendContentString[Position1D](), true, Default())
      .toList.sortBy(_.position) shouldBe result7
  }

  it should "return its first along in 3D" in {
    toPipe(data3)
      .histogram(Along(First), Locate.AppendContentString[Position2D](), true, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result8
  }

  it should "return its second over in 3D" in {
    toPipe(data3)
      .histogram(Over(Second), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result9
  }

  it should "return its second along in 3D" in {
    toPipe(data3)
      .histogram(Along(Second), Locate.AppendContentString[Position2D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result10
  }

  it should "return its third over in 3D" in {
    toPipe(data3)
      .histogram(Over(Third), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result11
  }

  it should "return its third along in 3D" in {
    toPipe(data3)
      .histogram(Along(Third), Locate.AppendContentString[Position2D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result12
  }
}

class TestSparkHistogram extends TestDistribution {

  "A histogram" should "return its first over in 1D" in {
    toRDD(data1)
      .histogram(Over(First), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result1
  }

  it should "return its first along in 1D" in {
    toRDD(data1)
      .histogram(Along(First), Locate.AppendContentString[Position0D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result2
  }

  it should "return its first over in 2D" in {
    toRDD(data2)
      .histogram(Over(First), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result3
  }

  it should "return its first along in 2D" in {
    toRDD(data2)
      .histogram(Along(First), Locate.AppendContentString[Position1D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result4
  }

  it should "return its second over in 2D" in {
    toRDD(data2)
      .histogram(Over(Second), Locate.AppendContentString[Position1D](), true, Default())
      .toList.sortBy(_.position) shouldBe result5
  }

  it should "return its second along in 2D" in {
    toRDD(data2)
      .histogram(Along(Second), Locate.AppendContentString[Position1D](), true, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result6
  }

  it should "return its first over in 3D" in {
    toRDD(data3)
      .histogram(Over(First), Locate.AppendContentString[Position1D](), true, Default())
      .toList.sortBy(_.position) shouldBe result7
  }

  it should "return its first along in 3D" in {
    toRDD(data3)
      .histogram(Along(First), Locate.AppendContentString[Position2D](), true, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result8
  }

  it should "return its second over in 3D" in {
    toRDD(data3)
      .histogram(Over(Second), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result9
  }

  it should "return its second along in 3D" in {
    toRDD(data3)
      .histogram(Along(Second), Locate.AppendContentString[Position2D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result10
  }

  it should "return its third over in 3D" in {
    toRDD(data3)
      .histogram(Over(Third), Locate.AppendContentString[Position1D](), false, Default())
      .toList.sortBy(_.position) shouldBe result11
  }

  it should "return its third along in 3D" in {
    toRDD(data3)
      .histogram(Along(Third), Locate.AppendContentString[Position2D](), false, Default(Reducers(12)))
      .toList.sortBy(_.position) shouldBe result12
  }
}
