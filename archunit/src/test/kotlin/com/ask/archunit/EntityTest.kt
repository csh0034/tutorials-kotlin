package com.ask.archunit

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.conditions.ArchConditions.be
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.MappedSuperclass
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
  val haveEnumFieldsWithEnumeratedString = ArchRuleDefinition.fields()
    .that().areDeclaredInClassesThat().areAnnotatedWith(Entity::class.java)
    .or().areDeclaredInClassesThat().areAnnotatedWith(MappedSuperclass::class.java)
    .and().haveRawType(assignableTo(Enum::class.java))
    .and().areNotAnnotatedWith(Transient::class.java)
    .should(be(describe("annotated with @Enumerated(EnumType.STRING)") {
      val annotation = it.tryGetAnnotationOfType(Enumerated::class.java)
      annotation.isPresent && annotation.get().value == EnumType.STRING
    }))
}

private class DomainImportOption : ImportOption {
  override fun includes(location: Location) = location.contains("/domain/")
}
