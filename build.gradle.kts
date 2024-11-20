plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("kapt") version "2.0.10"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.elroykanye"
version = "0.0.1-SNAPSHOT"

springBoot {
	this.mainClass = "com.elroykanye.Html2pdfApplicationKt"
}
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	mavenLocal()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.microsoft.playwright:playwright:1.41.0")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
