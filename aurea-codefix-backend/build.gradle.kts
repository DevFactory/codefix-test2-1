import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    id("org.springframework.boot") version "2.1.4.RELEASE"
    id("com.gorylenko.gradle-git-properties") version "1.5.1"
    id("java")
    id("jacoco")
    id("io.franzbecker.gradle-lombok") version "2.1"
    id("checkstyle")
    id("findbugs")
    id("pmd")
}

group = "com.devfactory.codefix"
version = "0.0.1-SNAPSHOT"

apply(from = "$rootDir/gradle/jacoco.gradle.kts")
apply(from = "$rootDir/gradle/quality.gradle.kts")

tasks.named<DefaultTask>("check") {
    dependsOn(tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification"))
}

gitProperties {
    keys = mutableListOf("git.branch", "git.commit.id", "git.commit.time", "git.tags")
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "com.devfactory.codefix.BackendApplication"
    archiveBaseName.set("codefix")
    archiveVersion.set("0.0.1-SNAPSHOT")
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule {
            element = "CLASS"
            excludes = listOf("com.devfactory.codefix.BackendApplication")
            limit {
                counter = "INSTRUCTION"
                minimum = BigDecimal.ONE
            }
            limit {
                counter = "BRANCH"
                minimum = BigDecimal.ONE
            }
        }
    }
}

repositories {
    maven(url = "https://scm.devfactory.com/nexus/content/groups/public")
    mavenCentral()
    jcenter()
}

ext["mockito.version"] = "2.27.0"

dependencies {
    implementation(Dependencies.springBootActuator)
    compileOnly(Dependencies.springBootConfiguration)

    implementation(Dependencies.springBootDataJpa)
    implementation(Dependencies.springBootWeb)
    implementation(Dependencies.springJms)
    implementation(Dependencies.springActiveMq)
    implementation(Dependencies.amazonSqs)
    implementation(Dependencies.amazonSes)

    implementation(Dependencies.guava)
    implementation(Dependencies.springBootStarterSecurity)
    implementation(Dependencies.springAuth0)
    implementation(Dependencies.auth0)
    implementation(Dependencies.auth0Jwt)
    implementation(Dependencies.mysql)
    implementation(Dependencies.flyway)
    implementation(Dependencies.guava)
    implementation(Dependencies.jiraCore)
    implementation(Dependencies.jiraApi)
    implementation(Dependencies.fugue)
    implementation(Dependencies.codeserverClient)
    implementation(Dependencies.jacksonJava8)
    implementation(Dependencies.httpClient)
    implementation(Dependencies.freemarker)
    implementation(Dependencies.springBootMail)

    implementation(Dependencies.lombok)


    testImplementation(TestDependencies.springBootTest) {
        exclude(group = "junit", module = "junit")
    }

    testRuntime(Dependencies.h2)
    testImplementation(TestDependencies.springSecurityTest)
    testImplementation(TestDependencies.junit5)
    testImplementation(TestDependencies.wiremock)
    testImplementation(TestDependencies.junitMockito)
    testImplementation(TestDependencies.javaxJson)
}
