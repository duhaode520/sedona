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

import org.apache.sedona.common.fhe.FHEMapAlgebra
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.sedona_sql.expressions.InferrableFunctionConverter._
import org.apache.spark.sql.sedona_sql.expressions.InferrableCipherMatTypes._
import org.apache.spark.sql.sedona_sql.expressions.InferrableRasterTypes._
import org.apache.spark.sql.sedona_sql.expressions.InferredExpression

// Add two CipherMaps
case class RS_Add_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEMapAlgebra.addPrivate _) {
  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class RS_Add_PrivatePlain(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEMapAlgebra.addPrivatePlain _) {
  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class RS_Subtract_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEMapAlgebra.subtractPrivate _) {
  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class RS_Multiply_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEMapAlgebra.multiplyPrivate _) {
  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class RS_Multiply_PrivatePlain(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEMapAlgebra.multiplyPrivatePlain _) {
  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class RS_MultiplyFactor_PrivatePlain(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEMapAlgebra.multiplyFactorPrivatePlain _) {
  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class RS_BandAsCipherMat(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEMapAlgebra.bandAsCipherMat _) {
  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}
