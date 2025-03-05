package com.ask.core.web

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDateTime

@Controller
@RequestMapping("/cache")
class CacheController {
  @GetMapping
  @ResponseBody
  fun cache(response: HttpServletResponse): String {
//    CacheControl.noCache().toString()
    response.addHeader(HttpHeaders.CACHE_CONTROL, "max-age=5, must-revalidate")
    return LocalDateTime.now().toString()
  }

  @GetMapping("/index")
  fun index() = "cache/index"
}
