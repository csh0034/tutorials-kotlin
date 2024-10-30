package com.ask.base

import org.hibernate.annotations.IdGeneratorType
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.util.UUID

class PrefixUuidGenerator(
  private val config: PrefixUuid,
) : IdentifierGenerator {
  override fun generate(session: SharedSessionContractImplementor?, `object`: Any?): Any {
    return "${config.prefix}${UUID.randomUUID()}"
  }
}

@IdGeneratorType(PrefixUuidGenerator::class)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrefixUuid(
  val prefix: String,
)
