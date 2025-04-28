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
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
	testImplementation("org.springframework.security:spring-security-test")
	"developmentOnly"("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:postgresql")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
	testImplementation("org.mockito:mockito-core")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("com.h2database:h2")

    implementation('me.paulschwarz:spring-dotenv:2.2.0')

}

tasks.withType<Test> {
	useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.10"
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


