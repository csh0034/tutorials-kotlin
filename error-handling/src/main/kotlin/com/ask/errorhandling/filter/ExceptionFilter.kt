package com.ask.errorhandling.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ExceptionFilter : OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    if (request.getParameter("exception") == "true") {
      throw IllegalStateException("필터에서 예외 발생 !!!")
    }

    filterChain.doFilter(request, response)
  }
}
