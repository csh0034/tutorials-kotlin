package com.ask.jpa.config

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.type.StandardBasicTypes

private const val SECRET = "secret"

class EncryptionFunctionContributor : FunctionContributor {
  override fun contributeFunctions(functionContributions: FunctionContributions) {
    val functionRegistry = functionContributions.functionRegistry
    val basicType = functionContributions.typeConfiguration.basicTypeRegistry.resolve(StandardBasicTypes.STRING)

    functionRegistry.registerPattern(
      "encrypt_with_key",
      "TO_BASE64(AES_ENCRYPT(?1, '$SECRET'))",
      basicType,
    )

    // 복호화 함수 등록
    functionRegistry.registerPattern(
      "decrypt_with_key",
      "CAST(AES_DECRYPT(FROM_BASE64(?1), '$SECRET') AS CHAR)",
      basicType,
    )
  }
}
