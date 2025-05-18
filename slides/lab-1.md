---
marp: true
theme: pragmatech
---

![bg](./assets/barcelona-spring-io.jpg)

---

<!--

Question: Who is writing tests manually and who is enjoying it?

--> 

![bg h:500 left:33%](assets/generated/demystify.png)

# Testing Spring Boot Applications Demystified

## Full-Day Workshop

_Spring I/O Conference Workshop 21.05.2025_

Philip Riecks - [PragmaTech GmbH](https://pragmatech.digital/) - [@rieckpil](https://x.com/rieckpil)


--- 

<!--

- Welcome, Introduction
  - 
- Overview of the workshop  
- When to eat, toilet, WLAN

-->

<!-- header: 'Testing Spring Boot Applications Demystified Workshop @ Spring I/O 21.05.2025' -->
<!-- footer: '![w:32 h:32](assets/generated/logo.webp)' -->

# Organization

- Hotel WLAN: TBD
- Slides & Code will be shared
- Workshop lab requirements
  - Java 21
  - Docker
  - GitHub Account (preferably a personal)

---

### (Planned) Timeline

- 9:00 - 10:45: **Lab 1 - Introduction and Spring Boot Testing Basics**  (105 minutes)
- 10:45 - 11:05: **Coffee Break** (20 minutes)
- 11:05 - 13:00: **Lab 2 - Sliced Testing** (115 minutes)
- 13:00 - 14:00 **Lunch** (60 minutes)
- 14:00 - 15:30: **Lab 3 - Integration Testing** (90 minutes)
- 15:30 - 15:50 **Coffee Break** (20 minutes)
- 15:50 - 17:00: **Lab 4 - Pitfalls, Best Practices, AI & Outlook** (70 minutes)

---
![bg right:33%](assets/generated/hza.jpg)

## Workshop Instructor: Philip

- Self-employed IT consultant from Herzogenaurach, Germany (Bavaria) 🍻
- Blogging & content creation for more than five years. Since three years with a focus on testing Java and specifically Spring Boot applications 🍃
- Founder of PragmaTech GmbH - Enabling Developers to Frequently Deliver Software with More Confidence 🚤
- Enjoys writing tests 🧪
- @rieckpil on various platforms


---

## Getting to Know Each Other

- What's your name?
- Where are you from?
- What's your role?
- How is automated testing handled in your team/company? 
- Do you have any specific testing challenges? 
- What's your expectation for this workshop?

---

![bg left:33%](assets/generated/lab-1.jpg)

# Lab 1

## Introduction and Spring Boot Testing Basics

---
<!-- paginate: true -->

<!-- header: '' -->
<!-- footer: '' -->
<!--


Notes:

- Raise hands if you enjoy writing tests -> I do and hope I can change that for some of you today
-->

![bg left](assets/generated/starting.jpg)

# Getting Started with Testing

## How It Started

---

<!-- header: 'Testing Spring Boot Applications Demystified Workshop @ Spring I/O 21.05.2025' -->
<!-- footer: '![w:32 h:32](assets/generated/logo.webp)' -->

<!--
- My story towards testing
- What I mean with testing: automated tests written by a developer
- Shifting left
- How much confidence do I have to deploy on a friday afternoon to prod on a dependabot update?
- Don't work towards 100% code coverage
- Fast feedback loops
- Quicker development, errors pop up more early, wouldn't say bugs, but sometimes we are overconfident only to realize after the deployment we missed a parameter or misspelled it. Avoid friction


-->

### Getting Used To Testing At Work

![](assets/generated/pr-reject.png)

---

# Goals of this Workshop

- Demystify the complexities of testing Spring Boot applications
- Provide clear explanations, practical insights, and actionable best practices
- Become more productive and confident in your development and testing efforts

---

<!-- _class: section -->

# Spring Boot Testing Basics
## Spring Boot Starter Test, Build Tools, Conventions, Unit Testing

---

## Why Do We Test Software

- Shift Left
- Catch Bugs Early
- Confidence in Code Changes
- Documentation
- Regression Prevention
- Become more Productive

---

<style>
img[alt~="center"] {
  display: block;
  margin: 0 auto;
}
</style>

### Naming Things Is Hard

![h:700 center](assets/cloud.svg)

---

### My Pragmatic Test Name Approach

1. **Unit Tests**: Tests that verify the functionality of a single, isolated component (like a method or class) by mocking or stubbing all external dependencies.
2. **Integration Tests**: Tests that verify interactions between two or more components work correctly together, with real implementations replacing some mocks.
3. **E2E**: Tests that validate the entire application workflow from start to finish, simulating real user scenarios across all components and external dependencies.

---

## Maven Build Lifecycle

![bg h:500 right:33%](assets/lifecycle.svg)

- **Maven Surfire Plugin** for unit tests: default postfix  `*Test` (e.g. `CustomerTest`)
- **Maven Failsafe Plugin** for integration tests: default postfix `*IT` (e.g. `CheckoutIT`)
- Reason for splitting: different **parallelization** options, better **organisation**

---

### Spring Boot Starter Test

<!--

Notes:

- Show the `spring-boot-starter-test` dependency and Maven dependency tree
- Show manual overriden


-->

![bg right:33%](assets/generated/swiss.jpg)

- aka. "Testing Swiss Army Knife"
- Batteries-included for testing
- Dependency management for:
  - JUnit Jupiter
  - Mockito
  - AssertJ
  - Awaitility
  - etc.
- We can manually override the dependency versions

---

---

<!--
Notes:
- Go to IDE to show the start
- Navigate to the parent pom to see the management
- Show the sample test to have seen the libraries at least once

Tips:
- Favor JUnit 5 over JUnit 4
- Pick one assertion library or at least not mix it within the same test class
-->

```shell
[INFO] +- org.springframework.boot:spring-boot-starter-test:jar:3.4.5:test
[INFO] |  +- org.springframework.boot:spring-boot-test:jar:3.4.5:test
[INFO] |  +- org.springframework.boot:spring-boot-test-autoconfigure:jar:3.4.5:test
[INFO] |  +- com.jayway.jsonpath:json-path:jar:2.9.0:test
[INFO] |  +- jakarta.xml.bind:jakarta.xml.bind-api:jar:4.0.2:test
[INFO] |  |  \- jakarta.activation:jakarta.activation-api:jar:2.1.3:test
[INFO] |  +- net.minidev:json-smart:jar:2.5.2:test
[INFO] |  |  \- net.minidev:accessors-smart:jar:2.5.2:test
[INFO] |  |     \- org.ow2.asm:asm:jar:9.7.1:test
[INFO] |  +- org.assertj:assertj-core:jar:3.26.3:test
[INFO] |  |  \- net.bytebuddy:byte-buddy:jar:1.15.11:test
[INFO] |  +- org.awaitility:awaitility:jar:4.3.0:test
[INFO] |  +- org.hamcrest:hamcrest:jar:2.2:test
[INFO] |  +- org.junit.jupiter:junit-jupiter:jar:5.11.4:test
[INFO] |  |  +- org.junit.jupiter:junit-jupiter-api:jar:5.11.4:test
[INFO] |  |  |  +- org.junit.platform:junit-platform-commons:jar:1.11.4:test
[INFO] |  |  |  \- org.apiguardian:apiguardian-api:jar:1.1.2:test
[INFO] |  |  +- org.junit.jupiter:junit-jupiter-params:jar:5.11.4:test
[INFO] |  |  \- org.junit.jupiter:junit-jupiter-engine:jar:5.11.4:test
[INFO] |  |     \- org.junit.platform:junit-platform-engine:jar:1.11.4:test
[INFO] |  +- org.mockito:mockito-core:jar:5.17.0:test
[INFO] |  |  +- net.bytebuddy:byte-buddy-agent:jar:1.15.11:test
[INFO] |  |  \- org.objenesis:objenesis:jar:3.3:test
[INFO] |  +- org.mockito:mockito-junit-jupiter:jar:5.17.0:test
[INFO] |  +- org.skyscreamer:jsonassert:jar:1.5.3:test
[INFO] |  |  \- com.vaadin.external.google:android-json:jar:0.0.20131108.vaadin1:test
[INFO] |  +- org.springframework:spring-core:jar:6.2.6:compile
[INFO] |  |  \- org.springframework:spring-jcl:jar:6.2.6:compile
[INFO] |  +- org.springframework:spring-test:jar:6.2.6:test
[INFO] |  \- org.xmlunit:xmlunit-core:jar:2.10.0:test
```

---

## Transitive Dependency #1: JUnit 5

- Modern testing framework for Java applications
- Key features: parameterized tests, nested tests, custom extensions
- Core component of Spring Boot test infrastructure

```java
@Test
void shouldCreateNewBook() {
  Book book = new Book("1234", "Spring Boot Testing", "Test Author");

  assertEquals("1234", book.getIsbn());
}

@ParameterizedTest
@CsvSource({
  "1234, Spring Boot Testing, Test Author",
  "5678, Advanced Spring, Another Author"
})
void shouldCreateBooksFromParameters(String isbn, String title, String author) {
  Book book = new Book(isbn, title, author);
  assertNotNull(book);
}
```

---

## Transitive Dependency #2: Mockito

- Mocking framework for unit tests
- Used to isolate the class under test from its dependencies
- Allows verification of interactions between objects

```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
  
  @Mock
  private BookRepository bookRepository;
  
  @InjectMocks
  private BookService bookService;
  
  @Test
  void shouldReturnBookWhenFound() {
    when(bookRepository.findByIsbn("1234")).thenReturn(Optional.of(expectedBook));
    
    Optional<Book> result = bookService.getBookByIsbn("1234");
    
    verify(bookRepository).findByIsbn("1234");
  }
}
```

---

## Transitive Dependency #3: AssertJ

- Fluent assertion library for Java tests
- Provides more readable, chain-based assertions
- Rich set of assertions for collections, exceptions, and more

```java
@Test
void shouldProvideFluentAssertions() {
  List<Book> books = List.of(
    new Book("1234", "Spring Boot Testing", "Test Author"),
    new Book("5678", "Advanced Spring", "Another Author")
  );
  
  assertThat(books)
    .hasSize(2)
    .extracting(Book::getTitle)
    .containsExactly("Spring Boot Testing", "Advanced Spring");
}
```

---

## Transitive Dependency #4: Hamcrest

- Framework for writing matcher objects
- Used for creating more readable assertions
- Often used alongside JUnit for expressive tests

```java
@Test
void shouldMatchWithHamcrest() {
  Book book = new Book("1234", "Spring Boot Testing", "Test Author");
  
  assertThat(book.getIsbn(), is("1234"));
  assertThat(book.getTitle(), allOf(
    startsWith("Spring"),
    containsString("Testing"),
    not(emptyString())
  ));
}
```
---

## Transitive Dependency #5: Awaitility

- Library for testing asynchronous systems
- Provides a DSL for expressing expectations on async operations
- Great for testing concurrent code and background tasks

```java
@Test
void shouldEventuallyCompleteAsyncOperation() {
  CompletableFuture<Book> futureBook = CompletableFuture.supplyAsync(() -> {
    try {
      Thread.sleep(300);
      return new Book("1234", "Async Testing", "Author");
    } catch (InterruptedException e) {
      return null;
    }
  });
  
  await().atMost(1, TimeUnit.SECONDS)
    .until(futureBook::isDone);
}
```

---

## Transitive Dependency #6: JsonPath

- Library for parsing and evaluating JSON documents
- Used for extracting and asserting on JSON structures
- Especially useful in REST API testing

```java
@Test
void shouldParseAndEvaluateJson() throws Exception {
  // Given
  String json = "{\"book\":{\"isbn\":\"1234\",\"title\":\"JSON Testing\",\"author\":\"Test Author\"}}";
  
  DocumentContext context = JsonPath.parse(json);
  
  assertThat(context.read("$.book.isbn", String.class)).isEqualTo("1234");
  assertThat(context.read("$.book.title", String.class)).isEqualTo("JSON Testing");
  
  // Assert on nested structure
  Map<String, Object> book = context.read("$.book");
  assertThat(book)
    .containsEntry("isbn", "1234")
    .containsEntry("title", "JSON Testing");
}
```

---

## Transitive Dependency #7: JSONAssert

- Library specifically for JSON assertion in tests
- Provides powerful comparison of JSON structures
- Supports strict and lenient comparison modes

```java
@Test
void shouldAssertJsonEquality() throws Exception {
  String actual = "{\"isbn\":\"1234\",\"title\":\"JSON Testing\",\"author\":\"Test Author\"}";
  String expected = "{\"isbn\":\"1234\",\"title\":\"JSON Testing\"}";
  
  // Strict mode would fail as expected is missing the author field
  JSONAssert.assertEquals(expected, actual, false);
}
```

---

## Transitive Dependency #8: XMLUnit

- Library for testing XML documents
- Provides comparison and validation of XML
- Useful for testing SOAP services or XML outputs

```java
@Test
void shouldCompareXmlDocuments() {
  String control = "<book><isbn>1234</isbn><title>XML Testing</title></book>";
  String test = "<book><isbn>1234</isbn><title>XML Testing</title></book>";
  
  Diff diff = DiffBuilder.compare(Input.fromString(control))
    .withTest(Input.fromString(test))
    .build();
  
  assertFalse(diff.hasDifferences(), diff.toString());
}
```

---

## Unit Testing with Spring Boot

- Provide collaborators from outside (dependency injection) -> no `new` inside your code
- Develop small, single responsibility classes
- Test only the public API of your class
- Verify behavior not implementation details
- TDD can help design (better) classes

---
### Avoid Static Method Access

```java
@Service
public class BirthdayService {

  public boolean isTodayBirthday(LocalDate birthday) {
    LocalDate today = LocalDate.now();

    return today.getMonth() == birthday.getMonth()
      && today.getDayOfMonth() == birthday.getDayOfMonth();
  }
}
```

---

### Better Alternative

```java
@Service
public class BirthdayServiceWithClock {

  private final Clock clock;

  public BirthdayServiceWithClock(Clock clock) {
    this.clock = clock;
  }

  public boolean isTodayBirthday(LocalDate birthday) {
    LocalDate today = LocalDate.now(clock);

    return today.getMonth() == birthday.getMonth()
      && today.getDayOfMonth() == birthday.getDayOfMonth();
  }
}
```

---

```java
@Test
void shouldReturnTrueWhenTodayIsBirthday() {
  // Arrange
  LocalDate fixedDate = LocalDate.of(2025, 5, 15);
  Clock fixedClock = Clock.fixed(
    fixedDate.atStartOfDay(ZONE_ID).toInstant(),
    ZONE_ID
  );

  BirthdayServiceWithClock cut = new BirthdayServiceWithClock(fixedClock);
  LocalDate birthday = LocalDate.of(1990, 5, 15); // Same month and day

  // Act
  boolean result = cut.isTodayBirthday(birthday);

  // Assert
  assertThat(result).isTrue();
}
```

---

### Check Your Imports

- Nothing Spring-related here
- Rely only on JUnit, Mockito and an assertion library

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
```

---

## Unify Test Structure

- Use a consistent test method naming: givenWhenThen, shouldWhen, etc.
- Structure test for the Arrange/Act/Assert test setup

```java
@Test
void should_When_() {

  // Arrange
  // ... setting up objects, data, collaborators, etc.

  // Act
  // ... performing the action to be tested on the class under test

  // Assert
  // ... verifying the expected outcome
}
```

---

## Unit Testing Has Limits

- **Request Mapping**: Does `/api/users/{id}` actually resolve to your desired method?
- **Validation**: Will incomplete request bodys result in a 400 bad request or return an accidental 200?
- **Serialization**: Are your JSON objects serialized and deserialized correctly?
- **Headers**: Are you setting Content-Type or custom headers correctly?
- **Security**: Are your Spring Security configuration and other authorization checks enforced?
- **Database**: Can we effectively map our complex JPA entity to a database table?
- etc.
- 
---

<!-- _class: code -->

# JUnit 5 & Mockito - The Foundation

```java
@Test
void testBookService() {
    // Given
    Book book = new Book("123", "Test Book", "Test Author");
    when(bookRepository.findById("123")).thenReturn(Optional.of(book));
    
    // When
    Optional<Book> result = bookService.getBookById("123");
    
    // Then
    assertTrue(result.isPresent());
    assertEquals("Test Book", result.get().getTitle());
    verify(bookRepository).findById("123");
}
```

---

# Design for Testability

## Prefer Constructor Injection

```java
// Hard to test
public class BookService {
    private final BookRepository bookRepository = new BookRepositoryImpl();
}

// Testable
public class BookService {
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
```

---

# Avoiding Static and Direct Instantiation

```java
// Hard to test
public LocalDate getDueDate() {
    return LocalDate.now().plusDays(14);
}

// Testable
public LocalDate getDueDate(Clock clock) {
    return LocalDate.now(clock).plusDays(14);
}
```

---

# Don't Overuse Mockito

- Mocks can make tests brittle
- Consider using real objects when:
  - They have no external dependencies
  - They're simple value objects
  - They're collections or other standard library classes

---

<!-- _class: code -->

# JUnit 5 Extensions

```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private BookService bookService;
}
```

---

# Exercise: Write a JUnit Jupiter Extension

Create a custom extension for timing tests:

```java
public class TimingExtension implements BeforeTestExecutionCallback, 
                                      AfterTestExecutionCallback {
    private static final Logger logger = LoggerFactory.getLogger(TimingExtension.class);
    
    @Override
    public void beforeTestExecution(ExtensionContext context) {
        getStore(context).put("start", System.currentTimeMillis());
    }
    
    @Override
    public void afterTestExecution(ExtensionContext context) {
        long start = getStore(context).remove("start", Long.class);
        long duration = System.currentTimeMillis() - start;
        logger.info("Test {} took {} ms", context.getDisplayName(), duration);
    }
    
    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
```

---

# Refactoring Time-Based Code

Before:
```java
public boolean isOverdue(BookLoan loan) {
    return LocalDate.now().isAfter(loan.getDueDate());
}
```

After:
```java
public boolean isOverdue(BookLoan loan, Clock clock) {
    return LocalDate.now(clock).isAfter(loan.getDueDate());
}
```

---

# Spring's Testing Pyramid

![Testing Pyramid](https://martinfowler.com/articles/practical-test-pyramid/testPyramid.png)

- Unit Tests: Fast, focused, isolated
- Integration Tests: Verify components work together
- End-to-End Tests: Full application testing

---

## Project Setup

1. Write unit tests for the Book entity
2. Create a custom JUnit 5 extension
3. Refactor the `isOverdue` method to use a `Clock` parameter

---

## Lab 1: Exercises

1. Write unit tests for the Book entity
2. Create a custom JUnit 5 extension
3. Refactor the `isOverdue` method to use a `Clock` parameter
