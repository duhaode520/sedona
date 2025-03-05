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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.ade.SpatialFHE.FHEHelper;
import org.ade.SpatialFHE.LibLoader;
import org.ade.SpatialFHE.spatialfhe.*;
import org.apache.sedona.common.fhe.FHEVectorTestBase;
import org.junit.Before;
import org.junit.Test;

public class CipherGeometryStreamTest extends FHEVectorTestBase {

  public CipherGeometryStreamTest() {}

  @Before
  @Override
  public void setup() {
    super.setup();
  }

  @Test
  public void testPutGetByte() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();
    out.putByte((byte) 42);

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);

    assertEquals((byte) 42, in.getByte(0));
  }

  @Test
  public void testPutGetBytes() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();
    out.putBytes(new byte[] {1, 2, 3, 4});

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);

    byte[] result = new byte[4];
    in.getBytes(result, 0, 4);

    assertArrayEquals(new byte[] {1, 2, 3, 4}, result);
  }

  @Test
  public void testPutGetInt() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();
    out.putInt(12345);

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);

    assertEquals(12345, in.getInt(0));
  }

  @Test
  public void testPutGetCoordinate() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();

    // 创建加密坐标
    TFHEInt32 x = new TFHEInt32(10);
    TFHEInt32 y = new TFHEInt32(20);
    TFHECoordinate coord = new TFHECoordinate(x, y);

    int size = out.putCoordinate(coord);

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);

    TFHECoordinateSequence result = in.getCoordinate(0);
    assertEquals(1, result.size());

    TFHECoordinate resultCoord = result.getAt(0);
    assertEquals(10, resultCoord.getX().decrypt());
    assertEquals(20, resultCoord.getY().decrypt());
  }

  @Test
  public void testPutGetCoordinates() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();

    // 创建坐标序列
    CoordinateVector coords = new CoordinateVector();
    coords.add(new TFHECoordinate(new TFHEInt32(10), new TFHEInt32(20)));
    coords.add(new TFHECoordinate(new TFHEInt32(30), new TFHEInt32(40)));
    TFHECoordinateSequence sequence = new TFHECoordinateSequence(coords);

    out.putCoordinates(sequence);

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);

    TFHECoordinateSequence result = in.getCoordinates(0, 2);
    assertEquals(2, result.size());

    TFHECoordinate coord1 = result.getAt(0);
    assertEquals(10, coord1.getX().decrypt());
    assertEquals(20, coord1.getY().decrypt());

    TFHECoordinate coord2 = result.getAt(1);
    assertEquals(30, coord2.getX().decrypt());
    assertEquals(40, coord2.getY().decrypt());
  }

  @Test
  public void testMixedDataTypes() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();

    // 写入混合类型数据
    out.putByte((byte) 5);
    out.putInt(12345);

    TFHEInt32 x = new TFHEInt32(100);
    TFHEInt32 y = new TFHEInt32(200);
    TFHECoordinate coord = new TFHECoordinate(x, y);
    out.putCoordinate(coord);

    out.putInt(67890);

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);

    // 读取第一个字节
    assertEquals((byte) 5, in.getByte(0));

    // 读取第一个整数
    assertEquals(12345, in.getInt(1));

    // 坐标开始的位置
    int coordPos = 5; // 1 byte + 4 bytes (int)

    // 读取最后一个整数的位置需要先获取坐标的大小
    int xSize = in.getInt(coordPos);
    int ySize = in.getInt(coordPos + 4);
    int lastIntPos = coordPos + 8 + xSize + ySize;
    assertEquals(67890, in.getInt(lastIntPos));

    // 读��坐标
    TFHECoordinateSequence resultSeq = in.getCoordinate(coordPos);
    TFHECoordinate resultCoord = resultSeq.getAt(0);
    assertEquals(100, resultCoord.getX().decrypt());
    assertEquals(200, resultCoord.getY().decrypt());
  }

  @Test
  public void testSlice() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();
    out.putBytes(new byte[] {1, 2, 3, 4});

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);

    // 测试从位置0切片
    CipherGeometryInputStream slice = in.slice(0);
    assertEquals(4, slice.getLength());
    assertEquals((byte) 1, slice.getByte(0));
    assertEquals((byte) 2, slice.getByte(1));
    assertEquals((byte) 3, slice.getByte(2));
    assertEquals((byte) 4, slice.getByte(3));

    // 测试从位置1切片
    slice = in.slice(1);
    assertEquals(3, slice.getLength());
    assertEquals((byte) 2, slice.getByte(0));
    assertEquals((byte) 3, slice.getByte(1));
    assertEquals((byte) 4, slice.getByte(2));

    // 测试从位置2切片
    slice = in.slice(2);
    assertEquals(2, slice.getLength());
    assertEquals((byte) 3, slice.getByte(0));
    assertEquals((byte) 4, slice.getByte(1));
  }

  @Test
  public void testMark() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream();
    out.putBytes(new byte[] {1, 2, 3, 4});
    out.mark(2);
    assertEquals(2, out.getMark());

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);
    in.mark(2);
    assertEquals(2, in.getMark());

    byte[] result = new byte[2];
    in.getBytes(result, 2, 2);
    assertArrayEquals(new byte[] {3, 4}, result);
  }

  @Test
  public void testStreamLength() throws IOException {
    CipherGeometryOutputStream out = new CipherGeometryOutputStream(10);
    out.putBytes(new byte[] {1, 2, 3, 4});
    assertEquals(4, out.getLength());

    byte[] bytes = out.toByteArray();
    CipherGeometryInputStream in = new CipherGeometryInputStream(bytes);
    assertEquals(4, in.getLength());
  }
}
