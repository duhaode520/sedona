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
package org.apache.sedona.common.fhe;

import org.ade.SpatialFHE.FHEHelper;
import org.ade.SpatialFHE.spatialfhe.HECrypto;
import org.junit.Before;

public abstract class FHEVectorTestBase {
  protected static final String resourceFolder =
      System.getProperty("user.dir") + "/../spark/common/src/test/resources/";
  String fhelibPath =
      "/home/ubuntu/projects/SpatialFHE/build/SpatialFHE/java/SpatialFHE-linux-x86-64/src/main/resources/SpatialFHE-linux-x86-64/libjniSpatialFHE.so";
  String fheJsonConfig =
      "{"
          + "\"SchemeType\": \"CKKS\","
          + "\"PolyModulusDegree\": 8192,"
          + "\"PlaintextModulus\": 0,"
          + "\"CoeffModulusPrimes\": [],"
          + "\"CoeffModulusBits\": [60, 30, 30, 30, 60],"
          + "\"ScaleFactor\": 30"
          + "}";

  @Before
  public void setup() {
    FHEHelper.getOrCreate(
        resourceFolder + "tmp/public.key",
        resourceFolder + "tmp/private.key",
        fhelibPath,
        fheJsonConfig,
        "127.0.0.1:8080",
        HECrypto.HELibrary.Phantom,
        true,
        2);
  }
}
