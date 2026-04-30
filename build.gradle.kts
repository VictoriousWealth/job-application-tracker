plugins {
    java
    id("org.springframework.boot") version "3.3.10" // or "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
}

group = "com.nick"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
		languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
	mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.hibernate.orm:hibernate-core:6.3.1.Final")
    


    // Runtime
    runtimeOnly("org.postgresql:postgresql")

    // Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.33")

    // Environment
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val ciTestIncludes = listOf(
    "**/JobApplicationTrackerApplicationTests.class",
    "**/JwtServiceTest.class",
    "**/CommunicationLogControllerTest.class",
    "**/CoverLetterControllerTest.class",
    "**/FollowUpReminderControllerTest.class",
    "**/JobApplicationControllerTest.class",
    "**/JobSourceControllerTest.class",
    "**/LocationControllerTest.class",
    "**/ResumeControllerTest.class",
    "**/ScheduledCommunicationControllerTest.class",
    "**/SkillTrackerControllerTest.class",
    "**/UserControllerTest.class",
    "**/InsightsControllerTest.class",
    "**/CalendarControllerTest.class",
    "**/ExportControllerTest.class",
    "**/MatchingControllerTest.class",
    "**/WorkspaceInsightsServiceTest.class",
    "**/ApplicationMatchingServiceTest.class",
    "**/CalendarIntegrationServiceTest.class",
    "**/WorkspaceExportServiceTest.class"
)

val ciTest by tasks.registering(Test::class) {
    description = "Runs the maintained behavior-focused CI suite."
    group = "verification"
    useJUnitPlatform()
    include(ciTestIncludes)
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.register<JacocoReport>("jacocoCiTestReport") {
    dependsOn(ciTest)

    executionData(ciTest)
    sourceSets(sourceSets.main.get())

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests must run before generating report

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true) // ENABLE HTML report
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

// ✅ Automatically generate coverage report on `./gradlew check`
tasks.named("check") {
    dependsOn(tasks.jacocoTestReport)
}

tasks.register("openJacocoReport") {
    dependsOn(tasks.jacocoTestReport)
    doLast {
        val reportPath = layout.buildDirectory.dir("jacocoHtml").get().asFile.resolve("index.html")
        println("📄 Opening report at: $reportPath")
        exec {
            commandLine("open", reportPath.absolutePath)
        }
    }
}


fun loadDotEnv(): Map<String, String> {
    val envFile = rootProject.file(".env")
    if (!envFile.exists()) return emptyMap()

    return envFile.readLines()
        .filter { it.isNotBlank() && !it.trim().startsWith("#") && it.contains("=") }
        .map { it.trim().split("=", limit = 2) }
        .associate { it[0] to it[1] }
}


tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    val dotenv = loadDotEnv()
    val activeProfile = dotenv["SPRING_PROFILES_ACTIVE"]
        ?: throw GradleException("❌ SPRING_PROFILES_ACTIVE not found in .env!")

    environment("SPRING_PROFILES_ACTIVE", activeProfile)

    // Set any other environment variables from .env
    dotenv.forEach { (key, value) ->
        environment(key, value)
    }
}

tasks.withType<Test> {
    val dotenv = loadDotEnv()
    dotenv.forEach { (k, v) -> environment(k, v) }
}
