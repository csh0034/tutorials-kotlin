package com.ask.errorhandling.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest

@RestController
@RequestMapping("\${server.error.path:\${error.path:/error}}")
class GlobalErrorController(
  private val errorAttributes: DefaultErrorAttributes
) : ErrorController {
  @RequestMapping
  fun error(request: HttpServletRequest) {
    throw errorAttributes.getError(ServletWebRequest(request))
  }
}
