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
package org.apache.sedona.common.geometrySerde;

import org.ade.SpatialFHE.spatialfhe.*;
import org.apache.sedona.common.fhe.FHEVectorTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TFHEPointSerdeTest extends FHEVectorTestBase {
  private static TFHEGeometryFactory factory;

  @Before
  @Override
  public void setup() {
    super.setup();
    factory = TFHEGeometryFactory.getDefaultInstance();
  }

  @Test
  public void testEmptyPoint() {
    TFHEPoint point = factory.createPoint();
    byte[] bytes = CipherGeometrySerializer.serialize(point);
    TFHEGeometry geom = CipherGeometrySerializer.deserialize(bytes);
    Assert.assertTrue(geom instanceof TFHEPoint);
    Assert.assertTrue(((TFHEPoint) geom).isEmpty());
  }

  @Test
  public void testNonEmptyPoint() {
    // 创建加密坐标
    TFHECoordinate coordinate = new TFHECoordinate(new TFHEInt32(1), new TFHEInt32(2));
    TFHEPoint point = factory.createPoint(coordinate);
    byte[] bytes = CipherGeometrySerializer.serialize(point);
    TFHEGeometry geom = CipherGeometrySerializer.deserialize(bytes);

    Assert.assertTrue(geom instanceof TFHEPoint);
    Assert.assertFalse(((TFHEPoint) geom).isEmpty());

    // 对于加密几何体，我们无法直接比较坐标值，但可以检查点是否成功序列化和反序列化
    TFHEPoint deserializedPoint = (TFHEPoint) geom;
    Assert.assertNotNull(deserializedPoint.getCoordinate());
  }
}
