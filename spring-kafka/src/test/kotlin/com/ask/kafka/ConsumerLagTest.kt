package com.ask.kafka

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.ListConsumerGroupsOptions
import org.apache.kafka.clients.admin.OffsetSpec
import org.apache.kafka.common.ConsumerGroupState
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ConsumerLagTest {
  private val log = LoggerFactory.getLogger(javaClass)

  lateinit var adminClient: AdminClient

  @BeforeEach
  fun setUp() {
    adminClient = AdminClient.create(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"))
  }

  @Test
  fun `모든 컨슈머 그룹 조회`() {
    val groups = adminClient.listConsumerGroups().valid().get()
    groups.forEach { log.info("group: {}", it) }
  }

  @Test
  fun `모든 토픽 조회`() {
    val topicNames = adminClient.listTopics().names().get()
    topicNames.forEach { log.info("topic: {}", it) }
  }

  @Test
  fun `특정 토픽 메세지 수 조회`() {
    val topicName = "demo.topic"
    val topicDescription = adminClient.describeTopics(listOf(topicName))
      .allTopicNames()
      .get()[topicName] ?: throw IllegalArgumentException("topic not found: $topicName")

    val partitions = topicDescription.partitions().map { TopicPartition(topicName, it.partition()) }
    val earliestOffsets = adminClient.listOffsets(partitions.associateWith { OffsetSpec.earliest() }).all().get()
    val latestOffsets = adminClient.listOffsets(partitions.associateWith { OffsetSpec.latest() }).all().get()

    val sum = partitions.sumOf {
      val earliestOffset = earliestOffsets[it]?.offset() ?: 0
      val latestOffset = latestOffsets[it]?.offset() ?: 0
      latestOffset - earliestOffset
    }

    log.info("sum: {}", sum)
  }

  @Test
  fun `모든 컨슈머그룹 lag 조회`() {
    val options = ListConsumerGroupsOptions().inStates(setOf(ConsumerGroupState.STABLE))
    val consumerGroups = adminClient.listConsumerGroups(options).valid().get()

    for (groupId in consumerGroups.map { it.groupId() }) {
      log.info("=== Consumer Group: $groupId ===")

      val consumedOffsets = adminClient.listConsumerGroupOffsets(groupId)
        .partitionsToOffsetAndMetadata()
        .get()

      if (consumedOffsets.isEmpty()) {
        log.info("(오프셋 정보 없음)")
        continue
      }

      val topicPartitionOffsets = consumedOffsets.keys.associateWith { OffsetSpec.latest() }
      val latestOffsets = adminClient.listOffsets(topicPartitionOffsets).all().get()

      consumedOffsets.forEach { (tp, consumedMeta) ->
        val consumed = consumedMeta.offset()
        val latest = latestOffsets[tp]?.offset() ?: -1L
        val lag = if (latest >= 0) latest - consumed else -1L

        log.info("Topic: ${tp.topic()}, Partition: ${tp.partition()}, Consumed: ${consumed}, Latest: $latest Lag: $lag")
      }
    }
  }
}
