package com.ask.springaimcpserver.tool

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.mcp.McpToolUtils
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DateTimeTool {
  private val log = LoggerFactory.getLogger(javaClass)

  @Tool(name = "current-date", description = "현재 날짜와 시간 정보를 제공합니다")
  fun getCurrentDateTime(): String {
    val now = LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString()
    log.info("call now: {}", now)
    return now
  }

  @Tool(name = "set-alarm", description = "ISO-8601 형식으로 제공되는 주어진 시간 동안 사용자 알람 설정합니다.")
  fun setAlarm(@ToolParam(description = "ISO-8601 형식의 시간") time: String, toolContext: ToolContext) {
    val alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME)

    log.info("Alarm set for {}", alarmTime)

    val mcpExchange = McpToolUtils.getMcpExchange(toolContext).orElseThrow()
    // todo ...
  }
}
