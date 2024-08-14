package com.ask.errorhandling.exception

open class BaseException(
  val errorCode: ErrorCode = ErrorCode.UNKNOWN,
  message: String? = null,
  val payload: Any? = null,
  cause: Throwable? = null,
) : RuntimeException(message, cause)
