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

import org.ade.SpatialFHE.spatialfhe.*;
import org.locationtech.jts.io.ParseException;

public class FHEConstructors {
  private static final TFHEGeometryFactory GEOMETRY_FACTORY =
      TFHEGeometryFactory.getDefaultInstance();

  public static TFHEGeometry geomFromWKT(String wkt) throws ParseException {
    if (wkt == null) {
      return null;
    }
    return new WKTReader(GEOMETRY_FACTORY).read(wkt);
  }

  public static TFHEGeometry point(double x, double y) {
    // See srid parameter discussion in https://issues.apache.org/jira/browse/SEDONA-234
    return GEOMETRY_FACTORY.createPoint(
        new TFHECoordinate(new TFHEInt32((int) x), new TFHEInt32((int) y)));
  }

  public static TFHEGeometry polygonFromEnvelope(
      double minX, double minY, double maxX, double maxY) {
    TFHECoordinate[] coordinates = new TFHECoordinate[5];
    coordinates[0] = new TFHECoordinate(new TFHEInt32((int) minX), new TFHEInt32((int) minY));
    coordinates[1] = new TFHECoordinate(new TFHEInt32((int) minX), new TFHEInt32((int) maxY));
    coordinates[2] = new TFHECoordinate(new TFHEInt32((int) maxX), new TFHEInt32((int) maxY));
    coordinates[3] = new TFHECoordinate(new TFHEInt32((int) maxX), new TFHEInt32((int) minY));
    coordinates[4] = coordinates[0];
    return GEOMETRY_FACTORY.createPolygon(new CoordinateVector(coordinates));
  }
}
