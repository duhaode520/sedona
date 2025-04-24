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

import java.io.IOException;
import org.ade.SpatialFHE.spatialfhe.*;

public class CipherGeometrySerializer {
  interface WKBType {
    int wkbPoint = 1;
    int wkbLineString = 2;
    int wkbPolygon = 3;
  }

  public static byte[] serialize(TFHEGeometry geometry) {
    try {
      CipherGeometryOutputStream outputStream;
      TFHEGeometryTypeId type = geometry.getGeometryTypeId();
      if (type == TFHEGeometryTypeId.TFHE_POINT) {
        outputStream = serializePoint(geometry.asPoint());
      } else if (type == TFHEGeometryTypeId.TFHE_LINESTRING
          || type == TFHEGeometryTypeId.TFHE_LINEARRING) {
        outputStream = serializeLineString(geometry.asLineString());
      } else if (type == TFHEGeometryTypeId.TFHE_POLYGON) {
        outputStream = serializePolygon(geometry.asPolygon());
      } else {
        throw new UnsupportedOperationException(
            "Geometry type is not supported: " + geometry.getClass().getSimpleName());
      }
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Error serializing geometry", e);
    }
  }

  public static TFHEGeometry deserialize(byte[] bytes) {
    return deserialize(new CipherGeometryInputStream(bytes), null);
  }

  public static TFHEGeometry deserialize(
      CipherGeometryInputStream inputStream, TFHEGeometryFactory factory) {
    try {
      int preambleByte = inputStream.getByte(0) & 0xFF;
      int wkbType = preambleByte >> 4;
      boolean hasSrid = (preambleByte & 0x01) != 0;
      // SRID handling code is commented out in the original code

      if (factory == null) {
        factory = getGeometryFactory();
      }
      return deserialize(inputStream, wkbType, factory);
    } catch (IOException e) {
      throw new RuntimeException("Error deserializing geometry", e);
    }
  }

  private static TFHEGeometry deserialize(
      CipherGeometryInputStream inputStream, int wkbType, TFHEGeometryFactory factory)
      throws IOException {
    switch (wkbType) {
      case WKBType.wkbPoint:
        return deserializePoint(inputStream, factory);
      case WKBType.wkbLineString:
        return deserializeLineString(inputStream, factory);
      case WKBType.wkbPolygon:
        return deserializePolygon(inputStream, factory);
      default:
        throw new IllegalArgumentException(
            "Cannot deserialize buffer containing unknown geometry type ID: " + wkbType);
    }
  }

  private static CipherGeometryOutputStream serializePoint(TFHEPoint point) throws IOException {
    TFHECoordinate coordinate = point.getCoordinate();
    int numCoordinates = (coordinate == null) ? 0 : 1;

    CipherGeometryOutputStream outputStream = createOutputStream(WKBType.wkbPoint, numCoordinates);

    if (coordinate != null) {
      outputStream.putCoordinate(coordinate);
    }

    return outputStream;
  }

  private static TFHEPoint deserializePoint(
      CipherGeometryInputStream inputStream, TFHEGeometryFactory factory) throws IOException {
    int numCoordinates = inputStream.readInt();
    TFHEPoint point;

    if (numCoordinates == 0) {
      point = factory.createPoint();
      inputStream.mark(5);
    } else {
      TFHECoordinateSequence coordinates = inputStream.readCoordinate();
      point = factory.createPoint(coordinates);
      // Mark the end of the point data
      int coordinateSize = inputStream.getMark();
      inputStream.mark(5 + coordinateSize);
    }

    return point;
  }

  private static CipherGeometryOutputStream serializeLineString(TFHELineString lineString)
      throws IOException {
    TFHECoordinateSequence coordinates = lineString.getCoordinates();
    long numCoordinates = coordinates.size();

    CipherGeometryOutputStream outputStream =
        createOutputStream(WKBType.wkbLineString, (int) numCoordinates);

    if (numCoordinates > 0) {
      outputStream.putCoordinates(coordinates);
    }

    return outputStream;
  }

  private static TFHELineString deserializeLineString(
      CipherGeometryInputStream inputStream, TFHEGeometryFactory factory) throws IOException {
    int numCoordinates = inputStream.readInt();

    if (numCoordinates > 0) {
      TFHECoordinateSequence coordinates = inputStream.readCoordinates(numCoordinates);
      // Mark where we finished reading
      //      inputStream.mark(8 + inputStream.getMark());
      return factory.createLineString(coordinates);
    } else {
      //      inputStream.mark(8);
      return factory.createLineString();
    }
  }

  private static CipherGeometryOutputStream serializePolygon(TFHEPolygon polygon)
      throws IOException {
    TFHELinearRing exteriorRing = polygon.getExteriorRing();
    int numInteriorRings = (int) polygon.getNumInteriorRing();
    int numCoordinates = (int) polygon.getNumPoints();

    CipherGeometryOutputStream outputStream =
        createOutputStream(WKBType.wkbPolygon, numCoordinates);

    if (exteriorRing != null && !exteriorRing.isEmpty()) {
      // Write number of rings
      outputStream.putInt(numInteriorRings + 1);

      // Write exterior ring point count
      outputStream.putInt((int) exteriorRing.getNumPoints());

      // Serialize all coordinates
      TFHECoordinateSequence coordinates = exteriorRing.getCoordinates();
      outputStream.putCoordinates(coordinates);

      // Write interior rings
      for (int i = 0; i < numInteriorRings; i++) {
        TFHELinearRing innerRing = polygon.getInteriorRingN(i);
        outputStream.putInt((int) innerRing.getNumPoints());
        outputStream.putCoordinates(innerRing.getCoordinates());
      }
    } else {
      outputStream.putInt(0);
      outputStream.putInt(0);
    }

    return outputStream;
  }

  private static TFHEPolygon deserializePolygon(
      CipherGeometryInputStream inputStream, TFHEGeometryFactory factory) throws IOException {
    int totalCoordinatesNum = inputStream.readInt();
    if (totalCoordinatesNum == 0) {
      return factory.createPolygon();
    }
    int numRings = inputStream.readInt();
    if (numRings == 0) {
      return factory.createPolygon();
    }

    int exteriorRingPoints = inputStream.readInt();
    if (exteriorRingPoints == 0) {
      return factory.createPolygon();
    }

    // Read exterior ring point count
    TFHECoordinateSequence coordinates = inputStream.readCoordinates(exteriorRingPoints);

    // Create exterior ring
    TFHELinearRing shell = factory.createLinearRing(coordinates);

    // Read interior rings if any
    TFHELinearRing[] holes = new TFHELinearRing[numRings - 1];
    int coordIndex = exteriorRingPoints;

    for (int i = 0; i < numRings - 1; i++) {
      int interiorRingPoints = inputStream.readInt();
      TFHECoordinateSequence interiorCoordinates = inputStream.readCoordinates(interiorRingPoints);
      holes[i] = factory.createLinearRing(interiorCoordinates);
      coordIndex += interiorRingPoints;
    }

    // Mark the position after all polygon data

    return factory.createPolygon(shell, new LinearRingVector(holes));
  }

  private static CipherGeometryOutputStream createOutputStream(int wkbType, int numCoordinates)
      throws IOException {
    CipherGeometryOutputStream outputStream = new CipherGeometryOutputStream();

    // Set header bytes [preamble][numCoordinates (4 bytes)]
    int preambleByte = (wkbType << 4);
    outputStream.putByte((byte) preambleByte);

    outputStream.putInt(numCoordinates);
    return outputStream;
  }

  private static int getBoundedInt(CipherGeometryInputStream inputStream, int offset)
      throws IOException {
    return inputStream.getInt(offset);
  }

  private static TFHEGeometryFactory getGeometryFactory() {
    return TFHEGeometryFactory.getDefaultInstance();
  }
}
