package com.ask.compress

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RestController
class CompressController {
  @GetMapping("/")
  fun zipOrCompress(useZip: Boolean = true): ResponseEntity<Resource> {
    val zipBytes = if (useZip) {
      createZipInMemoryWithoutCompress()
    } else {
      createZipInMemoryWithCompress()
    }

    val resource = ByteArrayResource(zipBytes)

    return ResponseEntity.ok()
      .header(
        HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
          .filename("이미지-useZip-${useZip}.zip", Charsets.UTF_8)
          .build()
          .toString()
      )
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .body(resource)
  }

  private fun createZipInMemoryWithoutCompress(): ByteArray {
    val files = listOf("images/ddd.png", "images/kotlin.png", "images/solid.png")
    val byteStream = ByteArrayOutputStream()

    ZipOutputStream(byteStream).use { zipOut ->
      for (path in files) {
        val resource = ClassPathResource(path)
        val entry = ZipEntry(resource.filename ?: throw IllegalArgumentException("name must not be null"))
        zipOut.putNextEntry(entry)
        resource.inputStream.use { it.copyTo(zipOut) }
        zipOut.closeEntry()
      }
    }
    return byteStream.toByteArray()
  }

  private fun createZipInMemoryWithCompress(): ByteArray {
    val files = listOf("images/ddd.png", "images/kotlin.png", "images/solid.png")
    val byteStream = ByteArrayOutputStream()

    ZipArchiveOutputStream(byteStream).use { zipOut ->
      for (filePath in files) {
        val resource = ClassPathResource(filePath)
        val entry = ZipArchiveEntry(resource.filename)
        zipOut.putArchiveEntry(entry)
        resource.inputStream.use { it.copyTo(zipOut) }
        zipOut.closeArchiveEntry()
      }
    }
    return byteStream.toByteArray()
  }
}
