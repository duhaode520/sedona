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

import org.apache.sedona.common.fhe.FHEConstructors
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.sedona_sql.expressions.{InferredExpression, UserDataGeneratator}
import org.apache.spark.sql.sedona_sql.expressions.InferrableFunctionConverter._

/**
 * Return a FHE Geometry from a WKT string
 *
 * @param inputExpressions
 *   This function takes a geometry string. The string format must be WKT.
 */
case class ST_GeomFromWKT_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEConstructors.geomFromWKT _) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Return a FHE Point from X and Y
 *
 * @param inputExpressions
 *   This function takes 2 parameter which are point x, y.
 */
case class ST_Point_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEConstructors.point _) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Return a FHE polygon given minX,minY,maxX,maxY
 *
 * @param inputExpressions
 */
case class ST_PolygonFromEnvelope_Private(inputExpressions: Seq[Expression])
    extends InferredExpression(FHEConstructors.polygonFromEnvelope _) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}
