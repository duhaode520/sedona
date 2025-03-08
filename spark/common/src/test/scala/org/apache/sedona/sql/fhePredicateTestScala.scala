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

import org.ade.SpatialFHE.spatialfhe.TFHEBool
import org.apache.spark.sql.catalyst.expressions.{EmptyRow, Literal}
import org.apache.spark.sql.sedona_sql.expressions.fhe.{ST_Contains_Private, ST_CoveredBy_Private, ST_Covers_Private, ST_Crosses_Private, ST_Disjoint_Private, ST_Equals_Private, ST_GeomFromWKT_Private, ST_Intersects_Private, ST_Overlaps_Private, ST_Point_Private, ST_PolygonFromEnvelope_Private, ST_Touches_Private, ST_Within_Private}

class fhePredicateTestScala extends TestBaseScala {

  describe("Sedona-SQL FHE Predicate Test") {

//    it("Passed ST_Contains_Private with FHE") {
//      var pointCsvDF = sparkSession.read
//        .format("csv")
//        .option("delimiter", ",")
//        .option("header", "false")
//        .load(csvPointInputLocation)
//      pointCsvDF.createOrReplaceTempView("pointtable")
//      var pointDf = sparkSession.sql(
//        "select ST_Point_Private(cast(pointtable._c0 as Integer), cast(pointtable._c1 as Integer)) as arealandmark from pointtable")
//      pointDf.createOrReplaceTempView("pointdf")
//
//      var resultDf = sparkSession.sql(
//        "select * from pointdf where ST_Contains_Private(ST_PolygonFromEnvelope_Private(1,100,1000,1100), pointdf.arealandmark)")
//      assert(resultDf.count() == 999)
//    }
//
//    it("Passed ST_Intersects_Private with FHE") {
//      var pointCsvDF = sparkSession.read
//        .format("csv")
//        .option("delimiter", ",")
//        .option("header", "false")
//        .load(csvPointInputLocation)
//      pointCsvDF.createOrReplaceTempView("pointtable")
//      var pointDf = sparkSession.sql(
//        "select ST_Point_Private(cast(pointtable._c0 as Integer), cast(pointtable._c1 as Integer)) as arealandmark from pointtable")
//      pointDf.createOrReplaceTempView("pointdf")
//
//      var resultDf = sparkSession.sql(
//        "select * from pointdf where ST_Intersects_Private(ST_PolygonFromEnvelope_Private(1,100,1000,1100), pointdf.arealandmark)")
//      assert(resultDf.count() == 999)
//    }
//
//    it("Passed ST_Within_Private with FHE") {
//      var pointCsvDF = sparkSession.read
//        .format("csv")
//        .option("delimiter", ",")
//        .option("header", "false")
//        .load(csvPointInputLocation)
//      pointCsvDF.createOrReplaceTempView("pointtable")
//      var pointDf = sparkSession.sql(
//        "select ST_Point_Private(cast(pointtable._c0 as Integer), cast(pointtable._c1 as Integer)) as arealandmark from pointtable")
//      pointDf.createOrReplaceTempView("pointdf")
//
//      var resultDf = sparkSession.sql(
//        "select * from pointdf where ST_Within_Private(pointdf.arealandmark, ST_PolygonFromEnvelope_Private(1,100,1000,1100))")
//      assert(resultDf.count() == 999)
//    }
//
//    it("Passed ST_Covers_Private with FHE") {
//      var pointCsvDF = sparkSession.read
//        .format("csv")
//        .option("delimiter", ",")
//        .option("header", "false")
//        .load(csvPointInputLocation)
//      pointCsvDF.createOrReplaceTempView("pointtable")
//      var pointDf = sparkSession.sql(
//        "select ST_Point_Private(cast(pointtable._c0 as Integer), cast(pointtable._c1 as Integer)) as arealandmark from pointtable")
//      pointDf.createOrReplaceTempView("pointdf")
//
//      var resultDf = sparkSession.sql(
//        "select * from pointdf where ST_Covers_Private(ST_PolygonFromEnvelope_Private(1,100,101,201), pointdf.arealandmark)")
//      assert(resultDf.count() == 100)
//    }
//
//    it("Passed ST_CoveredBy_Private with FHE") {
//      var pointCsvDF = sparkSession.read
//        .format("csv")
//        .option("delimiter", ",")
//        .option("header", "false")
//        .load(csvPointInputLocation)
//      pointCsvDF.createOrReplaceTempView("pointtable")
//      var pointDf = sparkSession.sql(
//        "select ST_Point_Private(cast(pointtable._c0 as Integer), cast(pointtable._c1 as Integer)) as arealandmark from pointtable")
//      pointDf.createOrReplaceTempView("pointdf")
//
//      var resultDf = sparkSession.sql(
//        "select * from pointdf where ST_CoveredBy_Private(pointdf.arealandmark, ST_PolygonFromEnvelope_Private(1,100,101,201))")
//      assert(resultDf.count() == 100)
//    }
//
//    it("Passed ST_Equals_Private for ST_Point_Private with FHE") {
//      // Read csv to get the points table
//      var pointCsvDF = sparkSession.read
//        .format("csv")
//        .option("delimiter", ",")
//        .option("header", "false")
//        .load(csvPoint1InputLocation)
//      pointCsvDF.createOrReplaceTempView("pointtable")
//
//      // Convert the pointtable to pointdf using ST_Point_Private with integer coordinates
//      var pointDf = sparkSession.sql(
//        "select ST_Point_Private(cast(pointtable._c0 as Integer), cast(pointtable._c1 as Integer)) as point from pointtable")
//      pointDf.createOrReplaceTempView("pointdf")
//
//      var equaldf = sparkSession.sql(
//        "select * from pointdf where ST_Equals_Private(pointdf.point, ST_Point_Private(100, 200)) ")
//
//      assert(equaldf.count() == 5, s"Expected 5 value but got ${equaldf.count()}")
//    }
//
//    it("Passed ST_Equals_Private for ST_Polygon_Private with FHE") {
//      // Read csv to get the polygon table
//      var polygonCsvDF = sparkSession.read
//        .format("csv")
//        .option("delimiter", ",")
//        .option("header", "false")
//        .load(csvPolygon1InputLocation)
//      polygonCsvDF.createOrReplaceTempView("polygontable")
//
//      // Convert the polygontable to polygons using ST_PolygonFromEnvelope_Private with integer coordinates
//      var polygonDf = sparkSession.sql(
//        "select ST_PolygonFromEnvelope_Private(cast(polygontable._c0 as Integer), cast(polygontable._c1 as Integer), cast(polygontable._c2 as Integer), cast(polygontable._c3 as Integer)) as polygonshape from polygontable")
//      polygonDf.createOrReplaceTempView("polygondf")
//
//      // Selected polygon is Polygon (100,200,101,201)
//      var equaldf1 = sparkSession.sql(
//        "select * from polygonDf where ST_Equals_Private(polygonDf.polygonshape, ST_PolygonFromEnvelope_Private(100,200,101,201)) ")
//
//      assert(equaldf1.count() == 5, s"Expected 5 value but got ${equaldf1.count()}")
//
//      // Change the order of the polygon points (101,201,100,200)
//      var equaldf2 = sparkSession.sql(
//        "select * from polygonDf where ST_Equals_Private(polygonDf.polygonshape, ST_PolygonFromEnvelope_Private(101,201,100,200)) ")
//
//      assert(equaldf2.count() == 5, s"Expected 5 value but got ${equaldf2.count()}")
//    }

    it("Passed ST_Crosses_Private with FHE") {
      var crossesTesttable = sparkSession.sql(
        "select ST_GeomFromWKT_Private('POLYGON((1 1, 4 1, 4 4, 1 4, 1 1))') as a, ST_GeomFromWKT_Private('LINESTRING(1 5, 5 1)') as b")
      crossesTesttable.createOrReplaceTempView("crossesTesttable")
      var crosses = sparkSession.sql(
        "select(FHE_Decrypt_Bool(ST_Crosses_Private(a, b))) from crossesTesttable")

      var notCrossesTesttable = sparkSession.sql(
        "select ST_GeomFromWKT_Private('POLYGON((1 1, 4 1, 4 4, 1 4, 1 1))') as a, ST_GeomFromWKT_Private('POLYGON((2 2, 5 2, 5 5, 2 5, 2 2))') as b")
      notCrossesTesttable.createOrReplaceTempView("notCrossesTesttable")
      var notCrosses = sparkSession.sql(
        "select(FHE_Decrypt_Bool(ST_Crosses_Private(a, b))) from notCrossesTesttable")

      assert(crosses.take(1)(0).get(0).asInstanceOf[Boolean])
      assert(!notCrosses.take(1)(0).get(0).asInstanceOf[Boolean])
    }

    it("Passed ST_Relate") {
      val baseDf = sparkSession.sql(
        "SELECT ST_GeomFromWKT_Private('LINESTRING (1 1, 5 5)') AS g1, ST_GeomFromWKT_Private('POLYGON ((3 3, 3 7, 7 7, 7 3, 3 3))') as g2, '1010F0212' as im");

      val actualBoolean =
        baseDf.selectExpr("FHE_Decrypt_Bool(ST_Relate(g1, g2, im))").first().getBoolean(0)
      assert(actualBoolean)
    }

    it("Passed ST_Touches_Private with FHE") {
      var pointCsvDF = sparkSession.read
        .format("csv")
        .option("delimiter", ",")
        .option("header", "false")
        .load(csvPointInputLocation)
      pointCsvDF.createOrReplaceTempView("pointtable")
      var pointDf = sparkSession.sql(
        "select ST_Point_Private(cast(pointtable._c0 as Integer), cast(pointtable._c1 as Integer)) as arealandmark from pointtable")
      pointDf.createOrReplaceTempView("pointdf")

      var resultDf = sparkSession.sql(
        "select * from pointdf where ST_Touches_Private(pointdf.arealandmark, ST_PolygonFromEnvelope_Private(0,99,1,101))")
      assert(resultDf.count() == 1)
    }

    it("Passed ST_Overlaps_Private with FHE") {
      var testtable = sparkSession.sql(
        "select ST_GeomFromWKT_Private('POLYGON((25 25, 25 45, 45 45, 45 25, 25 25))') as a,ST_GeomFromWKT_Private('POLYGON((40 40, 40 60, 60 60, 60 40, 40 40))') as b, ST_GeomFromWKT_Private('POLYGON((5 5, 4 6, 6 6, 6 4, 5 5))') as c, ST_GeomFromWKT_Private('POLYGON((5 5, 4 6, 6 6, 6 4, 5 5))') as d")
      testtable.createOrReplaceTempView("testtable")
      var overlaps =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_Overlaps_Private(a,b))) from testtable")
      var notoverlaps =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_Overlaps_Private(c,d))) from testtable")
      assert(overlaps.take(1)(0).get(0).asInstanceOf[Boolean])
      assert(!notoverlaps.take(1)(0).get(0).asInstanceOf[Boolean])
    }

    it("Passed ST_Disjoint_Private with FHE") {
      var testtable = sparkSession.sql(
        "select ST_GeomFromWKT_Private('POLYGON((10 40, 45 40, 40 20, 10 20, 10 40))') as a, ST_GeomFromWKT_Private('POLYGON((50 40, 60 40, 60 20, 50 20, 50 40))') as b, ST_GeomFromWKT_Private('POLYGON((1 9, 6 6, 6 4, 1 2, 1 9))') as c, ST_GeomFromWKT_Private('POLYGON((2 5, 4 5, 4 1, 2 1, 2 5))') as d")
      testtable.createOrReplaceTempView("testtable")
      var disjoint =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_Disjoint_Private(a,b))) from testtable")
      var notdisjoint =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_Disjoint_Private(c,d))) from testtable")
      assert(disjoint.take(1)(0).get(0).asInstanceOf[Boolean])
      assert(!notdisjoint.take(1)(0).get(0).asInstanceOf[Boolean])
    }

    it("Passed edge cases of ST_Contains_Private and ST_Covers_Private with FHE") {
      val testtable = sparkSession.sql(
        "select ST_GeomFromWKT_Private('POLYGON((2 0, 0 2, -2 0, 2 0))') AS a, ST_GeomFromWKT_Private('POINT(2 0)') AS b")
      testtable.createOrReplaceTempView("testtable")
      val contains =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_Contains_Private(a, b))) from testtable")
      val covers =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_Covers_Private(a, b))) from testtable")
      assert(!contains.take(1)(0).get(0).asInstanceOf[Boolean])
      assert(covers.take(1)(0).get(0).asInstanceOf[Boolean])
    }

    it("Passed edge cases of ST_Within_Private and ST_CoveredBy_Private with FHE") {
      val testtable = sparkSession.sql(
        "select ST_GeomFromWKT_Private('POLYGON((2 0, 0 2, -2 0, 2 0))') AS a, ST_GeomFromWKT_Private('POINT(2 0)') AS b")
      testtable.createOrReplaceTempView("testtable")
      val within =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_Within_Private(b, a))) from testtable")
      val coveredBy =
        sparkSession.sql("select(FHE_Decrypt_Bool(ST_CoveredBy_Private(b, a))) from testtable")
      assert(!within.take(1)(0).get(0).asInstanceOf[Boolean])
      assert(coveredBy.take(1)(0).get(0).asInstanceOf[Boolean])
    }

    Seq(
      ST_Contains_Private,
      ST_Intersects_Private,
      ST_Within_Private,
      ST_Covers_Private,
      ST_CoveredBy_Private,
      ST_Crosses_Private,
      ST_Overlaps_Private,
      ST_Touches_Private,
      ST_Equals_Private,
      ST_Disjoint_Private).foreach { predicate =>
      it(s"Passed null handling in $predicate") {
        val point =
          ST_Point_Private(Literal.create(0) :: Literal.create(0) :: Literal.create(0) :: Nil)
        val missing = Literal.create(null)

        assert(predicate(point :: point :: Nil).eval(EmptyRow) != null)
        assert(predicate(point :: missing :: Nil).eval(EmptyRow) == null)
        assert(predicate(missing :: point :: Nil).eval(EmptyRow) == null)
        assert(predicate(missing :: missing :: Nil).eval(EmptyRow) == null)
      }
    }
  }
}
