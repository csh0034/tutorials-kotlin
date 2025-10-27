package com.ask.jpa.config

import org.hibernate.resource.jdbc.spi.StatementInspector
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

const val SECRET_KEY_PLACEHOLDER = "__SECRET_KEY__"

@Component
class EncryptionStatementInspector : StatementInspector {
  private val log = LoggerFactory.getLogger(javaClass)

  @Value("\${custom.db-secret-key}")
  private lateinit var secretKey: String

  override fun inspect(sql: String): String {
    if (sql.contains(SECRET_KEY_PLACEHOLDER)) {
      return sql.replace(SECRET_KEY_PLACEHOLDER, "'${secretKey}'").also {
        log.debug("replaced sql: $it")
      }
    }

    return sql
  }
}
