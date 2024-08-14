package com.ask.errorhandling.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
  private val log = LoggerFactory.getLogger(javaClass)

  @ExceptionHandler
  fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
    printWarnLog(ErrorCode.PARAMETER_INVALID, ex.messages())
    return ResponseEntity(ErrorResponse.of(ErrorCode.PARAMETER_INVALID, ex.messages()), HttpStatus.BAD_REQUEST)
  }

  private fun MethodArgumentNotValidException.messages(): String {
    val fieldErrors = bindingResult.fieldErrors
    return if (fieldErrors.size == 1) {
      fieldErrors[0].defaultMessage.orEmpty()
    } else {
      fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage.orEmpty()}" }
    }
  }

  @ExceptionHandler
  fun baseException(ex: BaseException): ResponseEntity<ErrorResponse> {
    printWarnLog(ex.errorCode, ex.message)
    return ResponseEntity(ErrorResponse.from(ex), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler
  fun handle(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
    printWarnLog(ErrorCode.PARAMETER_INVALID, ex.message)
    return ResponseEntity(ErrorResponse.of(ErrorCode.PARAMETER_INVALID, "parameter invalid"), HttpStatus.BAD_REQUEST)
  }

  private fun printWarnLog(errorCode: ErrorCode, message: String?) {
    log.warn("errorCode: ${errorCode.code}, errorMessage: $message")
  }

  @ExceptionHandler
  fun exception(ex: Exception): ResponseEntity<ErrorResponse> {
    log.error("errorCode: ${ErrorCode.UNKNOWN.code}, errorMessage: ${ex.message}", ex)
    return ResponseEntity(ErrorResponse.of(ErrorCode.UNKNOWN, "unknown exception"), HttpStatus.INTERNAL_SERVER_ERROR)
  }
}
