// Copyright 2017 Commonwealth Bank of Australia
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

package commbank.grimlock.framework.environment.implicits

import commbank.grimlock.framework.{
  Cell,
  Matrix,
  Matrix1D,
  Matrix2D,
  Matrix3D,
  Matrix4D,
  Matrix5D,
  Matrix6D,
  Matrix7D,
  Matrix8D,
  Matrix9D,
  MatrixWithParseErrors,
  MultiDimensionMatrix,
  SaveStringsAsText
}
import commbank.grimlock.framework.content.{ Content, Contents, IndexedContents }
import commbank.grimlock.framework.encoding.Value
import commbank.grimlock.framework.environment.Context
import commbank.grimlock.framework.partition.Partitions
import commbank.grimlock.framework.position.{ Position, Positions }

import scala.reflect.ClassTag

import shapeless.{ ::, =:!=, HList, HNil, Nat }
import shapeless.nat.{ _0, _1, _2, _3, _4, _5, _6, _7, _8 }
import shapeless.ops.hlist.Length
import shapeless.ops.nat.GT

/** Defines standard functional operations for dealing with distributed lists. */
trait NativeOperations[X, U[_]] {
  /** Return the union of this `U` and `other`. */
  def ++(other: U[X]): U[X]

  /** Keep only items that satisfy the predicate `f`. */
  def filter(f: (X) => Boolean): U[X]

  /** Apply function `f`, then flatten the results. */
  def flatMap[Y : ClassTag](f: (X) => TraversableOnce[Y]): U[Y]

  /** Map each element using the function `f`. */
  def map[Y : ClassTag](f: (X) => Y): U[Y]
}

/** Defines operations for dealing with user defined values. */
trait ValueOperations[X, E[_]] {
  /** Cross this value with `that`. */
  def cross[Y](that: E[Y])(implicit ev: Y =:!= Nothing): E[(X, Y)]

  /** Apply the function `f` to the value. */
  def map[Y](f: (X) => Y): E[Y]
}

/** Defines convenience implicits for dealing with distributed cells. */
trait CellImplicits[C <: Context[C]] {
  /** Converts a `Cell[P]` into a `C#U[Cell[P]]`. */
  implicit def cellToU[P <: HList](c: Cell[P])(implicit ctx: C): C#U[Cell[P]]

  /** Converts a `List[Cell[P]]` into a `C#U[Cell[P]]`. */
  implicit def listCellToU[P <: HList](l: List[Cell[P]])(implicit ctx: C): C#U[Cell[P]]
}

/** Defines convenience implicits for dealing with distributed contents. */
trait ContentImplicits[C <: Context[C]] {
  /** Converts a `C#U[Content]` to a `Contents`. */
  implicit def toContents(data: C#U[Content]): Contents[C]

  /** Converts a `C#U[(Position[P], Content)]` to a `IndexedContents`. */
  implicit def toIndexed[P <: HList](data: C#U[(Position[P], Content)]): IndexedContents[P, C]
}

/** Defines convenience implicits for dealing with distributed strings. */
trait EnvironmentImplicits[C <: Context[C]] {
  /** Converts a `C#U[String]` to a `SaveStringsAsText`. */
  implicit def saveStringsAsText(data: C#U[String]): SaveStringsAsText[C]

  /** Make available native functions of `C#U`. */
  implicit def nativeFunctions[X](data: C#U[X]): NativeOperations[X, C#U]

  /** Make available functions of `C#E`. */
  implicit def valueFunctions[X](value: C#E[X]): ValueOperations[X, C#E]

  /** Convert an `C#E` to a `C#U`. */
  implicit def eToU[X : ClassTag](value: C#E[X])(implicit ctx: C): C#U[X]
}

/** Defines convenience implicits for dealing with matrices. */
trait MatrixImplicits[C <: Context[C]] {
  /** Converts a `C#U[Cell[P]]` to a `Matrix`. */
  implicit def toMatrix[P <: HList](data: C#U[Cell[P]]): Matrix[P, C]

  /** Conversion from `C#U[Cell[V1 :: HNil]]` to a `Matrix1D`. */
  implicit def toMatrix1D[
    V1 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: HNil, _0, V1]
  ): Matrix1D[V1, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: HNil]]` to a `Matrix2D`. */
  implicit def toMatrix2D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: HNil, _1, V2]
  ): Matrix2D[V1, V2, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: V3 :: HNil]]` to a `Matrix3D`. */
  implicit def toMatrix3D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: V3 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _2, V3]
  ): Matrix3D[V1, V2, V3, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: V3 :: V4 :: HNil]]` to a `Matrix4D`. */
  implicit def toMatrix4D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: V3 :: V4 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _3, V4]
  ): Matrix4D[V1, V2, V3, V4, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: HNil]]` to a `Matrix5D`. */
  implicit def toMatrix5D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _4, V5]
  ): Matrix5D[V1, V2, V3, V4, V5, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil]]` to a `Matrix6D`. */
  implicit def toMatrix6D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _5, V6]
  ): Matrix6D[V1, V2, V3, V4, V5, V6, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil]]` to a `Matrix7D`. */
  implicit def toMatrix7D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _6, V7]
  ): Matrix7D[V1, V2, V3, V4, V5, V6, V7, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil]]` to a `Matrix8D`. */
  implicit def toMatrix8D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag,
    V8 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _6, V7],
    ev8: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _7, V8]
  ): Matrix8D[V1, V2, V3, V4, V5, V6, V7, V8, C]

  /** Conversion from `C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil]]` to a `Matrix9D`. */
  implicit def toMatrix9D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag,
    V8 <: Value[_] : ClassTag,
    V9 <: Value[_] : ClassTag
  ](
    data: C#U[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil]]
  )(implicit
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _6, V7],
    ev8: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _7, V8],
    ev9: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _8, V9]
  ): Matrix9D[V1, V2, V3, V4, V5, V6, V7, V8, V9, C]

  /** Converts a `C#U[Cell[P]]` to a `MultiDimensionMatrix`. */
  implicit def toMultiDimensionMatrix[
    P <: HList,
    L <: Nat
  ](
    data: C#U[Cell[P]]
  )(implicit
    ev1: Length.Aux[P, L],
    ev2: GT[L, _1]
  ): MultiDimensionMatrix[P, C]

  /** Converts a `List[Cell[P]]` to a `Matrix`. */
  implicit def listToMatrix[P <: HList](data: List[Cell[P]])(implicit ctx: C): Matrix[P, C]

  /** Conversion from `List[Cell[V1 :: HNil]]` to a `Matrix1D`. */
  implicit def listToMatrix1D[
    V1 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: HNil, _0, V1]
  ): Matrix1D[V1, C]

  /** Conversion from `List[Cell[V1 :: V2 :: HNil]]` to a `Matrix2D`. */
  implicit def listToMatrix2D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: HNil, _1, V2]
  ): Matrix2D[V1, V2, C]

  /** Conversion from `List[Cell[V1 :: V2 :: V3 :: HNil]]` to a `Matrix3D`. */
  implicit def listToMatrix3D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: V3 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _2, V3]
  ): Matrix3D[V1, V2, V3, C]

  /** Conversion from `List[Cell[V1 :: V2 :: V3 :: V4 :: HNil]]` to a `Matrix4D`. */
  implicit def listToMatrix4D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: V3 :: V4 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _3, V4]
  ): Matrix4D[V1, V2, V3, V4, C]

  /** Conversion from `List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: HNil]]` to a `Matrix5D`. */
  implicit def listToMatrix5D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _4, V5]
  ): Matrix5D[V1, V2, V3, V4, V5, C]

  /** Conversion from `List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil]]` to a `Matrix6D`. */
  implicit def listToMatrix6D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _5, V6]
  ): Matrix6D[V1, V2, V3, V4, V5, V6, C]

  /** Conversion from `List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil]]` to a `Matrix7D`. */
  implicit def listToMatrix7D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _6, V7]
  ): Matrix7D[V1, V2, V3, V4, V5, V6, V7, C]

  /** Conversion from `List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil]]` to a `Matrix8D`. */
  implicit def listToMatrix8D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag,
    V8 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _6, V7],
    ev8: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _7, V8]
  ): Matrix8D[V1, V2, V3, V4, V5, V6, V7, V8, C]

  /** Conversion from `List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil]]` to a `Matrix9D`. */
  implicit def listToMatrix9D[
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag,
    V8 <: Value[_] : ClassTag,
    V9 <: Value[_] : ClassTag
  ](
    data: List[Cell[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil]]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _6, V7],
    ev8: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _7, V8],
    ev9: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _8, V9]
  ): Matrix9D[V1, V2, V3, V4, V5, V6, V7, V8, V9, C]

  /** Converts a `List[Cell[P]]` to a `MultiDimensionMatrix`. */
  implicit def listToMultiDimensionMatrix[
    P <: HList,
    L <: Nat
  ](
    data: List[Cell[P]]
  )(implicit
    ctx: C,
    ev1: Length.Aux[P, L],
    ev2: GT[L, _1]
  ): MultiDimensionMatrix[P, C]

  /** Conversion from `List[(T1, Content)]` to a `Matrix`. */
  implicit def tuple1ToMatrix[
    T1 <% V1,
    V1 <: Value[_]
  ](
    list: List[(T1, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: HNil, C]

  /** Conversion from `List[(T1, Content)]` to a `Matrix1D`. */
  implicit def tuple1ToMatrix1D[
    T1 <% V1,
    V1 <: Value[_] : ClassTag
  ](
    list: List[(T1, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: HNil, _0, V1]
  ): Matrix1D[V1, C]

  /** Conversion from `List[(T1, T2, Content)]` to a `Matrix`. */
  implicit def tuple2ToMatrix[
    T1 <% V1,
    T2 <% V2,
    V1 <: Value[_],
    V2 <: Value[_]
  ](
    list: List[(T1, T2, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: HNil, C]

  /** Conversion from `List[(T1, T2, Content)]` to a `Matrix2D`. */
  implicit def tuple2ToMatrix2D[
    T1 <% V1,
    T2 <% V2,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: HNil, _1, V2]
  ): Matrix2D[V1, V2, C]

  /** Conversion from `List[(T1, T2, Content)]` to a `Matrix`. */
  implicit def tuple2ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    V1 <: Value[_],
    V2 <: Value[_]
  ](
    list: List[(T1, T2, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, Content)]` to a `Matrix`. */
  implicit def tuple3ToMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_]
  ](
    list: List[(T1, T2, T3, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: V3 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, Content)]` to a `Matrix3D`. */
  implicit def tuple3ToMatrix3D[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, T3, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: HNil, _2, V3]
  ): Matrix3D[V1, V2, V3, C]

  /** Conversion from `List[(T1, T2, T3, Content)]` to a `MultiDimensionMatrix`. */
  implicit def tuple3ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_]
  ](
    list: List[(T1, T2, T3, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: V3 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, Content)]` to a `Matrix`. */
  implicit def tuple4ToMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: V3 :: V4 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, Content)]` to a `Matrix4D`. */
  implicit def tuple4ToMatrix4D[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, T3, T4, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: HNil, _3, V4]
  ): Matrix4D[V1, V2, V3, V4, C]

  /** Conversion from `List[(T1, T2, T3, T4, Content)]` to a `MultiDimensionMatrix`. */
  implicit def tuple4ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: V3 :: V4 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, Content)]` to a `Matrix`. */
  implicit def tuple5ToMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, Content)]` to a `Matrix5D`. */
  implicit def tuple5ToMatrix5D[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, T3, T4, T5, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, _4, V5]
  ): Matrix5D[V1, V2, V3, V4, V5, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, Content)]` to a `MultiDimensionMatrix`. */
  implicit def tuple5ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: V3 :: V4 :: V5 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, Content)]` to a `Matrix`. */
  implicit def tuple6ToMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, Content)]` to a `Matrix6D`. */
  implicit def tuple6ToMatrix6D[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, T3, T4, T5, T6, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, _5, V6]
  ): Matrix6D[V1, V2, V3, V4, V5, V6, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, Content)]` to a `MultiDimensionMatrix`. */
  implicit def tuple6ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, Content)]` to a `Matrix`. */
  implicit def tuple7ToMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_],
    V7 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, Content)]` to a `Matrix7D`. */
  implicit def tuple7ToMatrix7D[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, _6, V7]
  ): Matrix7D[V1, V2, V3, V4, V5, V6, V7, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, Content)]` to a `MultiDimensionMatrix`. */
  implicit def tuple7ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_],
    V7 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, T8, Content)]` to a `Matrix`. */
  implicit def tuple8ToMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    T8 <% V8,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_],
    V7 <: Value[_],
    V8 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, T8, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, T8, Content)]` to a `Matrix8D`. */
  implicit def tuple8ToMatrix8D[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    T8 <% V8,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag,
    V8 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, T8, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _6, V7],
    ev8: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, _7, V8]
  ): Matrix8D[V1, V2, V3, V4, V5, V6, V7, V8, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, T8, Content)]` to a `MultiDimensionMatrix`. */
  implicit def tuple8ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    T8 <% V8,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_],
    V7 <: Value[_],
    V8 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, T8, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, T8, T9, Content)]` to a `Matrix`. */
  implicit def tuple9ToMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    T8 <% V8,
    T9 <% V9,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_],
    V7 <: Value[_],
    V8 <: Value[_],
    V9 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, T8, T9, Content)]
  )(implicit
    ctx: C
  ): Matrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, T8, T9, Content)]` to a `Matrix9D`. */
  implicit def tuple9ToMatrix9D[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    T8 <% V8,
    T9 <% V9,
    V1 <: Value[_] : ClassTag,
    V2 <: Value[_] : ClassTag,
    V3 <: Value[_] : ClassTag,
    V4 <: Value[_] : ClassTag,
    V5 <: Value[_] : ClassTag,
    V6 <: Value[_] : ClassTag,
    V7 <: Value[_] : ClassTag,
    V8 <: Value[_] : ClassTag,
    V9 <: Value[_] : ClassTag
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, T8, T9, Content)]
  )(implicit
    ctx: C,
    ev1: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _0, V1],
    ev2: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _1, V2],
    ev3: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _2, V3],
    ev4: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _3, V4],
    ev5: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _4, V5],
    ev6: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _5, V6],
    ev7: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _6, V7],
    ev8: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _7, V8],
    ev9: Position.IndexConstraints[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, _8, V9]
  ): Matrix9D[V1, V2, V3, V4, V5, V6, V7, V8, V9, C]

  /** Conversion from `List[(T1, T2, T3, T4, T5, T6, T7, T8, T9, Content)]` to a `MultiDimensionMatrix`. */
  implicit def tuple9ToMultiDimensionMatrix[
    T1 <% V1,
    T2 <% V2,
    T3 <% V3,
    T4 <% V4,
    T5 <% V5,
    T6 <% V6,
    T7 <% V7,
    T8 <% V8,
    T9 <% V9,
    V1 <: Value[_],
    V2 <: Value[_],
    V3 <: Value[_],
    V4 <: Value[_],
    V5 <: Value[_],
    V6 <: Value[_],
    V7 <: Value[_],
    V8 <: Value[_],
    V9 <: Value[_]
  ](
    list: List[(T1, T2, T3, T4, T5, T6, T7, T8, T9, Content)]
  )(implicit
    ctx: C
  ): MultiDimensionMatrix[V1 :: V2 :: V3 :: V4 :: V5 :: V6 :: V7 :: V8 :: V9 :: HNil, C]

  /** Conversion from matrix with errors tuple to `MatrixWithParseErrors`. */
  implicit def tupleToParseErrors[
    P <: HList
  ](
    t: (C#U[Cell[P]], C#U[String])
  ): MatrixWithParseErrors[P, C#U] = MatrixWithParseErrors(t._1, t._2)
}

/** Defines convenience implicits for dealing with distributed partitions. */
trait PartitionImplicits[C <: Context[C]] {
  /** Conversion from `C#U[(I, Cell[P])]` to a `Partitions`. */
  implicit def toPartitions[P <: HList, I : Ordering](data: C#U[(I, Cell[P])]): Partitions[P, I, C]
}

/** Defines convenience implicits for dealing with distributed positions. */
trait PositionImplicits[C <: Context[C]] {
  /** Converts a `T` to a `C#U[Position[V :: HNil]]`. */
  implicit def valueToU[T <% V, V <: Value[_]](t: T)(implicit ctx: C): C#U[Position[V :: HNil]]

  /** Converts a `List[T]` to a `C#U[Position[V :: HNil]]`. */
  implicit def listValueToU[T <% V,V <: Value[_]](l: List[T])(implicit ctx: C): C#U[Position[V :: HNil]]

  /** Converts a `Position[P]` to a `C#U[Position[P]]`. */
  implicit def positionToU[P <: HList](p: Position[P])(implicit ctx: C): C#U[Position[P]]

  /** Converts a `List[Position[P]]` to a `C#U[Position[P]]`. */
  implicit def listPositionToU[P <: HList](l: List[Position[P]])(implicit ctx: C): C#U[Position[P]]

  /** Converts a `C#U[Position[P]]` to a `Positions`. */
  implicit def toPositions[P <: HList](data: C#U[Position[P]]): Positions[P, C]

  /** Converts a `(T, Cell.Predicate[P])` to a `List[(C#U[Position[S]], Cell.Predicate[P])]`. */
  implicit def predicateToU[
    P <: HList,
    S <: HList,
    T <% C#U[Position[S]]
  ](
    t: (T, Cell.Predicate[P])
  )(implicit
    ctx: C
  ): List[(C#U[Position[S]], Cell.Predicate[P])] = {
    val u: C#U[Position[S]] = t._1

    List((u, t._2))
  }

  /** Converts a `List[(T, Cell.Predicate[P])]` to a `List[(C#U[Position[S]], Cell.Predicate[P])]`. */
  implicit def listPredicateToU[
    P <: HList,
    S <: HList,
    T <% C#U[Position[S]]
  ](
    l: List[(T, Cell.Predicate[P])]
  )(implicit
    ctx: C
  ): List[(C#U[Position[S]], Cell.Predicate[P])] = l
    .map { case (i, p) =>
      val u: C#U[Position[S]] = i

      (u, p)
    }
}

/** Capture all implicits together. */
trait Implicits[C <: Context[C]] {
  /** Cell related implicits. */
  val cell: CellImplicits[C]

  /** Content related implicits. */
  val content: ContentImplicits[C]

  /** Environment related implicits. */
  val environment: EnvironmentImplicits[C]

  /** Matrix related implicits. */
  val matrix: MatrixImplicits[C]

  /** Partition related implicits. */
  val partition: PartitionImplicits[C]

  /** Position related implicits. */
  val position: PositionImplicits[C]
}
