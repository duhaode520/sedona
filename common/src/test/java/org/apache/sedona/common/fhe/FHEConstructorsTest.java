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

import static org.junit.Assert.*;

import org.ade.SpatialFHE.spatialfhe.*;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;

public class FHEConstructorsTest extends FHEVectorTestBase {

  @Test
  public void geomFromWKT() throws ParseException {
    // 测试输入为null的情况
    assertNull(FHEConstructors.geomFromWKT(null));

    //    // 测试有效的点
    //    TFHEPoint geom = FHEConstructors.geomFromWKT("POINT (1 1)").asPoint();
    //    assertNotNull(geom);
    //    assertEquals(1, geom.getX().decrypt());
    //    assertEquals(1, geom.getY().decrypt());
    //
    //    // 测试有效的多边形
    //    TFHEGeometry poly = FHEConstructors.geomFromWKT("POLYGON ((0 0, 1 0, 1 1, 0 1, 0
    // 0))").asPolygon();
    //    assertNotNull(poly);

    // 测试无效的WKT格式
    try {
      FHEConstructors.geomFromWKT("not valid");
      fail("应当抛出ParseException");
    } catch (RuntimeException e) {
      // 预期会抛出异常
      assertTrue(e.getMessage().contains("Unknown type"));
    }
  }

  @Test
  public void point() {
    // 测试创建点
    TFHEGeometry point = FHEConstructors.point(1.0d, 2.0d);

    assertNotNull(point);
    assertTrue(point instanceof TFHEPoint);
    assertEquals(1, ((TFHEPoint) point).getX().decrypt());
    assertEquals(2, ((TFHEPoint) point).getY().decrypt());

    // 测试负值坐标
    TFHEGeometry negPoint = FHEConstructors.point(-10d, -20d);
    assertNotNull(negPoint);
    assertEquals(-10, ((TFHEPoint) negPoint).getX().decrypt());
    assertEquals(-20, ((TFHEPoint) negPoint).getY().decrypt());
  }

  @Test
  public void polygonFromEnvelope() {
    // 测试创建一个矩形多边形
    TFHEGeometry poly = FHEConstructors.polygonFromEnvelope(0.0d, 0.0d, 10.0d, 10.0d);

    assertNotNull(poly);
    assertTrue(poly instanceof TFHEPolygon);

    TFHEPolygon polygon = (TFHEPolygon) poly;
    TFHECoordinateSequence shell = polygon.getExteriorRing().getCoordinatesRO();

    // 验证多边形的五个点(四个角加上闭合点)
    assertEquals(5, shell.size());

    // 验证各个坐标点
    assertEquals(0, shell.getAt(0).getX().decrypt());
    assertEquals(0, shell.getAt(0).getY().decrypt());

    assertEquals(0, shell.getAt(1).getX().decrypt());
    assertEquals(10, shell.getAt(1).getY().decrypt());

    assertEquals(10, shell.getAt(2).getX().decrypt());
    assertEquals(10, shell.getAt(2).getY().decrypt());

    assertEquals(10, shell.getAt(3).getX().decrypt());
    assertEquals(0, shell.getAt(3).getY().decrypt());

    // 闭合点应与起点相同
    assertEquals(shell.getAt(0).getX().decrypt(), shell.getAt(4).getX().decrypt());
    assertEquals(shell.getAt(0).getY().decrypt(), shell.getAt(4).getY().decrypt());

    // 测试负值坐标
    TFHEGeometry negPoly = FHEConstructors.polygonFromEnvelope(-10.0d, -10.0d, 10.0d, 10.0d);
    assertNotNull(negPoly);
    assertTrue(negPoly instanceof TFHEPolygon);

    TFHEPolygon negPolygon = (TFHEPolygon) negPoly;
    TFHECoordinateSequence negShell = negPolygon.getExteriorRing().getCoordinatesRO();

    // 验证负坐标
    assertEquals(-10, negShell.getAt(0).getX().decrypt());
    assertEquals(-10, negShell.getAt(0).getY().decrypt());
  }
}
