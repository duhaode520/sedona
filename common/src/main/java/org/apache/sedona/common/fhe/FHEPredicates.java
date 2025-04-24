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

import org.ade.SpatialFHE.spatialfhe.TFHEBool;
import org.ade.SpatialFHE.spatialfhe.TFHEGeometry;
import org.ade.SpatialFHE.spatialfhe.TFHEInt32;
import org.ade.SpatialFHE.spatialfhe.TFHEIntersectionMatrix;

public class FHEPredicates {

  public static TFHEBool contains(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.contains(rightGeometry);
  }

  public static TFHEBool intersects(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.intersects(rightGeometry);
  }

  public static TFHEBool within(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.within(rightGeometry);
  }

  public static TFHEBool covers(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.covers(rightGeometry);
  }

  public static TFHEBool coveredBy(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.coveredBy(rightGeometry);
  }

  public static TFHEBool crosses(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.crosses(rightGeometry);
  }

  public static TFHEBool overlaps(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.overlaps(rightGeometry);
  }

  public static TFHEBool touches(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.touches(rightGeometry);
  }

  public static TFHEBool equals(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.equals(rightGeometry);
  }

  public static TFHEBool disjoint(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
    TFHEInt32.javaGetContext().setServerKey();
    return leftGeometry.disjoint(rightGeometry);
  }

  //    public static String relate(TFHEGeometry leftGeometry, TFHEGeometry rightGeometry) {
  //        return relate(leftGeometry, rightGeometry).toString();
  //    }

  public static boolean relate(
      TFHEGeometry leftGeometry, TFHEGeometry rightGeometry, String intersectionMatrix) {
    TFHEInt32.javaGetContext().setServerKey();
    TFHEIntersectionMatrix matrixFromGeom = leftGeometry.relate(rightGeometry);
    return matrixFromGeom.matches(intersectionMatrix);
  }

  public static boolean relateMatch(String matrix1, String matrix2) {
    TFHEInt32.javaGetContext().setServerKey();
    return TFHEIntersectionMatrix.matches(matrix1, matrix2);
  }
}
