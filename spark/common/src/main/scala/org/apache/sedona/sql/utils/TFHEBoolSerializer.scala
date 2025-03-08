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
package org.apache.sedona.sql.utils

import org.ade.SpatialFHE.spatialfhe.TFHEBool
import org.apache.sedona.common.fheSerde.{TFHEBoolSerializer => JavaSerializer}

object TFHEBoolSerializer {

  /**
   * Serialize TFHEBool object to byte array
   * @param tfheBool
   *   the TFHEBool object
   * @return
   *   serialized byte array
   */
  def serialize(tfheBool: TFHEBool): Array[Byte] = {
    if (tfheBool == null) {
      Array.empty[Byte]
    } else {
      JavaSerializer.serialize(tfheBool)
    }
  }

  /**
   * Deserialize byte array to TFHEBool object
   * @param bytes
   *   serialized TFHEBool
   * @return
   *   TFHEBool object
   */
  def deserialize(bytes: Array[Byte]): TFHEBool = {
    if (bytes == null || bytes.isEmpty) {
      new TFHEBool() // 返回一个默认的空对象
    } else {
      JavaSerializer.deserialize(bytes)
    }
  }
}
