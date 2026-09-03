plugins {
    java
    kotlin("jvm") version "2.3.21" apply false
    kotlin("plugin.spring") version "2.3.21" apply false
    kotlin("plugin.jpa") version "2.3.21" apply false
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "io.github.edmaputra.ilmiya"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.1")
        }
    }

    dependencies {
        // Kotlin Standard Libraries
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "implementation"("tools.jackson.module:jackson-module-kotlin")

        // Annotation Processors & Mapping (MapStruct)
        "implementation"("org.mapstruct:mapstruct:1.6.3")
        "annotationProcessor"("org.mapstruct:mapstruct-processor:1.6.3")

        // Validation & Logging
        "implementation"("jakarta.validation:jakarta.validation-api")
        "implementation"("org.slf4j:slf4j-api")

        // Testing
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testImplementation"("org.assertj:assertj-core")
        "testImplementation"("io.mockk:mockk:1.13.17")
        "testImplementation"("com.ninja-squad:springmockk:4.0.2")
        "testImplementation"("org.mockito:mockito-core")
        "testImplementation"("org.mockito:mockito-junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(
            listOf(
                "-parameters",
                "-Amapstruct.defaultComponentModel=spring",
                "-Amapstruct.unmappedTargetPolicy=IGNORE"
            )
        )
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
