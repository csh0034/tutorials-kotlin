package com.ask.core.scheduling

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.scheduling.support.CronTrigger
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * 처음 실행될때 nextExecution 을 통해 계산된 값이 taskScheduler.schedule 로 동작되므로 변경된 값이 적용되려면 해당시간이 되어야만 한다.
 */
@RestController
@RequestMapping("/dynamic-scheduler")
class DynamicScheduler : SchedulingConfigurer {
  private val log = LoggerFactory.getLogger(javaClass)
  private var cronExpression: String = "0/5 * * * * *"

  override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
    taskRegistrar.addTriggerTask({
      log.info("작업 실행: ${LocalDateTime.now()} (cron=$cronExpression)")
    }) { triggerContext ->
      CronTrigger(cronExpression, StringUtils.parseTimeZoneString("Asia/Seoul")).nextExecution(triggerContext)
    }
  }

  @GetMapping("/update")
  fun updateCron(@RequestParam cron: String): String {
    cronExpression = cron
    return "Cron updated to: $cron"
  }
}
