package com.ask.archunit

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures
import org.junit.jupiter.api.Test

class LayeredTest {
  @Test
  fun layered() {
    val classes = ClassFileImporter().importPackages("com.ask.archunit.layered")
    val rule = Architectures.layeredArchitecture()
      .consideringAllDependencies()
      .layer("Controller").definedBy("..web..")
      .layer("Service").definedBy("..service..")
      .layer("Repository").definedBy("..repository..")
      .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
      .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
      .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
    rule.check(classes)
  }

  @Test
  fun `서비스간 참조 방지`() {
    val classes = ClassFileImporter().importPackages("com.ask.archunit.layered")
    val rule = ArchRuleDefinition.noClasses()
      .that().resideInAPackage("..service..")
      .should().dependOnClassesThat().resideInAPackage("..service..")
      .because("Services should be independent and not directly reference other services in the same layer.")
    rule.check(classes)
  }
}

@AnalyzeClasses(packagesOf = [ArchunitApplication::class])
class LayeredTest2 {
  @ArchTest
  val `layerd 의존성 검사` = Architectures.layeredArchitecture()
    .consideringAllDependencies()
    .layer("Controller").definedBy("..web..")
    .layer("Service").definedBy("..service..")
    .layer("Repository").definedBy("..repository..")
    .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
    .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
    .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
}

@AnalyzeClasses(packagesOf = [ArchunitApplication::class])
class LayeredTest3 {
  @ArchTest
  fun `layerd 의존성 검사`(classes: JavaClasses) {
    val rule = Architectures.layeredArchitecture()
      .consideringAllDependencies()
      .layer("Controller").definedBy("..web..")
      .layer("Service").definedBy("..service..")
      .layer("Repository").definedBy("..repository..")
      .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
      .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
      .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
    rule.check(classes)
  }
}

@AnalyzeClasses(packagesOf = [ArchunitApplication::class])
class LayeredTest4 {
  @ArchTest
  fun `layerd 의존성 검사`(classes: JavaClasses) {
    // 패키지 경로 정의
    val controllerPackage = "..web.."
    val servicePackage = "..service.."
    val repositoryPackage = "..repository.."

    val noLayerShouldAccessController = ArchRuleDefinition.noClasses()
      .that().resideInAPackage(servicePackage).or().resideInAPackage(repositoryPackage) // Repository도 Controller에 의존하면 안 됨
      .should().dependOnClassesThat().resideInAPackage(controllerPackage)
      .because("Controller layer should not be accessed by Service or Repository layers.")

    val noRepositoryShouldAccessService = ArchRuleDefinition.noClasses()
      .that().resideInAPackage(repositoryPackage)
      .should().dependOnClassesThat().resideInAPackage(servicePackage)
      .because("Repository layer should not access Service layer.")

    val noControllerShouldAccessRepository = ArchRuleDefinition.noClasses()
      .that().resideInAPackage(controllerPackage)
      .should().dependOnClassesThat().resideInAPackage(repositoryPackage)
      .because("Controller layer should not access Repository layer directly.")


    noLayerShouldAccessController.check(classes)
    noRepositoryShouldAccessService.check(classes)
    noControllerShouldAccessRepository.check(classes)
  }
}
