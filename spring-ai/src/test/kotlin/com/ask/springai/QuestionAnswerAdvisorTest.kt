package com.ask.springai

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.template.st.StTemplateRenderer
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class QuestionAnswerAdvisorTest {
  private val log = LoggerFactory.getLogger(javaClass)

  @Autowired
  lateinit var chatClientBuilder: ChatClient.Builder

  @Autowired
  lateinit var vectorStore: VectorStore

  @Test
  fun rag() {
    val advisor = QuestionAnswerAdvisor.builder(vectorStore)
      .searchRequest(SearchRequest.builder().similarityThreshold(0.5).topK(3).build())
      .build()

    val chatClient = chatClientBuilder
      .defaultAdvisors(advisor)
      .build()

    val userInput = "연봉 5,000 만원인 거주자 종합소득세는?"

    val message = chatClient.prompt()
      .user(userInput)
      .call()
      .content()

    log.info("message: $message")
  }

  @Test
  fun `rag with custom prompt`() {
    val customPromptTemplate = PromptTemplate.builder()
      .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
      .template("""
        <query>

        아래는 참고할 문맥 정보입니다.
        한국어로 답변해주세요.
        
        ---------------------
        <question_answer_context>
        ---------------------
        
        다음 규칙을 따라 질문에 답하세요.
        
        1. 문맥 정보에 답이 없으면 "잘 모르겠습니다."라고 답합니다.  
        2. "문맥에 따르면…" 또는 "제공된 정보에 따르면…"과 같은 표현은 사용하지 마세요.
        """.trimIndent()
      )
      .build()

    val advisor = QuestionAnswerAdvisor.builder(vectorStore)
      .searchRequest(SearchRequest.builder().similarityThreshold(0.5).topK(3).build())
      .promptTemplate(customPromptTemplate)
      .build()

    val chatClient = chatClientBuilder
      .defaultAdvisors(advisor)
      .build()

    val userInput = "연봉 5000 만원인 직장인 종합소득세는?"

    val message = chatClient.prompt()
      .user(userInput)
      .call()
      .content()

    log.info("message: $message")
  }
}
