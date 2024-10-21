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

import org.apache.spark.sql.types.{BinaryType, DataType, UserDefinedType}
import org.ade.SpatialFHE.spatialfhe.CipherMat
import org.apache.sedona.sql.utils.CipherMatSerializer

class CipherMatUDT extends UserDefinedType[CipherMat] {

  override def sqlType: DataType = BinaryType

  override def pyUDT: String = "org.ade.SpatialFHE.spatialfhe.CipherMat"

  override def defaultSize: Int = 256 * 1024

  override def serialize(cipherMat: CipherMat): Any = CipherMatSerializer.serialize(cipherMat)

  override def deserialize(datum: Any): CipherMat = {
    datum match {
      case value: Array[Byte] => CipherMatSerializer.deserialize(value)
    }
  }

  override def userClass: Class[CipherMat] = classOf[CipherMat]

  override def equals(other: Any): Boolean = other match {
    case _: UserDefinedType[_] => other.isInstanceOf[CipherMat]
    case _ => false
  }

  override def hashCode(): Int = userClass.hashCode()
}

case object CipherMatUDT extends CipherMatUDT with Serializable
