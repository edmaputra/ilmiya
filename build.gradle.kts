plugins {
    java
    jacoco
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
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")

    jacoco {
        toolVersion = "0.8.14"
    }

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
        "testImplementation"("com.tngtech.archunit:archunit-junit5:1.4.1")
        "testImplementation"("io.mockk:mockk:1.13.17")
        "testImplementation"("com.ninja-squad:springmockk:4.0.2")
        "testImplementation"("org.mockito:mockito-core")
        "testImplementation"("org.mockito:mockito-junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
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

    val jacocoExclusions = listOf(
        "**/config/**",
        "**/*Application*.*",
        "**/*MapperImpl*.*"
    )

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.withType<Test>())
        reports {
            xml.required.set(true)
            csv.required.set(true)
            html.required.set(true)
        }
        classDirectories.setFrom(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(jacocoExclusions)
                }
            }
        )
    }
}

jacoco {
    toolVersion = "0.8.14"
}

val aggregateJacocoExclusions = listOf(
    "**/config/**",
    "**/*Application*.*",
    "**/*MapperImpl*.*"
)

tasks.register<JacocoReport>("jacocoAggregateReport") {
    group = "verification"
    description = "Generates aggregate JaCoCo coverage report across all subprojects."

    dependsOn(subprojects.map { it.tasks.withType<Test>() })
    dependsOn(subprojects.map { it.tasks.withType<JacocoReport>() })

    val allSourceDirs = subprojects.flatMap { subproject ->
        val sourceSets = subproject.extensions.findByType<SourceSetContainer>()
        sourceSets?.named("main")?.get()?.allSource?.srcDirs ?: emptySet()
    }
    sourceDirectories.setFrom(files(allSourceDirs))

    val allClassesDirs = subprojects.flatMap { subproject ->
        val sourceSets = subproject.extensions.findByType<SourceSetContainer>()
        sourceSets?.named("main")?.get()?.output?.classesDirs?.files ?: emptySet()
    }
    classDirectories.setFrom(
        allClassesDirs.map { dir ->
            fileTree(dir) {
                exclude(aggregateJacocoExclusions)
            }
        }
    )

    executionData.setFrom(
        files(
            provider {
                subprojects.mapNotNull { subproject ->
                    val exec = subproject.layout.buildDirectory.file("jacoco/test.exec").get().asFile
                    if (exec.exists()) exec else null
                }
            }
        )
    )

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/aggregate/jacoco.xml"))
        csv.outputLocation.set(layout.buildDirectory.file("reports/jacoco/aggregate/jacoco.csv"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregate/html"))
    }
}

