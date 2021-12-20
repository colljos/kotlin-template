# Kotlin project template

This template can be used to rapidly bootstrap a new project.

The **basic template** defines the following:
- a project layout following Domain-Driven Design and Hexagonal Architecture principles.
- Gradle Kotlin DSL build and dependency management system.
- Kotlin JVM standard libraries
- JUnit 5 test framework
- JaCoCo gradle plugin for test code coverage

## Usage

To build
```
./gradlew clean build
```
To run tests
```
./gradlew test
./gradlew integrationTest
```
To generate code coverage report
```
./gradlew jacocoTestReport
```

## References

- **Domain-Driven Design** - Tackling complexity in the heart of software, Eric Evans.
  See also [Domain Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html) by Martin Fowler.
- **Designing Hexagonal Architecture with Java** [book](https://www.packtpub.com/product/designing-hexagonal-architecture-with-java-and-quarkus/9781801816489)
  and associated [code repository](https://github.com/PacktPublishing/Designing-Hexagonal-Architecture-with-Java).
- [DDD, Hexagonal, Onion, Clean, CQRS, ... How I put it all together](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/) by Herberto Graca.
- [Hexagonal architecture](https://alistair.cockburn.us/hexagonal-architecture/) by Alistair Cockburn.

## Versions

| Tech   | Version | Reference                                                                             |
|--------|---------|---------------------------------------------------------------------------------------|
| Gradle | 7.2     | [Gradle Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html) |
| Java   | 17      | [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/)                               |
| Kotlin | 1.6.0   | [Kotlin docs](https://kotlinlang.org/docs/home.html)                                  |
| Junit  | 5.8.2   | [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)               |
| JaCoCo | 0.8.7   | [JaCoCo Plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)         |
