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
    return (byte) byteArrayInputStream.read();
  }

  public void getBytes(byte[] bytes, int offset, int length) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    dataInputStream.readFully(bytes, 0, length);
  }

  public int getInt(int offset) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);
    return dataInputStream.readInt();
  }

  public TFHECoordinateSequence getCoordinate(int offset) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);

    int serXSize = dataInputStream.readInt();
    int serYSize = dataInputStream.readInt();

    byte[] data = new byte[serXSize + serYSize];
    dataInputStream.readFully(data, 0, serXSize + serYSize);

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

  public TFHECoordinateSequence getCoordinates(int offset, int numCoordinates) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);

    CoordinateVector coordinates = new CoordinateVector();

    for (int k = 0; k < numCoordinates; k++) {
      int serXSize = dataInputStream.readInt();
      int serYSize = dataInputStream.readInt();

      byte[] data = new byte[serXSize + serYSize];
      dataInputStream.readFully(data, 0, serXSize + serYSize);

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

    return new TFHECoordinateSequence(coordinates);
  }

  public CipherGeometryInputStream slice(int offset) throws IOException {
    byteArrayInputStream.reset();
    byteArrayInputStream.skip(offset);

    byte[] remaining = new byte[byteArrayInputStream.available()];
    dataInputStream.readFully(remaining);

    return new CipherGeometryInputStream(remaining);
  }
}
