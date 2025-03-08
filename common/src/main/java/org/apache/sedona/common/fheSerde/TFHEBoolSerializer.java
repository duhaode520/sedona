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
package org.apache.sedona.common.fheSerde;

import org.ade.SpatialFHE.spatialfhe.ByteVector;
import org.ade.SpatialFHE.spatialfhe.TFHEBool;

public class TFHEBoolSerializer {

  /**
   * Serialize TFHEBool object to byte array
   *
   * @param tfheBool the TFHEBool object
   * @return serialized byte array
   */
  public static byte[] serialize(TFHEBool tfheBool) {
    if (tfheBool == null) {
      return new byte[0];
    }

    // 调用TFHEBool的序列化方法
    ByteVector serializedData = TFHEBool.serialize(tfheBool);
    byte[] result = new byte[serializedData.size()];

    for (int i = 0; i < serializedData.size(); i++) {
      result[i] = serializedData.get(i).byteValue();
    }

    return result;
  }

  /**
   * Deserialize byte array to TFHEBool object
   *
   * @param bytes serialized TFHEBool
   * @return TFHEBool object
   */
  public static TFHEBool deserialize(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return new TFHEBool(); // 返回一个默认的空对象
    }

    // 将byte[]转换为ArrayList<Byte>
    ByteVector byteList = new ByteVector();
    for (byte b : bytes) {
      byteList.add((short) b);
    }

    // 调用TFHEBool的反序列化方法
    return TFHEBool.deserialize(byteList);
  }
}
