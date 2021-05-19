plugins {
    id("org.springframework.boot").version("2.4.3")
}

repositories {
    mavenLocal()
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":p6spy-spring-boot-starter"))

    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-tomcat")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework.cloud:spring-cloud-starter-sleuth:3.0.1")
    implementation("org.springframework.cloud:spring-cloud-sleuth-zipkin:3.0.1")

    implementation("com.h2database:h2")
    implementation("org.apache.commons:commons-io:1.3.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
