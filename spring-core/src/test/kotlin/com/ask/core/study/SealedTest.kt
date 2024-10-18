package com.ask.core.study

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * - sealed interface: kotlin 1.5+
 * - data object: kotlin 1.9+
 */
class SealedTest {
  private val jacksonObjectMapper = jacksonObjectMapper()

  @Test
  fun `richtext 메세지 파싱`() {
    val chatMessage = ChatMessage(Container.RICHTEXT, listOf(RichText.Text("text1"), RichText.Section(listOf(RichText.Text("text2")))))
    val json = jacksonObjectMapper.writeValueAsString(chatMessage)
    val value = jacksonObjectMapper.readValue<ChatMessage>(json)

    assertThat(json).isEqualTo("""{"type":"richtext","elements":[{"type":"text","text":"text1"},{"type":"section","elements":[{"type":"text","text":"text2"}]}]}""")
    assertThat(value).isEqualTo(chatMessage)
  }

  @Test
  fun `system 메세지 파싱`() {
    val chatMessage = ChatMessage(Container.SYSTEM, listOf(System.Add(AddData("aaa", listOf("bbb", "ccc")))))
    val json = jacksonObjectMapper.writeValueAsString(chatMessage)
    val value = jacksonObjectMapper.readValue<ChatMessage>(json)

    assertThat(json).isEqualTo("""{"type":"system","elements":[{"type":"add","data":{"inviter":"aaa","invitee":["bbb","ccc"]}}]}""")
    assertThat(value).isEqualTo(chatMessage)
  }

  @Test
  fun `vote 메세지 파싱`() {
    val chatMessage = ChatMessage(Container.VOTE, listOf(Vote.End))
    val json = jacksonObjectMapper.writeValueAsString(chatMessage)
    val value = jacksonObjectMapper.readValue<ChatMessage>(json)

    assertThat(json).isEqualTo("""{"type":"vote","elements":[{"type":"end"}]}""")
    assertThat(value).isEqualTo(chatMessage)
  }
}

data class ChatMessage(
  val type: Container,
  val elements: List<Element>,
)

enum class Container {
  RICHTEXT, SYSTEM, VOTE;

  @JsonValue
  private fun lowercase() = this.name.lowercase()
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = RichText::class),
  JsonSubTypes.Type(value = System::class),
  JsonSubTypes.Type(value = Vote::class),
)
sealed interface Element

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = RichText.Section::class, name = "section"),
  JsonSubTypes.Type(value = RichText.Text::class, name = "text"),
)
sealed class RichText : Element {
  data class Section(val elements: List<RichText>) : RichText()
  data class Text(val text: String) : RichText()
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "key")
@JsonSubTypes(
  JsonSubTypes.Type(value = System.Create::class, name = "create"),
  JsonSubTypes.Type(value = System.Add::class, name = "add"),
)
sealed class System : Element {
  data class Create(val data: CreateData) : System()
  data class Add(val data: AddData) : System()
}

data class CreateData(
  val inviter: String,
  val invitee: List<String>,
)

data class AddData(
  val inviter: String,
  val invitee: List<String>,
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "key")
@JsonSubTypes(
  JsonSubTypes.Type(value = Vote.Create::class, name = "create"),
  JsonSubTypes.Type(value = Vote.End::class, name = "end"),
  JsonSubTypes.Type(value = Vote.Remind::class, name = "remind"),
)
sealed class Vote : Element {
  data object Create : Vote()
  data object End : Vote()
  data object Remind : Vote()
}

// -------------------------------------------------------

sealed interface LoginError {
  data object IncorrectPasswordError : LoginError
  data object UserNotFoundError : LoginError
}

sealed interface HttpError {
  data object GetUserListError : HttpError
  data object GetUserLikesError : HttpError
}

sealed interface CommonError : LoginError, HttpError {
  object ServerError : CommonError
  object FileReadError : CommonError
}

private fun handleLoginError(loginError: LoginError) {
  when (loginError) {
    CommonError.FileReadError -> TODO()
    CommonError.ServerError -> TODO()
    LoginError.IncorrectPasswordError -> TODO()
    LoginError.UserNotFoundError -> TODO()
  }
}

private fun handleHttpError(httpError: HttpError) {
  when (httpError) {
    CommonError.FileReadError -> TODO()
    CommonError.ServerError -> TODO()
    HttpError.GetUserLikesError -> TODO()
    HttpError.GetUserListError -> TODO()
  }
}

private fun handleCommonError(commonError: CommonError) {
  when (commonError) {
    CommonError.FileReadError -> TODO()
    CommonError.ServerError -> TODO()
  }
}

