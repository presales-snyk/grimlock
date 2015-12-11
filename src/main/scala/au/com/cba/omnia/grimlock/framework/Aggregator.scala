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

package au.com.cba.omnia.grimlock.framework.aggregate

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.position._

/** Base trait for aggregations. */
trait Aggregator[P <: Position, S <: Position with ExpandablePosition, Q <: Position]
  extends AggregatorWithValue[P, S, Q] { self =>
  type V = Any

  def prepareWithValue(cell: Cell[P], ext: V): T = prepare(cell)
  def presentWithValue(pos: S, t: T, ext: V): Option[Cell[Q]] = present(pos, t)

  /**
   * Prepare for reduction.
   *
   * @param cell Cell which is to be aggregated. Note that its position is prior to `slice.selected` being applied.
   *
   * @return State to reduce.
   */
  def prepare(cell: Cell[P]): T

  /**
   * Present the reduced content.
   *
   * @param pos The reduced position. That is, the position returned by `slice.selected`.
   * @param t   The reduced state.
   *
   * @return Optional cell where the position is `pos` and the content is derived from `t`.
   *
   * @note An `Option` is used in the return type to allow aggregators to be selective in what content they apply to.
   *       For example, computing the mean is undefined for categorical variables. The aggregator now has the option to
   *       return `None`. This in turn permits an external API, for simple cases, where the user need not know about
   *       the types of variables of their data.
   */
  def present(pos: S, t: T): Option[Cell[Q]]

  /**
   * Operator for aggregating and then renaming dimensions.
   *
   * @param rename The rename to apply after the aggregation.
   *
   * @return An aggregator that runs `this` and then renames the resulting dimension(s).
   */
  override def andThenRename(rename: Locate.FromOutcome[Q, Q]) = {
    new Aggregator[P, S, Q] {
      type T = self.T

      def prepare(cell: Cell[P]): T = self.prepare(cell)
      def reduce(lt: T, rt: T): T = self.reduce(lt, rt)
      def present(pos: S, t: T): Option[Cell[Q]] = self.present(pos, t).map { case c => Cell(rename(c), c.content) }
    }
  }

  /**
   * Operator for aggregating and then expanding dimensions.
   *
   * @param expand The expansion to apply after the aggregation.
   *
   * @return An aggregator that runs `this` and then expands the resulting dimensions.
   */
  override def andThenExpand[R <: Position](expand: Locate.FromOutcome[Q, R])(implicit ev: PosExpDep[Q, R]) = {
    new Aggregator[P, S, R] {
      type T = self.T

      def prepare(cell: Cell[P]): T = self.prepare(cell)
      def reduce(lt: T, rt: T): T = self.reduce(lt, rt)
      def present(pos: S, t: T): Option[Cell[R]] = self.present(pos, t).map { case c => Cell(expand(c), c.content) }
    }
  }
}

/** Base trait for aggregations with a user supplied value. */
trait AggregatorWithValue[P <: Position, S <: Position with ExpandablePosition, Q <: Position]
  extends java.io.Serializable { self =>
  /** Type of the state being aggregated. */
  type T

  /** Type of the external value. */
  type V

  /**
   * Prepare for reduction.
   *
   * @param cell Cell which is to be aggregated. Note that its position is prior to `slice.selected` being applied.
   * @param ext  User provided data required for preparation.
   *
   * @return State to reduce.
   */
  def prepareWithValue(cell: Cell[P], ext: V): T

  /**
   * Standard reduce method.
   *
   * @param lt Left state to reduce.
   * @param rt Right state to reduce.
   *
   * @return Reduced state
   */
  def reduce(lt: T, rt: T): T

  /**
   * Present the reduced content.
   *
   * @param pos The reduced position. That is, the position returned by `Slice.selected`.
   * @param t   The reduced state.
   * @param ext User provided data required for presentation.
   *
   * @return Optional cell where the position is `pos` and the content is derived from `t`.
   *
   * @note An `Option` is used in the return type to allow aggregators to be selective in what content they apply to.
   *       For example, computing the mean is undefined for categorical variables. The aggregator now has the option to
   *       return `None`. This in turn permits an external API, for simple cases, where the user need not know about
   *       the types of variables of their data.
   */
  def presentWithValue(pos: S, t: T, ext: V): Option[Cell[Q]]

  /**
   * Operator for aggregating and then renaming dimensions.
   *
   * @param rename The rename to apply after the aggregation.
   *
   * @return An aggregator that runs `this` and then renames the resulting dimension(s).
   */
  def andThenRename(rename: Locate.FromOutcome[Q, Q]) = {
    new AggregatorWithValue[P, S, Q] {
      type T = self.T
      type V = self.V

      def prepareWithValue(cell: Cell[P], ext: V): T = self.prepareWithValue(cell, ext)
      def reduce(lt: T, rt: T): T = self.reduce(lt, rt)
      def presentWithValue(pos: S, t: T, ext: V): Option[Cell[Q]] = {
        self.presentWithValue(pos, t, ext).map { case c => Cell(rename(c), c.content) }
      }
    }
  }

  /**
   * Operator for aggregating and then expanding dimensions.
   *
   * @param expand The expansion to apply after the aggregation.
   *
   * @return An aggregator that runs `this` and then expands the resulting dimensions.
   */
  def andThenExpand[R <: Position](expand: Locate.FromOutcome[Q, R])(implicit ev: PosExpDep[Q, R]) = {
    new AggregatorWithValue[P, S, R] {
      type T = self.T
      type V = self.V

      def prepareWithValue(cell: Cell[P], ext: V): T = self.prepareWithValue(cell, ext)
      def reduce(lt: T, rt: T): T = self.reduce(lt, rt)
      def presentWithValue(pos: S, t: T, ext: V): Option[Cell[R]] = {
        self.presentWithValue(pos, t, ext).map { case c => Cell(expand(c), c.content) }
      }
    }
  }

  /**
   * Operator for aggregating and then renaming dimensions.
   *
   * @param rename The rename to apply after the aggregation.
   *
   * @return An aggregator that runs `this` and then renames the resulting dimension(s).
   */
  def andThenRenameWithValue(rename: Locate.FromOutcomeWithValue[Q, Q, V]) = {
    new AggregatorWithValue[P, S, Q] {
      type T = self.T
      type V = self.V

      def prepareWithValue(cell: Cell[P], ext: V): T = self.prepareWithValue(cell, ext)
      def reduce(lt: T, rt: T): T = self.reduce(lt, rt)
      def presentWithValue(pos: S, t: T, ext: V): Option[Cell[Q]] = {
        self.presentWithValue(pos, t, ext).map { case c => Cell(rename(c, ext), c.content) }
      }
    }
  }

  /**
   * Operator for aggregating and then expanding dimensions.
   *
   * @param expand The expansion to apply after the aggregation.
   *
   * @return An aggregator that runs `this` and then expands the resulting dimensions.
   */
  def andThenExpandWithValue[R <: Position](expand: Locate.FromOutcomeWithValue[Q, R, V])(
    implicit ev: PosExpDep[Q, R]) = {
    new AggregatorWithValue[P, S, R] {
      type T = self.T
      type V = self.V

      def prepareWithValue(cell: Cell[P], ext: V): T = self.prepareWithValue(cell, ext)
      def reduce(lt: T, rt: T): T = self.reduce(lt, rt)
      def presentWithValue(pos: S, t: T, ext: V): Option[Cell[R]] = {
        self.presentWithValue(pos, t, ext).map { case c => Cell(expand(c, ext), c.content) }
      }
    }
  }
}

/** Trait for transforming a type `T` to a `List[Aggregator[P, S, Q]]`. */
trait Aggregatable[P <: Position, S <: Position with ExpandablePosition, Q <: Position] extends java.io.Serializable {
  /** Returns a `List[Aggregator[P, S, Q]]` for this type `T`. */
  def apply(): List[Aggregator[P, S, Q]]
}

/** Companion object for the `Aggregatable` trait. */
object Aggregatable {
  /** Converts an `Aggregator[P, S, Q]` to a `List[Aggregator[P, S, Q]]`. */
  implicit def A2A[P <: Position, S <: Position with ExpandablePosition, Q <: Position](
    t: Aggregator[P, S, Q]): Aggregatable[P, S, Q] = {
    new Aggregatable[P, S, Q] { def apply(): List[Aggregator[P, S, Q]] = List(t) }
  }

  /** Converts a `List[Aggregator[P, S, Q]]` to a `List[Aggregator[P, S, Q]]`; that is, it's a pass through. */
  implicit def LA2A[P <: Position, S <: Position with ExpandablePosition, Q <: Position](
    t: List[Aggregator[P, S, Q]])(implicit ev: PosExpDep[S, Q]): Aggregatable[P, S, Q] = {
    new Aggregatable[P, S, Q] { def apply(): List[Aggregator[P, S, Q]] = t }
  }
}

/** Trait for transforming a type `T` to a 'List[AggregatorWithValue[P S, Q] { type V >: W }]`. */
trait AggregatableWithValue[P <: Position, S <: Position with ExpandablePosition, Q <: Position, W]
  extends java.io.Serializable {
  /** Returns a `List[AggregatorWithValue[P, S, Q] { type V >: W }]` for this type `T`. */
  def apply(): List[AggregatorWithValue[P, S, Q] { type V >: W }]
}

/** Companion object for the `AggregatableWithValue` trait. */
object AggregatableWithValue {
  /**
   * Converts an `AggregatorWithValue[P, S, Q] { type V >: W }` to a
   * `List[AggregatorWithValue[P, S, Q] { type V >: W }]`.
   */
  implicit def AWV2AWV[P <: Position, S <: Position with ExpandablePosition, Q <: Position, W](
    t: AggregatorWithValue[P, S, Q] { type V >: W }): AggregatableWithValue[P, S, Q, W] = {
    new AggregatableWithValue[P, S, Q, W] {
      def apply(): List[AggregatorWithValue[P, S, Q] { type V >: W }] = List(t)
    }
  }

  /**
   * Converts a `List[AggregatorWithValue[P, S, Q] { type V >: W }]` to a
   * `List[AggregatorWithValue[P, S, Q] { type V >: W }]`; that is, it is a pass through.
   */
  implicit def LAWV2AWV[P <: Position, S <: Position with ExpandablePosition, Q <: Position, W](
    t: List[AggregatorWithValue[P, S, Q] { type V >: W }])(
      implicit ev: PosExpDep[S, Q]): AggregatableWithValue[P, S, Q, W] = {
    new AggregatableWithValue[P, S, Q, W] {
      def apply(): List[AggregatorWithValue[P, S, Q] { type V >: W }] = t
    }
  }
}

