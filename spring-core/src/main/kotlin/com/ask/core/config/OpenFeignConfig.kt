package com.ask.core.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients("com.ask.core")
class OpenFeignConfig
