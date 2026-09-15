package io.github.edmaputra.ilmiya.app.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

/**
 * Architectural layer boundary tests enforcing Hexagonal Architecture constraints across Ilmiya.
 *
 * @author edmaputra
 * @since 0.0.1
 */
@AnalyzeClasses(
    packages = ["io.github.edmaputra.ilmiya"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ArchitectureTest {

    @ArchTest
    val domainMustNotDependOnSpring: ArchRule =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..")

    @ArchTest
    val domainMustNotDependOnJpa: ArchRule =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("jakarta.persistence..", "org.hibernate..")

    @ArchTest
    val domainMustNotDependOnApplicationOrAdapter: ArchRule =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..adapter..")

    @ArchTest
    val applicationMustNotDependOnAdapters: ArchRule =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..adapter..")

    @ArchTest
    val controllersMustNotAccessPersistence: ArchRule =
        noClasses().that().resideInAPackage("..adapter.web..")
            .should().dependOnClassesThat()
            .resideInAPackage("..adapter.persistence..")
}
