package com.ask.errorhandling.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(value = JsonInclude.Include.NON_NULL)
data class ErrorResponse(
  val errorCode: String,
  val message: String?,
  val payload: Any? = null,
) {
  companion object {
    fun from(e: BaseException) = ErrorResponse(e.errorCode.code, e.message, e.payload)
    fun of(errorCode: ErrorCode, message: String) = ErrorResponse(errorCode.code, message)
  }
}
