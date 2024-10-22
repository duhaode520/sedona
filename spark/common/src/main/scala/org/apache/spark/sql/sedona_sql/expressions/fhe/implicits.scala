package org.apache.spark.sql.sedona_sql.expressions.fhe

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.sedona_sql.expressions.SerdeAware
import org.ade.SpatialFHE.spatialfhe.CipherMat
import org.apache.sedona.sql.utils.CipherMatSerializer

object implicits {
  implicit class CipherMatInputExpressionEnhancer(inputExpression: Expression) {
    def toCipherMat(input: InternalRow): CipherMat = {
      inputExpression match {
        case expression: SerdeAware =>
          expression
            .evalWithoutSerialization(input)
            .asInstanceOf[CipherMat]
        case _ =>
          inputExpression.eval(input).asInstanceOf[Array[Byte]] match {
            case bytes: Array[Byte] => CipherMatSerializer.deserialize(bytes)
            case _ => null
          }
      }
    }
  }

  implicit class CipherMatEnhancer(cipherMat: CipherMat) {
    def serialize: Array[Byte] = CipherMatSerializer.serialize(cipherMat)
  }
}
