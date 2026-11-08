plugins {
    id("org.springframework.boot")
}

springBoot {
    mainClass.set("io.github.edmaputra.ilmiya.IlmiyaApplicationKt")
}


dependencies {
    // All modules
    implementation(project(":ilmiya-common"))
    implementation(project(":ilmiya-core"))
    implementation(project(":ilmiya-academic"))
    implementation(project(":ilmiya-student"))
    implementation(project(":ilmiya-attendance"))
    implementation(project(":ilmiya-hr"))
    implementation(project(":ilmiya-timetable"))
    implementation(project(":ilmiya-exam"))
    implementation(project(":ilmiya-finance"))

    // Web & Actuator
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Database & Migrations
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")

    // OpenAPI / Swagger Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")

    // HashiCorp Vault & Consul Configuration & Discovery
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-config")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
}
