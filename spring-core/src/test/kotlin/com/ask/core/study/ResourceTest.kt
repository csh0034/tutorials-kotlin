package com.ask.core.study

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.core.io.UrlResource
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

/**
 *  @see <a href="https://docs.spring.io/spring-framework/reference/core/resources.html">Resources</a>
 */
class ResourceTest : FunSpec({
  xtest("UrlResource") {
    val resource = UrlResource("https://docs.spring.io/spring-framework/reference/_/img/spring-logo.svg")

    resource.exists() shouldBe true
    resource.filename shouldBe "spring-logo.svg"

    resource.inputStream.use { input ->
      Files.copy(input, Path("image.svg"), StandardCopyOption.REPLACE_EXISTING)
    }
  }
})
