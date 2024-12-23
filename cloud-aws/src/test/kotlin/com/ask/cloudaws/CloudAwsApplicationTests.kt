package com.ask.cloudaws

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest

@SpringBootTest
class CloudAwsApplicationTests {
  @Autowired
  private lateinit var resourceLoader: ResourceLoader

  @Autowired
  private lateinit var s3Client: S3Client

  @Test
  fun `업로드`() {
    val resource = resourceLoader.getResource("s3://web/test123123.log") as WritableResource
    resource.outputStream.use {
      it.write("123123123123".toByteArray())
    }
  }

  @Test
  fun `삭제`() {
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket("web").key("test123123.log").build())
  }
}
