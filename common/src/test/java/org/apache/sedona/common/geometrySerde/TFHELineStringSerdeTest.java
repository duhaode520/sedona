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

public class TFHELineStringSerdeTest extends FHEVectorTestBase {
  private static TFHEGeometryFactory factory;

  @Before
  @Override
  public void setup() {
    super.setup();
    factory = TFHEGeometryFactory.getDefaultInstance();
  }

  @Test
  public void testEmptyLineString() {
    TFHELineString lineString = factory.createLineString();
    byte[] bytes = CipherGeometrySerializer.serialize(lineString);
    TFHEGeometry geom = CipherGeometrySerializer.deserialize(bytes);
    Assert.assertTrue(geom instanceof TFHELineString);
    Assert.assertTrue(((TFHELineString) geom).isEmpty());
  }

  @Test
  public void testNonEmptyLineString() {
    // 创建坐标数组
    CoordinateVector coordinates = new CoordinateVector();
    coordinates.add(new TFHECoordinate(new TFHEInt32(1), new TFHEInt32(2)));
    coordinates.add(new TFHECoordinate(new TFHEInt32(3), new TFHEInt32(4)));
    coordinates.add(new TFHECoordinate(new TFHEInt32(5), new TFHEInt32(6)));

    TFHECoordinateSequence coordSeq = new TFHECoordinateSequence();
    for (int i = 0; i < 3; i++) {
      coordSeq.add(coordinates.get(i));
    }

    TFHELineString lineString = factory.createLineString(coordSeq);

    byte[] bytes = CipherGeometrySerializer.serialize(lineString);
    TFHEGeometry geom = CipherGeometrySerializer.deserialize(bytes);

    Assert.assertTrue(geom instanceof TFHELineString);
    Assert.assertFalse(((TFHELineString) geom).isEmpty());

    TFHELineString deserializedLineString = (TFHELineString) geom;
    Assert.assertEquals(3, deserializedLineString.getNumPoints());
  }
}
