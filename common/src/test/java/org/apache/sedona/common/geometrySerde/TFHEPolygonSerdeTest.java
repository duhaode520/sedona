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

public class TFHEPolygonSerdeTest extends FHEVectorTestBase {
  private static TFHEGeometryFactory factory;

  @Before
  @Override
  public void setup() {
    super.setup();
    factory = TFHEGeometryFactory.getDefaultInstance();
  }

  @Test
  public void testEmptyPolygon() {
    TFHEPolygon polygon = factory.createPolygon();
    byte[] bytes = CipherGeometrySerializer.serialize(polygon);
    TFHEGeometry geom = CipherGeometrySerializer.deserialize(bytes);
    Assert.assertTrue(geom instanceof TFHEPolygon);
    Assert.assertTrue(((TFHEPolygon) geom).isEmpty());
  }

  @Test
  public void testPolygonWithoutHoles() {
    // 创建外环坐标
    TFHECoordinateSequence shellSeq = new TFHECoordinateSequence();
    shellSeq.add(new TFHECoordinate(new TFHEInt32(0), new TFHEInt32(0)));
    shellSeq.add(new TFHECoordinate(new TFHEInt32(0), new TFHEInt32(1)));
    shellSeq.add(new TFHECoordinate(new TFHEInt32(1), new TFHEInt32(1)));
    shellSeq.add(new TFHECoordinate(new TFHEInt32(1), new TFHEInt32(0)));
    shellSeq.add(new TFHECoordinate(new TFHEInt32(0), new TFHEInt32(0))); // 闭合环

    TFHELinearRing shell = factory.createLinearRing(shellSeq);
    TFHEPolygon polygon = factory.createPolygon(shell);

    byte[] bytes = CipherGeometrySerializer.serialize(polygon);
    TFHEGeometry geom = CipherGeometrySerializer.deserialize(bytes);

    Assert.assertTrue(geom instanceof TFHEPolygon);
    Assert.assertFalse(((TFHEPolygon) geom).isEmpty());

    TFHEPolygon deserializedPolygon = (TFHEPolygon) geom;
    Assert.assertEquals(5, deserializedPolygon.getExteriorRing().getNumPoints());
    Assert.assertEquals(0, deserializedPolygon.getNumInteriorRing());
  }

  @Test
  public void testPolygonWithHoles() {
    // 创建外环
    TFHECoordinateSequence shellCoords = new TFHECoordinateSequence();
    shellCoords.add(new TFHECoordinate(new TFHEInt32(0), new TFHEInt32(0)));
    shellCoords.add(new TFHECoordinate(new TFHEInt32(0), new TFHEInt32(10)));
    shellCoords.add(new TFHECoordinate(new TFHEInt32(10), new TFHEInt32(10)));
    shellCoords.add(new TFHECoordinate(new TFHEInt32(10), new TFHEInt32(0)));
    shellCoords.add(new TFHECoordinate(new TFHEInt32(0), new TFHEInt32(0)));

    TFHELinearRing shell = factory.createLinearRing(shellCoords);

    // 创建内环
    TFHECoordinateSequence hole1Coords = new TFHECoordinateSequence();
    hole1Coords.add(new TFHECoordinate(new TFHEInt32(1), new TFHEInt32(1)));
    hole1Coords.add(new TFHECoordinate(new TFHEInt32(1), new TFHEInt32(2)));
    hole1Coords.add(new TFHECoordinate(new TFHEInt32(2), new TFHEInt32(2)));
    hole1Coords.add(new TFHECoordinate(new TFHEInt32(2), new TFHEInt32(1)));
    hole1Coords.add(new TFHECoordinate(new TFHEInt32(1), new TFHEInt32(1)));

    TFHELinearRing hole1 = factory.createLinearRing((hole1Coords));

    // 创建内环数组
    LinearRingVector holes = new LinearRingVector();
    holes.add(hole1);

    TFHEPolygon polygon = factory.createPolygon(shell, holes);

    byte[] bytes = CipherGeometrySerializer.serialize(polygon);
    TFHEGeometry geom = CipherGeometrySerializer.deserialize(bytes);

    Assert.assertTrue(geom instanceof TFHEPolygon);

    TFHEPolygon deserializedPolygon = (TFHEPolygon) geom;
    Assert.assertEquals(5, deserializedPolygon.getExteriorRing().getNumPoints());
    Assert.assertEquals(1, deserializedPolygon.getNumInteriorRing());
    Assert.assertEquals(5, deserializedPolygon.getInteriorRingN(0).getNumPoints());
  }
}
