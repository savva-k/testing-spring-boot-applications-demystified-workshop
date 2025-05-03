# Testing Spring Boot Applications Demystified

A one-day workshop to help developers become more confident and productive when implementing automated tests for Spring Boot applications.

## Workshop Overview

This workshop is divided into four lab sessions, each focusing on different aspects of testing Spring Boot applications:

### Lab 1: Fundamentals of Spring Boot Testing
- Introduction to Spring Boot's testing support
- Unit testing with JUnit 5
- Basic Spring context testing
- Test slices

### Lab 2: Testing Database Access
- Testing repositories with TestEntityManager
- Data JPA Test slices
- Integration testing with H2
- Testing SQL queries and database operations

### Lab 3: Testing Web Applications
- Testing REST controllers with MockMvc
- Testing JSON serialization/deserialization
- Integration testing with TestRestTemplate
- Using TestContainers for real database testing

### Lab 4: Advanced Testing Techniques
- Testing with WebClient and WebTestClient
- External service testing with WireMock
- Test doubles (mocks, stubs, spies)
- Testing asynchronous operations

## Project Structure

The workshop includes four Spring Boot projects:

- `lab-1`: Basic Spring Boot project to practice fundamental testing concepts
- `lab-2`: Spring Boot project with database integration for repository testing
- `lab-3`: Spring Boot web application with REST controllers
- `lab-4`: Advanced Spring Boot application with external service integration

## Prerequisites

- Java 21 (or later)
- Maven 3.8+ (wrapper included)
- Docker (for TestContainers)
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## Getting Started

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/testing-spring-boot-applications-demystified.git
   ```

2. Import the projects into your IDE of choice.

3. Navigate to each lab directory and run:
   ```bash
   ./mvnw clean verify
   ```

## Resources

- [Official Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [WireMock Documentation](http://wiremock.org/docs/)

## License

This project is licensed under the MIT License - see the LICENSE file for details.