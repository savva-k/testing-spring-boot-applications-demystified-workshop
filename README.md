# Testing Spring Boot Applications Demystified Workshop

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/PragmaTech-GmbH/testing-spring-boot-applications-demystified-workshop)

A one-day workshop to help developers become more confident and productive when implementing automated tests for Spring Boot applications.

## Workshop Overview

This workshop is designed to demystify testing in Spring Boot applications through hands-on exercises, covering everything from basic unit testing to advanced integration testing techniques. The workshop is divided into four lab sessions, each focusing on different aspects of testing Spring Boot applications.

## Workshop Format

- Full-day workshop
- Four main labs, ranging from 70-110 minutes each
- Hands-on exercises with provided solutions
- Building on a consistent domain model (a library management system)

## GitHub Codespaces

This repository is configured for use with GitHub Codespaces, which provides a complete, ready-to-use development environment in the cloud. To use GitHub Codespaces:

1. Click on the "Code" button on the GitHub repository
2. Select the "Codespaces" tab
3. Click "Create codespace on main"
4. Wait for the codespace to start and setup to complete

The codespace includes:
- Java 21
- Maven
- Docker (for TestContainers)
- VS Code with Spring Boot extensions
- All diagrams rendered as images

For more information, see the [Codespaces documentation](.devcontainer/README.md).

## Todos

- [ ] ZIP Version to share for non-git Users
- [ ] Workshop infos, links for attendees on my company website
- [ ] Prepare template for the certificate of completion
- [ ] Show IDEA shortcut for fast test run
- [ ] Optimize code and remove imports
- [ ] Secure Workshop

## Workshop Slots

- 9 AM - 10:45: Lab 1 (105 minutes)
- 10:45 - 11:05: **Coffee Break**
- 11:05 - 13:00: Lab 2 (115 minutes)
- 13:00 - 14:00 **Lunch**
- 14:00 - 15:30: Lab 3 (90 minutes)
- 15:30 - 15:50 **Coffee Break**
- 15:50 - 17:00: Lab 4 (70 minutes)

### Lab 1 (105 Minutes): Introduction, Welcome, Code Setup, Spring Boot Testing Fundamentals

- Introduction to the workshop, show goals for the day
- Timeline, breaks, questions, code, and exercises
- Formal start with introduction of myself, then all the participants -> write down names like teacher

- Why do we Test Code?
- Why is testing an afterthought?
- How to make testing more joyful?

- My Journey to testing

- Workshop code examples: GitHub repository, Java 21 and Docker

- Maven Testing Basics
- Gradle Testing Basics
- Java Testing Basics: AAA Pattern, naming methods, other best practices

- Spring Boot Testing basics
  - Exploring the Testing Toolbox
  - JUnit: 4 vs. 5.: most important annotations, features, understanding when JUnit will instantiate a test class
  - More details on AssertJ
  - Mockito: default stubbing, how to create a mock?, DO's and DON'Ts (golden mockito rules)

- Unit testing with Spring
  - External collaborators and dependency injection for testability
  - Sample test to showcase
  - How to handle static? -> Date.now() example

- Checking out the project for the workshop

Exercises: 
- JUnit: Creating custom JUnit 5 extensions, explore parameterized tests
- Mockito: Explore stubbing, see default mock behavior
- AssertJ: Explore the fluent API, chaining assertions, expecting exceptions

### Lab 2 (115 Minutes): Testing Spring MVC and Data JPA with Test Slices

- Discuss Exercise from Slot 1

- Introduction to sliced testing
- When unit testing is not sufficient
- Using @WebMvcTest for controller testing
- Comparing Sliced Testing to plain Unit Testing -> pros and cons

- Testing filters and security configurations
- Using @DataJpaTest for repository testing
  - In-memory vs. real database
  - How to prepare data
  - Transaction management
  - Enable logs to see when the persistence context flushes to the database
- Understanding JPA's transaction behavior and EntityManager
- Testing custom database queries, including native SQL queries

Exercise:
- @WebMvcTest: Testing a controller with MockMvc
- @DataJpaTest: Testing a repository with a custom SQL query, verify the JPA entity can be stored, retrieved -> pitfall with flushing/commit persistence context
- Explore sliced tests for your own tech-stack

### Lab 3 (90 Minutes): Integration Testing with Testcontainers and WireMock

- Discuss Exercise solutions for Slot 2
- Full application testing with @SpringBootTest
- Testing with WebClient and WebTestClient
- Mocking external services with WireMock
- Introduction to Testcontainers for external resources
- Data cleanup strategies for integration tests
- Understanding Spring test context caching
- Performance optimization in test suites
- Testing from outside with TestRestTemplate

### Lab 4 (70 Minutes): Advanced Testing Techniques and Best Practices

- Discuss Exercise from Slot 3

- Common Spring Boot testing pitfalls and how to avoid them
  - 1. @SpringBootTest Obsession
  - 2. @Mock, @MockBean, @MockitoBean
  - 3. JUnit 4 vs. 5 pitfall
- Best practices for testing Spring Boot applications
  - 1. Parallelizing tests
  - 2. Get help from AI -> show Diffblue, Claude Code etc.
  - 3. Mutation testing -> show in action

- AI and testing
  - GitHub Copilot
  - Diffblue
  - Claude COde

- Outlook
  - Still feature dev happening, see @ServiceConnection, MockMvcTester

- Pitch follow-up courses/workshops
  - Want to also onboard your colleges? Being the only one in the office to care about testing is hard
  - On-demand courses

- Summary

- Try to gather feedback, make the connect on LinkedIn 
- Certificates for the participants, reach out via LinkedIn or mail

- Q&A round

## Lab Structure

Each lab (`lab-1` through `lab-4`) includes:

- Exercise files with instructions and TODO comments
- Solution files that show the complete implementation
- Supporting code and configurations

## Prerequisites

- Java 21 (or later)
- Maven 3.8+ (wrapper included)
- Docker (for Testcontainers in Lab 3)
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## Getting Started

1. Clone this repository:

 ```bash
 git clone https://github.com/yourusername/testing-spring-boot-applications-demystified.git
 ```

2. Import the projects into your IDE of choice.

3. Run all builds with:

```bash
./mvnw verify
```

## Resources

- [Official Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [WireMock Documentation](http://wiremock.org/docs/)

## License

This project is licensed under the MIT License - see the LICENSE file for details.
