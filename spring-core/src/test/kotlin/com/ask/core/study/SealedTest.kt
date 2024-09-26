package com.ask.core.study

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * - sealed interface: kotlin 1.5+
 * - data object: kotlin 1.9+
 */
class SealedTest {
  @Test
  fun `RichText Section`() {
    val element = RichText.Section(
      listOf(
        RichText.Text("text1"),
        RichText.Section(
          listOf(
            RichText.Text("text2")
          )
        )
      )
    )
    val message = convertToMessage(element)
    assertThat(message).isEqualTo("Section(element=[Text(text=text1), Section(element=[Text(text=text2)])])")
  }

  @Test
  fun `System Create`() {
    val element = System.Create("aaa", listOf("bbb", "ccc"))
    val message = convertToMessage(element)
    assertThat(message).isEqualTo("Create(inviter=aaa, invitee=[bbb, ccc])")
  }

  @Test
  fun `Vote Create`() {
    val element = Vote.Create
    val message = convertToMessage(element)
    assertThat(message).isEqualTo("Create")
  }

  private fun convertToMessage(element: Element) = when (element) {
    is RichText.Section -> element.toString()
    is RichText.Text -> element.toString()
    is System.Create -> element.toString()
    is System.Add -> element.toString()
    Vote.Create -> element.toString()
    Vote.End -> element.toString()
    Vote.Remind -> element.toString()
  }
}

data class ChatMessage(
  val type: Container,
  val element: List<Element>,
)

enum class Container {
  RICHTEXT, SYSTEM, VOTE,
}

sealed interface Element

sealed class RichText : Element {
  data class Section(val element: List<RichText>) : RichText()
  data class Text(val text: String) : RichText()
}

sealed class System : Element {
  data class Create(val inviter: String, val invitee: List<String>) : System()
  data class Add(val inviter: String, val invitee: List<String>) : System()
}

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

