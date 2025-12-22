package com.ask.javers.core

import org.assertj.core.api.Assertions.assertThat
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.repository.jql.QueryBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class JaversAdvancedTest {
  private lateinit var javers: Javers

  @BeforeEach
  fun setup() {
    javers = JaversBuilder.javers().build()
  }

  @Test
  fun `Entity 타입 비교`() {
    // given
    val employee1 = Employee(id = 1, name = "John", department = "IT")
    val employee2 = Employee(id = 1, name = "John", department = "HR")

    // when
    val diff = javers.compare(employee1, employee2)

    // then
    assertThat(diff.changes).hasSize(1)

    val change = diff.changes[0] as ValueChange
    assertThat(change.propertyName).isEqualTo("department")
    assertThat(change.left).isEqualTo("IT")
    assertThat(change.right).isEqualTo("HR")
  }

  @Test
  fun `ValueObject 타입 비교`() {
    // given
    val money1 = Money(amount = 100.0, currency = "USD")
    val money2 = Money(amount = 150.0, currency = "USD")

    // when
    val diff = javers.compare(money1, money2)

    // then
    assertThat(diff.changes).hasSize(1)

    val change = diff.changes[0] as ValueChange
    assertThat(change.propertyName).isEqualTo("amount")
    assertThat(change.left).isEqualTo(100.0)
    assertThat(change.right).isEqualTo(150.0)
  }

  @Test
  fun `복잡한 객체 그래프 비교`() {
    // given
    val department1 = Department(
      id = 1,
      name = "Engineering",
      manager = Employee(id = 10, name = "Alice", department = "Engineering")
    )
    val department2 = Department(
      id = 1,
      name = "Engineering",
      manager = Employee(id = 10, name = "Bob", department = "Engineering")
    )

    // when
    val diff = javers.compare(department1, department2)

    // then
    assertThat(diff.changes).isNotEmpty

    val nameChange = diff.changes[0] as ValueChange

    assertThat(nameChange).isNotNull()
    assertThat(nameChange.left).isEqualTo("Alice")
    assertThat(nameChange.right).isEqualTo("Bob")
  }

  @Test
  fun `Map 컬렉션 비교 - 값 변경`() {
    // given
    val config1 = Configuration(
      id = 1,
      settings = mapOf("theme" to "dark", "language" to "en")
    )
    val config2 = Configuration(
      id = 1,
      settings = mapOf("theme" to "light", "language" to "en")
    )

    // when
    val diff = javers.compare(config1, config2)

    // then
    assertThat(diff.changes).isNotEmpty
  }

  @Test
  fun `Map 컬렉션 비교 - 키 추가`() {
    // given
    val config1 = Configuration(
      id = 1,
      settings = mapOf("theme" to "dark")
    )
    val config2 = Configuration(
      id = 1,
      settings = mapOf("theme" to "dark", "language" to "en")
    )

    // when
    val diff = javers.compare(config1, config2)

    // then
    assertThat(diff.changes).isNotEmpty
  }

  @Test
  fun `Set 컬렉션 비교`() {
    // given
    val team1 = Team(
      id = 1,
      name = "Backend",
      members = setOf("John", "Jane", "Bob")
    )
    val team2 = Team(
      id = 1,
      name = "Backend",
      members = setOf("John", "Jane", "Alice")
    )

    // when
    val diff = javers.compare(team1, team2)

    // then
    assertThat(diff.changes).isNotEmpty
  }

  @Test
  fun `날짜 타입 비교`() {
    // given
    val event1 = Event(
      id = 1,
      name = "Conference",
      date = LocalDate.of(2025, 1, 1)
    )
    val event2 = Event(
      id = 1,
      name = "Conference",
      date = LocalDate.of(2025, 1, 2)
    )

    // when
    val diff = javers.compare(event1, event2)

    // then
    assertThat(diff.changes).hasSize(1)

    val change = diff.changes[0] as ValueChange
    assertThat(change.propertyName).isEqualTo("date")
    assertThat(change.left).isEqualTo(LocalDate.of(2025, 1, 1))
    assertThat(change.right).isEqualTo(LocalDate.of(2025, 1, 2))
  }

  @Test
  fun `복잡한 리스트 비교 - 순서 변경`() {
    // given
    val project1 = Project(
      id = 1,
      name = "Project A",
      tasks = listOf("Task 1", "Task 2", "Task 3")
    )
    val project2 = Project(
      id = 1,
      name = "Project A",
      tasks = listOf("Task 1", "Task 3", "Task 2")
    )

    // when
    val diff = javers.compare(project1, project2)

    // then
    assertThat(diff.changes).isNotEmpty
  }

  @Test
  fun `여러 커밋 이력 추적`() {
    // given
    val employee = Employee(id = 1, name = "John", department = "IT")

    // when
    javers.commit("admin", employee)
    javers.commit("admin", employee.copy(department = "HR"))
    javers.commit("admin", employee.copy(name = "John Doe", department = "HR"))

    // then
    val snapshots = javers.findSnapshots(QueryBuilder.byClass(Employee::class.java).build())
    assertThat(snapshots).hasSize(3)

    val changes = javers.findChanges(QueryBuilder.byClass(Employee::class.java).build())
    assertThat(changes).hasSizeGreaterThanOrEqualTo(2)
  }

  @Test
  fun `특정 속성 무시 설정`() {
    // given
    val javersWithIgnore = JaversBuilder.javers()
      .registerIgnoredClass(String::class.java)
      .build()

    val person1 = SimplePerson(id = 1, name = "John", age = 30)
    val person2 = SimplePerson(id = 1, name = "Jane", age = 31)

    // when
    val diff = javersWithIgnore.compare(person1, person2)

    assertThat(diff.changes).hasSize(1)
  }
}

// 테스트용 도메인 모델
@Entity
@TypeName("Employee")
data class Employee(
  @Id val id: Int,
  val name: String,
  val department: String
)

@ValueObject
data class Money(
  val amount: Double,
  val currency: String
)

@Entity
data class Department(
  @Id val id: Int,
  val name: String,
  val manager: Employee
)

@Entity
data class Configuration(
  @Id val id: Int,
  val settings: Map<String, String>
)

@Entity
data class Team(
  @Id val id: Int,
  val name: String,
  val members: Set<String>
)

@Entity
data class Event(
  @Id val id: Int,
  val name: String,
  val date: LocalDate
)

@Entity
data class Project(
  @Id val id: Int,
  val name: String,
  val tasks: List<String>
)

data class SimplePerson(
  @Id val id: Int,
  val name: String,
  val age: Int
)

@Entity
data class Account(
  @Id val id: Int,
  val balance: Double
)
