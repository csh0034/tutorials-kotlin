package com.ask.kafka.indicator

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.ListConsumerGroupsOptions
import org.apache.kafka.clients.admin.ListOffsetsResult
import org.apache.kafka.clients.admin.OffsetSpec
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.ConsumerGroupState
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

private const val CONSUMER_LANG_COUNT_EXCEED = "802"
private const val CONSUMER_LANG_COUNT_THRESHOLD = 100

@Component
class ConsumerLagIndicator(
  private val adminClient: AdminClient,
) : HealthIndicator {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun health(): Health {
    val builder = Health.up()
    val count = getTotalStableConsumerGroupLag()

    log.info("consumerLagCount: {}, consumerLagThreshold: {}", count, CONSUMER_LANG_COUNT_THRESHOLD)

    builder.withDetail("consumerLagCount", count)
      .withDetail("consumerLagThreshold", CONSUMER_LANG_COUNT_THRESHOLD)

    if (count > CONSUMER_LANG_COUNT_THRESHOLD) {
      builder.down().withDetail("errorCode", CONSUMER_LANG_COUNT_EXCEED)
    }

    return builder.build()
  }

  private fun getTotalStableConsumerGroupLag(): Long {
    val consumerGroupIds = getStableConsumerGroupIds()
    return consumerGroupIds.sumOf { calculateConsumerGroupLag(it) }
  }

  private fun getStableConsumerGroupIds(): Collection<String> {
    val options = ListConsumerGroupsOptions().inStates(setOf(ConsumerGroupState.STABLE))
    return adminClient.listConsumerGroups(options).valid().get().map { it.groupId() }
  }

  private fun calculateConsumerGroupLag(groupId: String): Long {
    val consumerGroupOffsets = getConsumerGroupOffsets(groupId)
    if (consumerGroupOffsets.isEmpty()) return 0L

    val latestOffsets = getLatestOffsets(consumerGroupOffsets.keys)

    return consumerGroupOffsets.entries.sumOf { (partition, consumedMeta) ->
      val consumed = consumedMeta.offset()
      val latest = latestOffsets[partition]?.offset() ?: 0L
      if (latest > 0) latest - consumed else 0L
    }
  }

  private fun getConsumerGroupOffsets(groupId: String): Map<TopicPartition, OffsetAndMetadata> {
    return adminClient.listConsumerGroupOffsets(groupId)
      .partitionsToOffsetAndMetadata()
      .get()
  }

  private fun getLatestOffsets(partitions: Set<TopicPartition>): Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> {
    val topicPartitionOffsets = partitions.associateWith { OffsetSpec.latest() }
    return adminClient.listOffsets(topicPartitionOffsets).all().get()
  }
}
