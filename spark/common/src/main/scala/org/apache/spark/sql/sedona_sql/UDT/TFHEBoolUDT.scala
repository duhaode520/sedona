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
package org.apache.spark.sql.sedona_sql.UDT

import org.ade.SpatialFHE.spatialfhe.TFHEBool
import org.apache.sedona.sql.utils.TFHEBoolSerializer
import org.apache.spark.sql.types._

class TFHEBoolUDT extends UserDefinedType[TFHEBool] {

  override def sqlType: DataType = BinaryType

  override def pyUDT: String = "sedona.sql.types.TFHEBoolType"

  override def userClass: Class[TFHEBool] = classOf[TFHEBool]

  override def serialize(obj: TFHEBool): Array[Byte] = TFHEBoolSerializer.serialize(obj)

  override def deserialize(datum: Any): TFHEBool = {
    datum match {
      case value: Array[Byte] => TFHEBoolSerializer.deserialize(value)
    }
  }

  override def equals(other: Any): Boolean = other match {
    case _: UserDefinedType[_] => other.isInstanceOf[TFHEBoolUDT]
    case _ => false
  }

  override def hashCode(): Int = userClass.hashCode()
}

case object TFHEBoolUDT extends TFHEBoolUDT with Serializable
