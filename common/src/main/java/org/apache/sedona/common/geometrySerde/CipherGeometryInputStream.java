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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.ade.SpatialFHE.spatialfhe.*;

public class CipherGeometryInputStream {
  private final ByteArrayInputStream byteArrayInputStream;
  private final DataInputStream dataInputStream;
  private int markOffset = 0;

  public CipherGeometryInputStream(byte[] bytes) {
    this.byteArrayInputStream = new ByteArrayInputStream(bytes);
    this.dataInputStream = new DataInputStream(byteArrayInputStream);
  }

  public int getLength() {
    return byteArrayInputStream.available();
  }

  public void mark(int offset) {
    markOffset = offset;
    byteArrayInputStream.mark(offset);
  }

  public int getMark() {
    return markOffset;
  }

  public byte getByte(int offset) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    return readByte();
  }

  public byte readByte() throws IOException {
    return (byte) byteArrayInputStream.read();
  }

  public void getBytes(byte[] bytes, int offset, int length) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    readBytes(bytes, length);
  }

  public void readBytes(byte[] bytes, int length) throws IOException {
    dataInputStream.readFully(bytes, 0, length);
  }

  public int getInt(int offset) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    return readInt();
  }

  public int readInt() throws IOException {
    return dataInputStream.readInt();
  }

  public TFHECoordinateSequence getCoordinate(int offset) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    return readCoordinate();
  }

  public TFHECoordinateSequence readCoordinate() throws IOException {
    int serXSize = readInt();
    int serYSize = readInt();

    byte[] data = new byte[serXSize + serYSize];
    readBytes(data, serXSize + serYSize);

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
    TFHECoordinateSequence coordinateSequence = new TFHECoordinateSequence(coordinates.size());
    for (int i = 0; i < coordinates.size(); i++) {
      coordinateSequence.setAt(coordinates.get(i), i);
    }
    return coordinateSequence;
  }

  public TFHECoordinateSequence getCoordinates(int offset, int numCoordinates) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    return readCoordinates(numCoordinates);
  }

  public TFHECoordinateSequence readCoordinates(int numCoordinates) throws IOException {
    CoordinateVector coordinates = new CoordinateVector();

    for (int k = 0; k < numCoordinates; k++) {
      int serXSize = readInt();
      int serYSize = readInt();

      byte[] data = new byte[serXSize + serYSize];
      readBytes(data, serXSize + serYSize);

      ByteVector serX = new ByteVector();
      ByteVector serY = new ByteVector();

      for (int i = 0; i < serXSize; i++) {
        serX.add((short) data[i]);
      }
      for (int i = 0; i < serYSize; i++) {
        serY.add((short) data[i + serXSize]);
      }

      coordinates.add(new TFHECoordinate(TFHEInt32.deserialize(serX), TFHEInt32.deserialize(serY)));
    }

    TFHECoordinateSequence coordinateSequence = new TFHECoordinateSequence(coordinates.size());
    for (int i = 0; i < coordinates.size(); i++) {
      coordinateSequence.setAt(coordinates.get(i), i);
    }
    return coordinateSequence;
  }

  public CipherGeometryInputStream slice(int offset) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    return readSlice();
  }

  public CipherGeometryInputStream readSlice() throws IOException {
    byte[] remaining = new byte[byteArrayInputStream.available()];
    readBytes(remaining, remaining.length);
    return new CipherGeometryInputStream(remaining);
  }
}
