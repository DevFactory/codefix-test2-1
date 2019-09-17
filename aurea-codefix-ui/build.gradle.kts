import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmTask

plugins {
  id("com.moowork.node") version "1.3.1"
}

configure<NodeExtension> {
  version = "9.11.2"
  npmVersion = "6.0.1"
  download = true
  nodeModulesDir = file("${project.projectDir}")
}

task<NpmTask>("build") {
  dependsOn("installDependencies", "check")
  description = "Compile client side folder"
  setArgs(listOf("run", "build"))
}

task<NpmTask>("buildProd") {
  dependsOn("installDependencies", "check", "test")
  description = "Compile client side folder for production"
  setArgs(listOf("run", "buildProd"))
}

task<NpmTask>("check") {
  dependsOn("installDependencies")
  description = "Source code analysis with Lint"

  doFirst {
    mkdir("build/reports/pmd/")
  }
  setArgs(listOf("run", "lint"))
}

task<NpmTask>("test") {
  dependsOn("installDependencies")
  description = "Unit Testing"
  setArgs(listOf("run", "test"))
}

task("clean") {
  delete("${project.projectDir}/dist")
  delete("${project.projectDir}/node_modules")
  delete("${project.projectDir}/build")
}

task<NpmTask>("installDependencies") {
  setArgs(listOf("install", "--@devfactory:registry=http://nexus-rapid-proto.devfactory.com/repository/npm-proto/"))
}
