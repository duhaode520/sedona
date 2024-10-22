package org.apache.spark.sql.sedona_sql.expressions

import scala.reflect.runtime.universe.{Type, typeOf}
import org.ade.SpatialFHE.spatialfhe.CipherMat
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.sedona_sql.UDT.CipherMatUDT
import org.apache.spark.sql.types.UserDefinedType
import org.apache.spark.sql.sedona_sql.expressions.fhe.implicits._

object InferrableCipherMatTypes {
  implicit val cipherMatInstance: InferrableType[CipherMat] =
    new InferrableType[CipherMat] {}

  def isCipherMatType(t: Type): Boolean = t =:= typeOf[CipherMat]

  val cipherMatUDT: UserDefinedType[_] = CipherMatUDT

  def cipherMatExtractor(expr: Expression)(input: InternalRow): Any = expr.toCipherMat(input)

  def cipherMatSerializer(output: Any) :Any =
    if (output != null) {
      output.asInstanceOf[CipherMat].serialize
    } else {
      null
    }
}
