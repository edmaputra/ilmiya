plugins {
    id("org.springframework.boot")
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Database & Migrations
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // OpenAPI / Swagger Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}
