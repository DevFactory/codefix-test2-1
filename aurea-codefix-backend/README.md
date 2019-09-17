# Code Fix Backend

Spring boot java application, provides end points to repositories issues administration.

## Technology stack

- Spring boot 2.x
- Junit 5
- Gradle 5.x
- Java 8

## Specific project practices 

1. Use `Package By Feature` packaging convention.

## Specific project practices (Unit test)

1. Name class under test `testInstance` in unit test suites.
1. Consider junit5 `@Nested` when testing multiple branches or complex setup of a method. 

## Running application locally

Application as any spring boot application can be run by using `gradle bootRun` command or executing main entry point (BackendApplication class). 
To run an specific environment configure required properties directly in application.yml or using environment properties.

## Quality checks

Findbugs, Checkstyle, Pmd, unit test result and coverage are executed as part of `gradle build` command, reports are available in 
`aurea-codefix-backend/build/reports` directory.
