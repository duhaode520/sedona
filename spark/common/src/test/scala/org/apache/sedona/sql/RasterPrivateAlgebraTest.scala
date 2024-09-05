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
package org.apache.sedona.sql

import org.ade.SpatialFHE.FHEHelper
import org.ade.SpatialFHE.spatialfhe.{CipherMat, DoubleVector}
import org.scalatest.{BeforeAndAfter, GivenWhenThen}

import scala.collection.JavaConverters

class RasterPrivateAlgebraTest extends TestBaseScala with BeforeAndAfter with GivenWhenThen {
  import sparkSession.implicits._

  describe("Should pass all cipher operations on cipher bands") {
    it("Passed RS_Add_Private") {
      val fheHelper = FHEHelper.getInstance()
      var inputDF = Seq(Seq(200.0, 400.0, 600.0), Seq(200.0, 500.0, 800.0))
        .map(seq => seq.map(d => java.lang.Double.valueOf(d.toString)))
        .map(seq => JavaConverters.asJavaIterable(seq))
        .map(it => fheHelper.getManager.encryptMat(3, 1, new DoubleVector(it)))
        .grouped(2)
        .map {
          case Seq(a, b) => (a, b)
          case Seq(a) => (a, null)
        }
        .toSeq
        .toDF("Band1", "Band2")

      val expected = Seq(400.0, 900.0, 1400.0)
      inputDF = inputDF.selectExpr("RS_Add_Private(Band1, Band2) as sumOfBands")
      val actual = inputDF
        .first()
        .toSeq
        .asInstanceOf[Seq[CipherMat]]
        .map(fheHelper.getManager.decryptMat(_))
        .flatMap(JavaConverters.asScalaBuffer(_).toSeq)
      // expected and actual are approximately equal
      actual.zip(expected).foreach { case (a, b) =>
        assert(Math.abs(a - b) < 1e-5)
      }
    }

    it("Passed BS_BandAsCipherMat") {
      val df = sparkSession.read.format("binaryFile").load(resourceFolder + "raster/test1.tiff")
      val metadata =
        df.selectExpr("RS_Metadata(RS_FromGeoTiff(content))").first().getStruct(0).toSeq
      val width = metadata(2).asInstanceOf[Int]
      val height = metadata(3).asInstanceOf[Int]
      val result = df
        .selectExpr("RS_BandAsCipherMat(RS_FromGeoTiff(content), 1)")
        .first()
        .getAs[CipherMat](0)
      assert(result.getWidth == width && result.getHeight == height)
    }
  }
}
