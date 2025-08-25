package com.ask.core.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(value = "sampleClient", url = "https://localhost:8082")
interface SampleClient {
  /**
   * RequestTemplateFactoryResolver.addQueryMapQueryParameters 에서 List 도 처리함
   */
  @GetMapping
  fun get(@SpringQueryMap dto: SampleDto): String
}

data class SampleDto(
  val id: Long,
  val names: List<String>,
)
