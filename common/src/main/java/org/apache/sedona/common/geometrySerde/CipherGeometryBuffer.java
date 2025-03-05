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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.ade.SpatialFHE.spatialfhe.*;

public class CipherGeometryBuffer {
  private final ByteBuffer byteBuffer;
  private int markOffset = 0;

  public CipherGeometryBuffer(int bufferSize) {
    byteBuffer = ByteBuffer.allocate(bufferSize);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
  }

  public CipherGeometryBuffer(byte[] bytes) {
    byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());
  }

  public CipherGeometryBuffer(ByteBuffer byteBuffer) {
    this.byteBuffer = byteBuffer.order(ByteOrder.nativeOrder());
  }

  public int getLength() {
    return byteBuffer.capacity();
  }

  public void mark(int offset) {
    markOffset = offset;
  }

  public int getMark() {
    return markOffset;
  }

  public void putByte(int offset, byte value) {
    byteBuffer.put(offset, value);
  }

  public byte getByte(int offset) {
    return byteBuffer.get(offset);
  }

  public void putBytes(int offset, byte[] bytes) {
    byteBuffer.position(offset);
    byteBuffer.put(bytes, 0, bytes.length);
  }

  public void getBytes(byte[] bytes, int offset, int length) {
    byteBuffer.position(offset);
    byteBuffer.get(bytes, 0, length);
  }

  public void putInt(int offset, int value) {
    byteBuffer.putInt(offset, value);
  }

  public int getInt(int offset) {
    return byteBuffer.getInt(offset);
  }

  public int putCoordinate(int offset, TFHECoordinate coordinate) {
    ByteVector serX = TFHEInt32.serialize(coordinate.getX());
    ByteVector serY = TFHEInt32.serialize(coordinate.getY());
    byteBuffer.putInt(offset, serX.size());
    byteBuffer.putInt(offset + 4, serY.size());
    offset += 8;
    byte[] data = new byte[serX.size() + serY.size()];
    for (int i = 0; i < serX.size(); i++) {
      data[i] = serX.get(i).byteValue();
    }
    for (int i = 0; i < serY.size(); i++) {
      data[i + serX.size()] = serY.get(i).byteValue();
    }
    putBytes(offset, data);
    return 8 + serX.size() + serY.size();
  }

  public TFHECoordinateSequence getCoordinate(int offset) {
    int serXSize = byteBuffer.getInt(offset);
    int serYSize = byteBuffer.getInt(offset + 4);
    offset += 8;
    byte[] data = new byte[serXSize + serYSize];
    getBytes(data, offset, serXSize + serYSize);
    ByteVector serX = new ByteVector();
    ByteVector serY = new ByteVector();
    for (int i = 0; i < serXSize; i++) {
      serX.add((short) data[i]);
    }
    for (int i = 0; i < serYSize; i++) {
      serY.add((short) data[i + serXSize]);
    }
    CoordinateVector coordinates = new CoordinateVector();
    coordinates.add(new TFHECoordinate(TFHEInt32.deserialize(serX), TFHEInt32.deserialize(serY)));
    return new TFHECoordinateSequence(coordinates);
  }

  public void putCoordinates(int offset, TFHECoordinateSequence coordinates) {
    long numCoordinates = coordinates.size();
    for (int k = 0; k < numCoordinates; k++) {
      TFHECoordinate coord = coordinates.getAt(k);
      int size = putCoordinate(offset, coord);
      offset += size;
    }
  }

  public TFHECoordinateSequence getCoordinates(int offset, int numCoordinates) {
    CoordinateVector coordinates = new CoordinateVector();
    for (int k = 0; k < numCoordinates; k++) {
      int serXSize = byteBuffer.getInt(offset);
      int serYSize = byteBuffer.getInt(offset + 4);
      offset += 8;
      byte[] data = new byte[serXSize + serYSize];
      getBytes(data, offset, serXSize + serYSize);
      ByteVector serX = new ByteVector();
      ByteVector serY = new ByteVector();
      for (int i = 0; i < serXSize; i++) {
        serX.add((short) data[i]);
      }
      for (int i = 0; i < serYSize; i++) {
        serY.add((short) data[i + serXSize]);
      }
      coordinates.add(new TFHECoordinate(TFHEInt32.deserialize(serX), TFHEInt32.deserialize(serY)));
      offset += serXSize + serYSize;
    }
    return new TFHECoordinateSequence(coordinates);
  }

  public CipherGeometryBuffer slice(int offset) {
    byteBuffer.position(offset);
    return new CipherGeometryBuffer(byteBuffer.slice());
  }

  public byte[] toByteArray() {
    if (byteBuffer.arrayOffset() == 0) {
      return byteBuffer.array();
    } else {
      byte[] bytes = new byte[byteBuffer.capacity()];
      byteBuffer.get(bytes);
      return bytes;
    }
  }
}
