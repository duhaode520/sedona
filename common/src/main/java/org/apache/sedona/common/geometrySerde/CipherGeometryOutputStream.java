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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.ade.SpatialFHE.spatialfhe.*;

public class CipherGeometryOutputStream {
  private final ByteArrayOutputStream byteArrayOutputStream;
  private final DataOutputStream dataOutputStream;
  private int markOffset = 0;

  public CipherGeometryOutputStream() {
    this.byteArrayOutputStream = new ByteArrayOutputStream();
    this.dataOutputStream = new DataOutputStream(byteArrayOutputStream);
  }

  public CipherGeometryOutputStream(int initialSize) {
    this.byteArrayOutputStream = new ByteArrayOutputStream(initialSize);
    this.dataOutputStream = new DataOutputStream(byteArrayOutputStream);
  }

  public int getLength() {
    return byteArrayOutputStream.size();
  }

  public void mark(int offset) {
    markOffset = offset;
  }

  public int getMark() {
    return markOffset;
  }

  public void putByte(byte value) throws IOException {
    dataOutputStream.writeByte(value);
  }

  public void putBytes(byte[] bytes) throws IOException {
    dataOutputStream.write(bytes, 0, bytes.length);
  }

  public void putInt(int value) throws IOException {
    dataOutputStream.writeInt(value);
  }

  public int putCoordinate(TFHECoordinate coordinate) throws IOException {
    ByteVector serX = TFHEInt32.serialize(coordinate.getX());
    ByteVector serY = TFHEInt32.serialize(coordinate.getY());

    int startPosition = byteArrayOutputStream.size();

    dataOutputStream.writeInt(serX.size());
    dataOutputStream.writeInt(serY.size());

    byte[] data = new byte[serX.size() + serY.size()];
    for (int i = 0; i < serX.size(); i++) {
      data[i] = serX.get(i).byteValue();
    }
    for (int i = 0; i < serY.size(); i++) {
      data[i + serX.size()] = serY.get(i).byteValue();
    }

    dataOutputStream.write(data, 0, data.length);

    return byteArrayOutputStream.size() - startPosition;
  }

  public void putCoordinates(TFHECoordinateSequence coordinates) throws IOException {
    long numCoordinates = coordinates.size();
    for (int k = 0; k < numCoordinates; k++) {
      TFHECoordinate coord = coordinates.getAt(k);
      putCoordinate(coord);
    }
  }

  public byte[] toByteArray() {
    return byteArrayOutputStream.toByteArray();
  }
}
