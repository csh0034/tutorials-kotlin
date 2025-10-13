package com.ask.archunit

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

/**
 * fields 로 접근할 경우 superclass 필드는 검사하지 못함
 */
@AnalyzeClasses(
  packagesOf = [ArchunitApplication::class],
  importOptions = [ImportOption.DoNotIncludeTests::class, DomainImportOption::class]
)
class EntityTest {
  @ArchTest
  val enumFields_should_use_Enumerated_String = ArchRuleDefinition.classes()
    .that().areAnnotatedWith(Entity::class.java)
//    .and().containAnyFieldsThat(rawType(assignableTo(Enum::class.java))) // superclass 필드 제외됨
    .should(haveEnumFieldsWithEnumeratedString())

  private fun haveEnumFieldsWithEnumeratedString(): ArchCondition<JavaClass> {
    return object : ArchCondition<JavaClass>("have enum fields with @Enumerated(EnumType.STRING)") {
      override fun check(javaClass: JavaClass, events: ConditionEvents) {
        javaClass.allFields // allFields 사용해야 superclass 필드도 대상이됨
          .filter { it.rawType.isEnum && it.isAnnotatedWith(Column::class.java) }
          .forEach {
            val enumerated = it.tryGetAnnotationOfType(Enumerated::class.java)
            if (enumerated.isEmpty || enumerated.get().value != EnumType.STRING) {
              val message = "[${javaClass.simpleName}.${it.name}] does not have @Enumerated(EnumType.STRING) annotation"
              events.add(SimpleConditionEvent.violated(it, message))
            }
          }
      }
    }
  }
}

private class DomainImportOption : ImportOption {
  override fun includes(location: Location) = location.contains("/domain/")
}
