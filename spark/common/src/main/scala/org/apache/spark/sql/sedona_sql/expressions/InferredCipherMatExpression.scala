package org.apache.spark.sql.sedona_sql.expressions

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.types.{ArrayType, UserDefinedType}

import scala.reflect.runtime.universe.{Type, typeOf}

object InferredCipherMatExpression {
  def isCipherMatType(t: Type): Boolean =
    InferrableCipherMatTypes.isCipherMatType(t)

  def cipherMatUDT: UserDefinedType[_] = InferrableCipherMatTypes.cipherMatUDT

  val cipherMatExtractor: Expression => InternalRow => Any =
    InferrableCipherMatTypes.cipherMatExtractor

  val cipherMatSerializer: Any => Any =
    InferrableCipherMatTypes.cipherMatSerializer
}
