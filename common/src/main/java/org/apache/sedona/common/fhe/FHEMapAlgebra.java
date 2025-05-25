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

import java.awt.image.Raster;
import org.ade.SpatialFHE.FHEHelper;
import org.ade.SpatialFHE.spatialfhe.CipherMat;
import org.ade.SpatialFHE.spatialfhe.DoubleVector;
import org.apache.sedona.common.raster.MapAlgebra;
import org.apache.sedona.common.utils.RasterUtils;
import org.geotools.coverage.grid.GridCoverage2D;

public class FHEMapAlgebra {

  /**
   * Returns a CipherMat object representing the specified band of the given raster.
   *
   * @param rasterGeom The raster from which to extract the band.
   * @param bandIndex The index of the band to extract.
   * @return A CipherMat object representing the specified band, or null if the band does not exist.
   */
  public static CipherMat bandAsCipherMat(GridCoverage2D rasterGeom, int bandIndex) {
    double[] bandValues = MapAlgebra.bandAsArray(rasterGeom, bandIndex);
    if (bandValues == null) {
      return null;
    }
    Raster raster = RasterUtils.getRaster(rasterGeom.getRenderedImage());
    // Get the width and height of the raster
    int width = raster.getWidth();
    int height = raster.getHeight();

    FHEHelper fhe = FHEHelper.getInstance();
    return fhe.getManager().encryptMat(width, height, new DoubleVector(bandValues));
  }

  /**
   * @param cipher_band1 The first CipherMat object to add.
   * @param cipher_band2 The second CipherMat object to add.
   * @return A CipherMat object representing the sum of the two input bands.
   */
  public static CipherMat addPrivate(CipherMat cipher_band1, CipherMat cipher_band2) {
    FHEHelper fheHelper = FHEHelper.getInstance();
    ensureBandShape(cipher_band1.getWidth(), cipher_band2.getWidth());
    ensureBandShape(cipher_band1.getHeight(), cipher_band2.getHeight());
    ensureBandShape(cipher_band1.getData().size(), cipher_band2.getData().size());
    return fheHelper.getManager().addMat(cipher_band1, cipher_band2);
  }

  /**
   * @param cipher_band1 The first CipherMat object to add.
   * @param plain_band2 The second band of the raster to add.
   * @return A CipherMat object representing the sum of the two input bands.
   */
  public static CipherMat addPrivatePlain(CipherMat cipher_band1, double[] plain_band2) {
    FHEHelper fheHelper = FHEHelper.getInstance();
    ensureBandShape(cipher_band1.getWidth() * cipher_band1.getHeight(), plain_band2.length);
    return fheHelper.getManager().addMatPlain(cipher_band1, new DoubleVector(plain_band2));
  }

  public static CipherMat subtractPrivate(CipherMat cipher_band1, CipherMat cipher_band2) {
    FHEHelper fheHelper = FHEHelper.getInstance();
    ensureBandShape(cipher_band1.getWidth(), cipher_band2.getWidth());
    ensureBandShape(cipher_band1.getHeight(), cipher_band2.getHeight());
    ensureBandShape(cipher_band1.getData().size(), cipher_band2.getData().size());
    return fheHelper.getManager().subMat(cipher_band1, cipher_band2);
  }

  public static CipherMat multiplyPrivate(CipherMat cipher_band1, CipherMat cipher_band2) {
    FHEHelper fheHelper = FHEHelper.getInstance();
    ensureBandShape(cipher_band1.getWidth(), cipher_band2.getWidth());
    ensureBandShape(cipher_band1.getHeight(), cipher_band2.getHeight());
    ensureBandShape(cipher_band1.getData().size(), cipher_band2.getData().size());
    return fheHelper.getManager().multiplyMat(cipher_band1, cipher_band2);
  }

  public static CipherMat multiplyPrivatePlain(CipherMat cipher_band1, double[] plain_band2) {
    FHEHelper fheHelper = FHEHelper.getInstance();
    ensureBandShape(cipher_band1.getWidth() * cipher_band1.getHeight(), plain_band2.length);
    return fheHelper.getManager().multiplyMatPlain(cipher_band1, new DoubleVector(plain_band2));
  }
  

  /**
   * Multiplies an encrypted band by a plaintext factor.
   *
   * @param cipher_band The CipherMat object representing the encrypted band.
   * @param factor The plaintext factor to multiply with.
   * @return A CipherMat object representing the product of the band and factor.
   */
  public static CipherMat multiplyFactorPrivatePlain(CipherMat cipher_band, double factor) {
    FHEHelper fheHelper = FHEHelper.getInstance();
    // Create a vector with the same factor repeated for all elements
    double[] factorArray = new double[cipher_band.getWidth() * cipher_band.getHeight()];
    for (int i = 0; i < factorArray.length; i++) {
      factorArray[i] = factor;
    }
    return fheHelper.getManager().multiplyMatPlain(cipher_band, new DoubleVector(factorArray));
  }

  /**
   * Throws an IllegalArgumentException if the lengths of the bands are not the same.
   *
   * @param band1 length of band values
   * @param band2 length of band values
   */
  private static void ensureBandShape(int band1, int band2) {
    if (band1 != band2) {
      throw new IllegalArgumentException(
          "The shape of the provided bands is not same. Please check your inputs, it should be same.");
    }
  }
}
