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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.ade.SpatialFHE.spatialfhe.TFHEBool;
import org.ade.SpatialFHE.spatialfhe.TFHEGeometry;
import org.ade.SpatialFHE.spatialfhe.WKTReader;
import org.junit.Before;
import org.junit.Test;

public class FHEPredicatesTest extends FHEVectorTestBase {

    private WKTReader reader;

    @Override
    @Before
    public void setup() {
        super.setup();
        reader = new WKTReader();
    }

    private TFHEGeometry createTFHEGeometry(String wkt) {
        try {
            return reader.read(wkt);
        } catch (Exception e) {
            throw new RuntimeException("Error creating TFHEGeometry from WKT", e);
        }
    }

    @Test
    public void testIntersects() {
        // Test A: 点在多边形内部 - 预期相交为真
        System.out.println("Test A: 点(100,100)在多边形内部 - 预期相交为true");
        String pointWkt = "POINT (100 100)";
        String polygonWkt = "POLYGON ((0 1000, 1000 1000, 1000 0, 0 0, 0 1000))";
        TFHEGeometry point = createTFHEGeometry(pointWkt);
        TFHEGeometry polygon = createTFHEGeometry(polygonWkt);

        TFHEBool result = FHEPredicates.intersects(polygon, point);
        assertTrue(result.decrypt());

        // Test B: 不相交的线段 - 预期相交为假
        System.out.println("Test B: 两条不相交的线段 - 预期相交为false");
        String line1Wkt = "LINESTRING (0 0, 900 900)";
        String line2Wkt = "LINESTRING(1000 1900, 1900 1000)";
        TFHEGeometry line1 = createTFHEGeometry(line1Wkt);
        TFHEGeometry line2 = createTFHEGeometry(line2Wkt);

        result = FHEPredicates.intersects(line1, line2);
        assertFalse(result.decrypt());

        System.out.println("Test C: 两条相交的线段 - 预期相交为true");
        String line3Wkt = "LINESTRING (0 0, 900 900)";
        String line4Wkt = "LINESTRING(0 900, 900 0)";
        TFHEGeometry line3 = createTFHEGeometry(line3Wkt);
        TFHEGeometry line4 = createTFHEGeometry(line4Wkt);

        result = FHEPredicates.intersects(line3, line4);
        assertTrue(result.decrypt());
    }

    @Test
    public void testDisjoint() {
        // Test A: 不相交的线段 - 预期不相交为真
        System.out.println("Test A: 两条不相交的线段 - 预期不相交为true");
        String line1Wkt = "LINESTRING (0 0, 900 900)";
        String line2Wkt = "LINESTRING(1000 1900, 1900 1000)";
        TFHEGeometry line1 = createTFHEGeometry(line1Wkt);
        TFHEGeometry line2 = createTFHEGeometry(line2Wkt);

        TFHEBool result = FHEPredicates.disjoint(line1, line2);
        assertTrue(result.decrypt());

        // Test B: 相交的线段 - 预期不相交为假
        System.out.println("Test B: 两条相交的线段 - 预期不相交为false");
        String line3Wkt = "LINESTRING (0 0, 900 900)";
        String line4Wkt = "LINESTRING(0 900, 900 0)";
        TFHEGeometry line3 = createTFHEGeometry(line3Wkt);
        TFHEGeometry line4 = createTFHEGeometry(line4Wkt);

        result = FHEPredicates.disjoint(line3, line4);
        assertFalse(result.decrypt());
    }

    @Test
    public void testContains() {
        // Test A: 多边形包含点 - 预期包含为真
        System.out.println("Test A: 多边形包含点(100,100) - 预期包含为true");
        String pointWkt = "POINT (100 100)";
        String polygonWkt = "POLYGON ((0 1000, 1000 1000, 1000 0, 0 0, 0 1000))";
        TFHEGeometry point = createTFHEGeometry(pointWkt);
        TFHEGeometry polygon = createTFHEGeometry(polygonWkt);

        TFHEBool result = FHEPredicates.contains(polygon, point);
        assertTrue(result.decrypt());

        // Test B: 多边形包含线 - 预期包含为真
        System.out.println("Test B: 多边形包含线段 - 预期包含为true");
        String lineWkt = "LINESTRING (100 800, 300 500, 500 800)";
        TFHEGeometry line = createTFHEGeometry(lineWkt);

        result = FHEPredicates.contains(polygon, line);
        assertTrue(result.decrypt());
    }

    @Test
    public void testWithin() {
        // Test A: 点在多边形内部 - 预期within为真
        System.out.println("Test A: 点(100,100)在多边形内部 - 预期within为true");
        String pointWkt = "POINT (100 100)";
        String polygonWkt = "POLYGON ((0 1000, 1000 1000, 1000 0, 0 0, 0 1000))";
        TFHEGeometry point = createTFHEGeometry(pointWkt);
        TFHEGeometry polygon = createTFHEGeometry(polygonWkt);

        TFHEBool result = FHEPredicates.within(point, polygon);
        assertTrue(result.decrypt());

        // Test B: 内部多边形在外部多边形内 - 预期within为真
        System.out.println("Test B: 内部多边形在外部多边形内 - 预期within为true");
        String polygon1Wkt = "POLYGON ((100 900, 900 900, 900 100, 100 100, 100 900))";
        String polygon2Wkt = "POLYGON ((200 800, 800 800, 800 200, 200 200, 200 800))";
        TFHEGeometry polygon1 = createTFHEGeometry(polygon1Wkt);
        TFHEGeometry polygon2 = createTFHEGeometry(polygon2Wkt);

        result = FHEPredicates.within(polygon2, polygon1);
        assertTrue(result.decrypt());
    }

    @Test
    public void testCovers() {
        // Test A: 多边形覆盖边界上的点 - 预期covers为真
        System.out.println("Test A: 多边形覆盖边界上的点(100,0) - 预期covers为true");
        String pointWkt = "POINT (100 0)";
        String polygonWkt = "POLYGON ((1000 0, 0 0, 0 1000, 1000 0))";
        TFHEGeometry point = createTFHEGeometry(pointWkt);
        TFHEGeometry polygon = createTFHEGeometry(polygonWkt);

        TFHEBool result = FHEPredicates.covers(polygon, point);
        assertTrue(result.decrypt());

        // Test B: 外部多边形覆盖内部多边形 - 预期covers为真
        System.out.println("Test B: 外部多边形覆盖内部多边形 - 预期covers为true");
        String polygon1Wkt = "POLYGON ((100 900, 900 900, 900 100, 100 100, 100 900))";
        String polygon2Wkt = "POLYGON ((200 800, 800 800, 800 200, 200 200, 200 800))";
        TFHEGeometry polygon1 = createTFHEGeometry(polygon1Wkt);
        TFHEGeometry polygon2 = createTFHEGeometry(polygon2Wkt);

        result = FHEPredicates.covers(polygon1, polygon2);
        assertTrue(result.decrypt());
    }

    @Test
    public void testCoveredBy() {
        // Test B: 内部多边形被外部多边形覆盖 - 预期coveredBy为真
        System.out.println("Test B: 内部多边形被外部多边形覆盖 - 预期coveredBy为true");
        String polygon1Wkt = "POLYGON ((100 900, 900 900, 900 100, 100 100, 100 900))";
        String polygon2Wkt = "POLYGON ((200 800, 800 800, 800 200, 200 200, 200 800))";
        TFHEGeometry polygon1 = createTFHEGeometry(polygon1Wkt);
        TFHEGeometry polygon2 = createTFHEGeometry(polygon2Wkt);

        TFHEBool result = FHEPredicates.coveredBy(polygon2, polygon1);
        assertTrue(result.decrypt());

        // Test A: 点被多边形边界覆盖 - 预期coveredBy为真
        System.out.println("Test A: 点(100,0)被多边形边界覆盖 - 预期coveredBy为true");
        String pointWkt = "POINT (100 0)";
        String polygonWkt = "POLYGON ((1000 0, 0 0, 0 1000, 1000 0))";
        TFHEGeometry point = createTFHEGeometry(pointWkt);
        TFHEGeometry polygon = createTFHEGeometry(polygonWkt);

        result = FHEPredicates.coveredBy(point, polygon);
        assertTrue(result.decrypt());

    }

    @Test
    public void testCrosses() {
        // Test A: 两条线相交 - 预期crosses为真
        System.out.println("Test A: 两条线X型相交 - 预期crosses为true");
        String line1Wkt = "LINESTRING (0 0, 900 900)";
        String line2Wkt = "LINESTRING(0 900, 900 0)";
        TFHEGeometry line1 = createTFHEGeometry(line1Wkt);
        TFHEGeometry line2 = createTFHEGeometry(line2Wkt);

        TFHEBool result = FHEPredicates.crosses(line1, line2);
        assertTrue(result.decrypt());

        // Test B: 多点与线交叉 - 预期crosses为真
        System.out.println("Test B: 多点与线交叉 - 预期crosses为true");
        String multipointWkt = "MULTIPOINT((0 0), (2 2))";
        String linestringWkt = "LINESTRING(-1 -1, 1 1)";
        TFHEGeometry multipoint = createTFHEGeometry(multipointWkt);
        TFHEGeometry linestring = createTFHEGeometry(linestringWkt);

        result = FHEPredicates.crosses(multipoint, linestring);
        assertTrue(result.decrypt());
    }

    @Test
    public void testOverlaps() {
        // Test A: 两条线部分重叠 - 预期overlaps为真
        System.out.println("Test A: 两条线部分重叠 - 预期overlaps为true");
        String line1Wkt = "LINESTRING (0 0, 500 500)";
        String line2Wkt = "LINESTRING(300 300, 900 900)";
        TFHEGeometry line1 = createTFHEGeometry(line1Wkt);
        TFHEGeometry line2 = createTFHEGeometry(line2Wkt);

        TFHEBool result = FHEPredicates.overlaps(line1, line2);
        assertTrue(result.decrypt());

        // Test B: 两个多边形部分重叠 - 预期overlaps为真
        System.out.println("Test B: 两个多边形部分重叠 - 预期overlaps为true");
        String polygon1Wkt = "POLYGON ((100 100, 100 700, 700 700, 700 100, 100 100))";
        String polygon2Wkt = "POLYGON ((200 800, 800 800, 800 200, 200 200, 200 800))";
        TFHEGeometry polygon1 = createTFHEGeometry(polygon1Wkt);
        TFHEGeometry polygon2 = createTFHEGeometry(polygon2Wkt);

        result = FHEPredicates.overlaps(polygon1, polygon2);
        assertTrue(result.decrypt());
    }

    @Test
    public void testTouches() {
        // Test A: 两条线在顶点接触 - 预期touches为真
        System.out.println("Test A: 两条线在顶点(500,500)接触 - 预期touches为true");
        String line1Wkt = "LINESTRING (500 500, 100 800)";
        String line2Wkt = "LINESTRING (500 500, 900 500)";
        TFHEGeometry line1 = createTFHEGeometry(line1Wkt);
        TFHEGeometry line2 = createTFHEGeometry(line2Wkt);

        TFHEBool result = FHEPredicates.touches(line1, line2);
        assertTrue(result.decrypt());

        // Test B: 两个多边形边缘相接 - 预期touches为真
        System.out.println("Test B: 两个多边形边缘相接 - 预期touches为true");
        String polygon1Wkt = "POLYGON ((100 300, 300 300, 300 100, 100 100, 100 300))";
        String polygon2Wkt = "POLYGON ((500 300, 500 100, 300 100, 300 300, 500 300))";
        TFHEGeometry polygon1 = createTFHEGeometry(polygon1Wkt);
        TFHEGeometry polygon2 = createTFHEGeometry(polygon2Wkt);

        result = FHEPredicates.touches(polygon1, polygon2);
        assertTrue(result.decrypt());
    }

    @Test
    public void testEquals() {
        // Test A: 两点坐标相同 - 预期equals为真
        System.out.println("Test A: 两点坐标相同(0,0) - 预期equals为true");
        String point1Wkt = "POINT (0 0)";
        String point2Wkt = "POINT (0 0)";
        TFHEGeometry point1 = createTFHEGeometry(point1Wkt);
        TFHEGeometry point2 = createTFHEGeometry(point2Wkt);

        TFHEBool result = FHEPredicates.equals(point1, point2);
        assertTrue(result.decrypt());

        // Test B: 两线几何相等但顶点不同 - 预期equals为真
        System.out.println("Test B: 两线几何相等但顶点数不同 - 预期equals为true");
        String line1Wkt = "LINESTRING (0 0, 2 2)";
        String line2Wkt = "LINESTRING (0 0, 1 1, 2 2)";
        TFHEGeometry line1 = createTFHEGeometry(line1Wkt);
        TFHEGeometry line2 = createTFHEGeometry(line2Wkt);

        result = FHEPredicates.equals(line1, line2);
        assertTrue(result.decrypt());
    }

    @Test
    public void testRelate() {
        // Test A: 两点不相交 - 预期relate匹配为真
        System.out.println("Test A: 两点不相交 - 预期DE-9IM矩阵(FF0FFF0F2)匹配为true");
        String point1Wkt = "POINT (0 0)";
        String point2Wkt = "POINT (10 10)";
        TFHEGeometry point1 = createTFHEGeometry(point1Wkt);
        TFHEGeometry point2 = createTFHEGeometry(point2Wkt);

        boolean result = FHEPredicates.relate(point1, point2, "FF0FFF0F2");
        assertTrue(result);

        // Test B: 线与多边形的特定空间关系 - 预期relate匹配为真
        System.out.println("Test B: 线与多边形的特定空间关系 - 预期DE-9IM矩阵(1010F0212)匹配为true");
        String lineWkt = "LINESTRING (1 1, 5 5)";
        String polygonWkt = "POLYGON ((3 3, 3 7, 7 7, 7 3, 3 3))";
        TFHEGeometry line = createTFHEGeometry(lineWkt);
        TFHEGeometry polygon = createTFHEGeometry(polygonWkt);

        result = FHEPredicates.relate(line, polygon, "1010F0212");
        assertTrue(result);
    }

    @Test
    public void testRelateMatch() {
        // Test A: DE-9IM矩阵匹配 - 预期匹配为真
        System.out.println("Test A: DE-9IM矩阵101202FFF与TTTTTTFFF - 预期匹配为true");
        boolean result = FHEPredicates.relateMatch("101202FFF", "TTTTTTFFF");
        assertTrue(result);

        // Test B: DE-9IM矩阵不匹配 - 预期匹配为假
        System.out.println("Test B: DE-9IM矩阵101202FFF与TTTFTFFFF - 预期匹配为false");
        result = FHEPredicates.relateMatch("101202FFF", "TTTFTFFFF");
        assertFalse(result);
    }
}
