package com.ask.javers.core

import org.assertj.core.api.Assertions.assertThat
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.InitialValueChange
import org.javers.core.diff.changetype.TerminalValueChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.repository.jql.QueryBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JaversCoreTest {
  private lateinit var javers: Javers

  @BeforeEach
  fun setup() {
    javers = JaversBuilder.javers().build()
  }

  @Test
  fun `단순 객체 비교 - 변경 없음`() {
    // given
    val person1 = Person(name = "John", age = 30)
    val person2 = Person(name = "John", age = 30)

    // when
    val diff = javers.compare(person1, person2)

    // then
    assertThat(diff.changes).isEmpty()
  }

  @Test
  fun `단순 객체 비교 - 필드 값 변경 감지`() {
    // given
    val oldPerson = Person(name = "John", age = 30)
    val newPerson = Person(name = "John", age = 31)

    // when
    val diff = javers.compare(oldPerson, newPerson)

    // then
    assertThat(diff.changes).hasSize(1)

    val change = diff.changes[0] as ValueChange
    assertThat(change.propertyName).isEqualTo("age")
    assertThat(change.left).isEqualTo(30)
    assertThat(change.right).isEqualTo(31)
  }

  @Test
  fun `여러 필드 변경 감지`() {
    // given
    val oldPerson = Person(name = "John", age = 30)
    val newPerson = Person(name = "Jane", age = 31)

    // when
    val diff = javers.compare(oldPerson, newPerson)

    // then
    assertThat(diff.changes).hasSize(2)

    val changes = diff.changes
    assertThat(changes).anyMatch { it is ValueChange && it.propertyName == "name" }
    assertThat(changes).anyMatch { it is ValueChange && it.propertyName == "age" }
  }

  @Test
  fun `중첩 객체 비교`() {
    // given
    val address1 = Address(street = "Main St", city = "New York")
    val address2 = Address(street = "Second St", city = "New York")

    val oldPerson = PersonWithAddress(name = "John", address = address1)
    val newPerson = PersonWithAddress(name = "John", address = address2)

    // when
    val diff = javers.compare(oldPerson, newPerson)

    // then
    assertThat(diff.changes).hasSize(1)

    val change = diff.changes[0] as ValueChange
    assertThat(change.propertyName).isEqualTo("street")
    assertThat(change.left).isEqualTo("Main St")
    assertThat(change.right).isEqualTo("Second St")
  }

  @Test
  fun `컬렉션 비교 - 리스트 요소 추가`() {
    // given
    val oldPerson = PersonWithSkills(name = "John", skills = listOf("Java", "Kotlin"))
    val newPerson = PersonWithSkills(name = "John", skills = listOf("Java", "Kotlin", "Python"))

    // when
    val diff = javers.compare(oldPerson, newPerson)

    // then
    assertThat(diff.changes).isNotEmpty
  }

  @Test
  fun `컬렉션 비교 - 리스트 요소 제거`() {
    // given
    val oldPerson = PersonWithSkills(name = "John", skills = listOf("Java", "Kotlin", "Python"))
    val newPerson = PersonWithSkills(name = "John", skills = listOf("Java", "Kotlin"))

    // when
    val diff = javers.compare(oldPerson, newPerson)

    // then
    assertThat(diff.changes).isNotEmpty
  }

  @Test
  fun `null 값 처리`() {
    // given
    val oldPerson = PersonWithNullable(name = "John", email = "john@example.com")
    val newPerson = PersonWithNullable(name = "John", email = null)

    // when
    val diff = javers.compare(oldPerson, newPerson)

    // then
    assertThat(diff.changes).hasSize(1)

    val change = diff.changes[0] as ValueChange
    assertThat(change.propertyName).isEqualTo("email")
    assertThat(change.left).isEqualTo("john@example.com")
    assertThat(change.right).isNull()
  }

  @Test
  fun `snapshot 생성 및 조회`() {
    // given
    val person = Person(name = "John", age = 30)

    // when
    javers.commit("author", person)
    val snapshots = javers.findSnapshots(QueryBuilder.byClass(Person::class.java).build())

    // then
    assertThat(snapshots).hasSize(1)

    val snapshot = snapshots[0]
    assertThat(snapshot.getPropertyValue("name")).isEqualTo("John")
    assertThat(snapshot.getPropertyValue("age")).isEqualTo(30)
  }

  @Test
  fun `변경 이력 추적`() {
    // given
    val person = Person(name = "John", age = 30)
    javers.commit("author", person)

    // when - 나이 변경
    val updatedPerson = person.copy(age = 31)
    javers.commit("author", updatedPerson)

    // then
    val changes = javers.findChanges(QueryBuilder.byClass(Person::class.java).build())
    assertThat(changes).hasSize(3)

    val change = changes[0] as ValueChange
    assertThat(change.propertyName).isEqualTo("age")
    assertThat(change.left).isEqualTo(30)
    assertThat(change.right).isEqualTo(31)

    assertThat(changes[1]).isInstanceOf(InitialValueChange::class.java)
    assertThat(changes[2]).isInstanceOf(InitialValueChange::class.java)
  }

  @Test
  fun `새 객체 생성 감지`() {
    // given
    val newPerson = Person(name = "John", age = 30)

    // when
    val diff = javers.compare(null, newPerson)

    // then
    assertThat(diff.changes).isNotEmpty
    assertThat(diff.changes[0]).isInstanceOf(InitialValueChange::class.java)
  }

  @Test
  fun `객체 삭제 감지`() {
    // given
    val person = Person(name = "John", age = 30)

    // when
    val diff = javers.compare(person, null)

    // then
    assertThat(diff.changes).isNotEmpty
    assertThat(diff.changes[0]).isInstanceOf(TerminalValueChange::class.java)
  }
}

data class Person(
  val name: String,
  val age: Int
)

data class Address(
  val street: String,
  val city: String
)

data class PersonWithAddress(
  val name: String,
  val address: Address
)

data class PersonWithSkills(
  val name: String,
  val skills: List<String>
)

data class PersonWithNullable(
  val name: String,
  val email: String?
)
