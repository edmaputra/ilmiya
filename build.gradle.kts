plugins {
    java
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.ilmiya"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.3")
        }
    }

    dependencies {
        // Annotation Processors & Mapping (Using pure Java 25 Records + MapStruct)
        "implementation"("org.mapstruct:mapstruct:1.6.3")
        "annotationProcessor"("org.mapstruct:mapstruct-processor:1.6.3")

        // Validation & Logging
        "implementation"("jakarta.validation:jakarta.validation-api")
        "implementation"("org.slf4j:slf4j-api")

        // Testing
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testImplementation"("org.mockito:mockito-core")
        "testImplementation"("org.mockito:mockito-junit-jupiter")
        "testImplementation"("org.assertj:assertj-core")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
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
