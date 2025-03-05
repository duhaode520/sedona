/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.spark.sql.sedona_sql.expressions.fhe

import org.apache.sedona.sql.utils.CipherGeometrySerializer
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenFallback
import org.apache.spark.sql.catalyst.expressions.{ExpectsInputTypes, Expression, NullIntolerant}
import org.apache.spark.sql.sedona_sql.UDT.CipherGeometryUDT
import org.apache.spark.sql.types.{AbstractDataType, BooleanType, DataType}
import org.apache.spark.sql.sedona_sql.expressions.InferrableFunctionConverter._
import org.apache.spark.sql.sedona_sql.expressions._
import org.ade.SpatialFHE.spatialfhe._
import org.apache.sedona.common.fhe.FHEPredicates

abstract class ST_Predicate_Private
    extends Expression
    with FoldableExpression
    with ExpectsInputTypes
    with NullIntolerant {

  def inputExpressions: Seq[Expression]

  override def toString: String = s" **${this.getClass.getName}**  "

  override def nullable: Boolean = children.exists(_.nullable)

  override def inputTypes: Seq[AbstractDataType] = Seq(CipherGeometryUDT, CipherGeometryUDT)

  override def dataType: DataType = BooleanType

  override def children: Seq[Expression] = inputExpressions

  override final def eval(inputRow: InternalRow): Any = {
    val leftArray = inputExpressions(0).eval(inputRow).asInstanceOf[Array[Byte]]
    if (leftArray == null) {
      null
    } else {
      val rightArray = inputExpressions(1).eval(inputRow).asInstanceOf[Array[Byte]]
      if (rightArray == null) {
        null
      } else {
        val leftGeometry = CipherGeometrySerializer.deserialize(leftArray)
        val rightGeometry = CipherGeometrySerializer.deserialize(rightArray)
        try {
          evalGeom(leftGeometry, rightGeometry)
        } catch {
          case e: Exception =>
            InferredExpression.throwExpressionInferenceException(
              getClass.getSimpleName,
              Seq(leftGeometry, rightGeometry),
              e)
        }
      }
    }
  }

  def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool
}

/**
 * Test if leftGeometry full contains rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Contains_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.contains(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry full intersects rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Intersects_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.intersects(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry is full within rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Within_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.within(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry covers rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Covers_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.covers(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry is covered by rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_CoveredBy_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.coveredBy(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry crosses rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Crosses_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.crosses(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry overlaps rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Overlaps_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.overlaps(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry touches rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Touches_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.touches(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry is equal to rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Equals_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.equals(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if leftGeometry is disjoint from rightGeometry with homomorphic encryption
 *
 * @param inputExpressions
 */
case class ST_Disjoint_Private(inputExpressions: Seq[Expression])
    extends ST_Predicate_Private
    with CodegenFallback {

  override def evalGeom(leftGeometry: TFHEGeometry, rightGeometry: TFHEGeometry): TFHEBool = {
    FHEPredicates.disjoint(leftGeometry, rightGeometry)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if geometry relation matches a given pattern with homomorphic encryption
 */
case class ST_Relate_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(inferrableFunction3(FHEPredicates.relate)) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if two DE-9IM intersection matrix patterns match with homomorphic encryption
 */
case class ST_RelateMatch_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(inferrableFunction2(FHEPredicates.relateMatch)) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}
