package com.ask.archunit

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.jupiter.api.Test

class PackageTest {
  @Test
  fun `특정 패키지의 하위 패키지 제한`() {
    val classes = ClassFileImporter().importPackages("com.ask.archunit.layered")
    val rule = ArchRuleDefinition.classes()
      .that().resideInAPackage("com.ask.archunit.layered..")
      .should().resideInAnyPackage(
        "com.ask.archunit.layered.web..",
        "com.ask.archunit.layered.service..",
        "com.ask.archunit.layered.repository..",
      )
    rule.check(classes)
  }
}
