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

import static org.junit.Assert.*;

import java.util.List;
import org.ade.SpatialFHE.FHEHelper;
import org.ade.SpatialFHE.spatialfhe.CipherMat;
import org.ade.SpatialFHE.spatialfhe.DoubleVector;
import org.apache.sedona.common.raster.MapAlgebra;
import org.apache.sedona.common.raster.RasterConstructors;
import org.geotools.coverage.grid.GridCoverage2D;
import org.junit.Test;
import org.opengis.referencing.FactoryException;

public class FHEMapAlgebraTest extends FHERasterTestBase {
  @Test
  public void testBandAsCipherMat() throws FactoryException {
    int widthInPixel = 10;
    int heightInPixel = 10;
    double upperLeftX = 0;
    double upperLeftY = 0;
    double cellSize = 1;
    int numbBands = 1;
    GridCoverage2D raster =
        RasterConstructors.makeEmptyRaster(
            numbBands, widthInPixel, heightInPixel, upperLeftX, upperLeftY, cellSize);
    // Out of bound index should return null
    CipherMat cipherBand = FHEMapAlgebra.bandAsCipherMat(raster, 0);
    assertNull(cipherBand);
    cipherBand = FHEMapAlgebra.bandAsCipherMat(raster, 1);
    assertNotNull(cipherBand);
    FHEHelper fheHelper = FHEHelper.getInstance();
    DoubleVector doubleVector = fheHelper.getManager().decryptMat(cipherBand);
    double[] band = doubleVector.stream().mapToDouble(Double::doubleValue).toArray();

    assertEquals(widthInPixel * heightInPixel, band.length);
    for (double v : band) {
      // The default value is 0.0
      assertEquals(0.0, v, 0.1);
    }
    // Now set the value of the first band and check again
    for (int i = 0; i < band.length; i++) {
      band[i] = i * 0.1;
    }
    CipherMat newCipherBand =
        FHEMapAlgebra.bandAsCipherMat(MapAlgebra.addBandFromArray(raster, band, 1), 1);
    assertNotNull(newCipherBand);
    List<Double> newDoubleList = fheHelper.getManager().decryptMat(newCipherBand);
    double[] bandNew = newDoubleList.stream().mapToDouble(Double::doubleValue).toArray();
    assertEquals(band.length, bandNew.length);
    for (int i = 0; i < band.length; i++) {
      assertEquals(band[i], bandNew[i], 1e-5);
    }
  }

  @Test
  public void testAddPrivate() {
    double[] band1 = new double[] {200, 100, 145, 245};
    double[] band2 = new double[] {55, 155, 110, 10};
    FHEHelper fheHelper = FHEHelper.getInstance();
    CipherMat cipherBand1 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band1));
    CipherMat cipherBand2 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band2));
    CipherMat cipherActual = FHEMapAlgebra.addPrivate(cipherBand1, cipherBand2);
    List<Double> actualList = fheHelper.getManager().decryptMat(cipherActual);
    double[] actual = actualList.stream().mapToDouble(Double::doubleValue).toArray();
    double[] expected = new double[] {255.0, 255.0, 255.0, 255.0};
    assertArrayEquals(expected, actual, 0.1d);
  }

  @Test
  public void testAddPrivatePlain() {
    double[] band1 = new double[] {200, 100, 145, 245};
    double[] band2 = new double[] {55, 155, 110, 10};
    FHEHelper fheHelper = FHEHelper.getInstance();
    CipherMat cipherBand1 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band1));
    CipherMat cipherActual = FHEMapAlgebra.addPrivatePlain(cipherBand1, band2);
    List<Double> actualList = fheHelper.getManager().decryptMat(cipherActual);
    double[] actual = actualList.stream().mapToDouble(Double::doubleValue).toArray();
    double[] expected = new double[] {255.0, 255.0, 255.0, 255.0};
    assertArrayEquals(expected, actual, 0.1d);
  }

  @Test
  public void testSubtractPrivate() {
    double[] band1 = new double[] {200, 100, 145, 245};
    double[] band2 = new double[] {55, 155, 110, 10};
    FHEHelper fheHelper = FHEHelper.getInstance();
    CipherMat cipherBand1 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band1));
    CipherMat cipherBand2 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band2));
    CipherMat cipherActual = FHEMapAlgebra.subtractPrivate(cipherBand1, cipherBand2);
    List<Double> actualList = fheHelper.getManager().decryptMat(cipherActual);
    double[] actual = actualList.stream().mapToDouble(Double::doubleValue).toArray();
    double[] expected = new double[] {145.0, -55.0, 35.0, 235.0};
    assertArrayEquals(expected, actual, 0.1d);
  }

  @Test
  public void testMultiplyPrivate() {
    double[] band1 = new double[] {200, 100, 145, 245};
    double[] band2 = new double[] {55, 155, 110, 10};
    FHEHelper fheHelper = FHEHelper.getInstance();
    CipherMat cipherBand1 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band1));
    CipherMat cipherBand2 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band2));
    CipherMat cipherActual = FHEMapAlgebra.multiplyPrivate(cipherBand1, cipherBand2);
    List<Double> actualList = fheHelper.getManager().decryptMat(cipherActual);
    double[] actual = actualList.stream().mapToDouble(Double::doubleValue).toArray();
    double[] expected = new double[] {11000.0, 15500.0, 15950.0, 2450.0};
    assertArrayEquals(expected, actual, 0.1d);
  }

  @Test
  public void testMultiplyPrivatePlain() {
    double[] band1 = new double[] {200, 100, 145, 245};
    double[] band2 = new double[] {55, 155, 110, 10};
    FHEHelper fheHelper = FHEHelper.getInstance();
    CipherMat cipherBand1 = fheHelper.getManager().encryptMat(2, 2, new DoubleVector(band1));
    CipherMat cipherActual = FHEMapAlgebra.multiplyPrivatePlain(cipherBand1, band2);
    List<Double> actualList = fheHelper.getManager().decryptMat(cipherActual);
    double[] actual = actualList.stream().mapToDouble(Double::doubleValue).toArray();
    double[] expected = new double[] {11000.0, 15500.0, 15950.0, 2450.0};
    assertArrayEquals(expected, actual, 0.1d);
  }
}
