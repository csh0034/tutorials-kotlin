package com.ask.springai

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RetrievalAugmentationAdvisorTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Autowired
  lateinit var chatMemory: ChatMemory

  @Autowired
  lateinit var vectorStore: VectorStore

  @BeforeEach
  fun setUp() {
    chatClientBuilder.defaultAdvisors(SimpleLoggerAdvisor())

    chatMemory.add("cid", UserMessage("연봉 1400만원인 직장인 종합소득세는?"))
    chatMemory.add("cid", AssistantMessage("과세표준의 6퍼센트 이므로 840,000 입니다."))
  }

  @Test
  fun rag() {
    val messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build()
    val retrievalAugmentationAdvisor = createRetrievalAugmentationAdvisor()

    val chatClient = chatClientBuilder.build()

    val userInput = "5000만원은?"

    val message = chatClient.prompt()
      .user(userInput)
      .advisors(messageChatMemoryAdvisor, retrievalAugmentationAdvisor)
      .advisors { a -> a.param(ChatMemory.CONVERSATION_ID, "cid") }
      .call()
      .content()

    log.info("message: $message")
  }

  private fun createRetrievalAugmentationAdvisor() = RetrievalAugmentationAdvisor.builder()
    .queryTransformers(
      CompressionQueryTransformer.builder()
        .chatClientBuilder(chatClientBuilder)
        .build(),
      TranslationQueryTransformer.builder()
        .chatClientBuilder(chatClientBuilder)
        .targetLanguage("korean")
        .build(),
      RewriteQueryTransformer.builder()
        .chatClientBuilder(chatClientBuilder)
        .build(),
    )
    .queryExpander(
      MultiQueryExpander.builder()
        .chatClientBuilder(chatClientBuilder)
        .numberOfQueries(3)
        .includeOriginal(true) // default
        .build()
    )
    .documentRetriever(
      VectorStoreDocumentRetriever.builder()
        .similarityThreshold(0.5)
        .topK(3)
        .vectorStore(vectorStore)
        .build()
    )
    .queryAugmenter(
      ContextualQueryAugmenter.builder()
        .allowEmptyContext(true)
        .build()
    )
    .build()
}
