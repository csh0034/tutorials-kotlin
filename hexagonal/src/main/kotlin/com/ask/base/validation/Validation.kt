package com.ask.base.validation

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation.buildDefaultValidatorFactory
import jakarta.validation.Validator

object Validation {
  // Your IDE may complain that the ValidatorFactory needs to be closed, but if we do that here,
  // we break the contract of ValidatorFactory#close.
  private val validator: Validator = buildDefaultValidatorFactory().validator

  /**
   * Evaluates all Bean Validation annotations on the subject.
   */
  fun <T> validate(subject: T) {
    val violations: Set<ConstraintViolation<T>> = validator.validate(subject)
    if (violations.isNotEmpty()) {
      throw ConstraintViolationException(violations)
    }
  }
}
