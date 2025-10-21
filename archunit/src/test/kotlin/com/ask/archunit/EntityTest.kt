package com.ask.archunit

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo
import com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.conditions.ArchConditions.be
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToOne
import jakarta.persistence.Transient

/**
 * fields 로 접근할 경우 superclass 필드는 검사하지 못함
 */
@AnalyzeClasses(
  packagesOf = [ArchunitApplication::class],
  importOptions = [ImportOption.DoNotIncludeTests::class, DomainImportOption::class]
)
class EntityTest {
  @ArchTest
  val `entity enum 타입의 경우 EnumType STRING 이어야 한다` = entityFields()
    .and().haveRawType(assignableTo(Enum::class.java))
    .and().areNotAnnotatedWith(Transient::class.java)
    .should(
      be(
        describe("annotated with @Enumerated(EnumType.STRING)") {
          val annotation = it.tryGetAnnotationOfType(Enumerated::class.java)
          annotation.isPresent && annotation.get().value == EnumType.STRING
        }
      )
    )

  @ArchTest
  val `*ToOne 의 경우 lazy 이어야 한다` = entityFields()
    .and(annotatedWith(ManyToOne::class.java).or(annotatedWith(OneToOne::class.java)))
    .should(
      be(
        describe("fetch with Lazy") {
          val manyToOne = it.tryGetAnnotationOfType(ManyToOne::class.java)
          val oneToOne = it.tryGetAnnotationOfType(OneToOne::class.java)

          val manyToOneLazy = manyToOne.isPresent && manyToOne.get().fetch == FetchType.LAZY
          val oneToOneLazy = oneToOne.isPresent && oneToOne.get().fetch == FetchType.LAZY

          manyToOneLazy || oneToOneLazy
        }
      )
    ).allowEmptyShould(true)

  private fun entityFields() = ArchRuleDefinition.fields()
    .that(declaredIn(annotatedWith(Entity::class.java).or(annotatedWith(MappedSuperclass::class.java))))
}

private class DomainImportOption : ImportOption {
  override fun includes(location: Location) = location.contains("/domain/")
}
