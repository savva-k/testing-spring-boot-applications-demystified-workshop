---
marp: true
theme: pragmatech
---

![bg](./assets/barcelona-spring-io.jpg)

---

<!-- _class: title -->
![bg h:500 left:33%](assets/generated/demystify.png)

# Testing Spring Boot Applications Demystified

## Full-Day Workshop

_Spring I/O Conference Workshop 21.05.2025_

Philip Riecks - [PragmaTech GmbH](https://pragmatech.digital/) - [@rieckpil](https://x.com/rieckpil)

--- 

![bg left:33%](assets/generated/lab-4.jpg)

# Lab 4

## Best Practices, Pitfalls, AI & Outlook

---

## Discuss Exercises from Lab 3

---

<!-- _class: section -->

# SECTION 4
# Advanced Testing Techniques & Best Practices

---

# WebClient and WebTestClient Testing

- WebClient is reactive, non-blocking HTTP client
- WebTestClient is testing alternative to TestRestTemplate
- Useful for testing both WebFlux and MVC controllers
- Provides fluent API for HTTP request construction
- Advanced assertions for responses
- Can be used with a running server or mock environment

---

<!-- _class: code -->

# Testing with WebTestClient

```java
@WebFluxTest(BookController.class)
class BookControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private BookService bookService;
    
    @Test
    void getBookByIsbnReturnsBook() {
        Book book = new Book("123", "Test Title", "Test Author");
        when(bookService.findByIsbn("123")).thenReturn(Mono.just(book));
        
        webTestClient.get()
                    .uri("/api/books/123")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Book.class)
                    .isEqualTo(book);
    }
}
```

---

# Mocking External Services with WireMock

- WireMock provides HTTP API mocking
- Perfect for testing external service dependencies
- Can be configured programmatically or with JSON files
- Simulates various response scenarios
- Verifies HTTP interactions

---

<!-- _class: code -->

# WireMock Example

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class ExternalServiceTest {
    
    @Autowired
    private BookInfoService bookInfoService;
    
    @Test
    void retrievesBookDetails() {
        stubFor(get(urlEqualTo("/api/books/123"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"isbn\":\"123\",\"title\":\"Spring Boot Testing\"}")));
                
        Book book = bookInfoService.getBookDetails("123").block();
        
        assertThat(book.getTitle()).isEqualTo("Spring Boot Testing");
        verify(getRequestedFor(urlEqualTo("/api/books/123")));
    }
}
```

---

# Testing Error Scenarios

- Important to test error handling
- WireMock can simulate connection failures
- Test timeouts, 4xx, and 5xx responses
- Verify error handling behavior works correctly

```java
@Test
void handleServerErrors() {
    stubFor(get(urlEqualTo("/api/books/error"))
        .willReturn(aResponse()
            .withStatus(500)
            .withFixedDelay(100)));
            
    assertThrows(WebClientResponseException.class, () -> 
        bookInfoService.getBookDetails("error").block());
}
```

---

# Common Spring Boot Testing Pitfalls

1. Using @SpringBootTest everywhere
2. Mixing JUnit 4 and JUnit 5
3. Not leveraging context caching
4. Missing test isolation
5. Brittle tests with implementation details

---

# Pitfall #1: @SpringBootTest Everywhere

❌ Problem:
- Using @SpringBootTest for every test
- Heavy test startup time
- Slow test execution

✅ Solution:
- Use the right test slice for the job
- Prefer unit tests for isolated components
- Reserve full context tests for integration needs

```java
// Bad Practice
@SpringBootTest  // Overkill for testing a single controller method
class BookControllerTest {
    @Autowired
    private BookController controller;
    
    @Test
    void getBookTitleById() {
        // Simple test that only needs the controller
    }
}

// Better Approach
@WebMvcTest(BookController.class)  // Focused on web layer only
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
}
```

---

# Choosing the Right Test Type

| Test Type | When to Use | Speed | Isolation |
|-----------|-------------|-------|-----------|
| Unit Test | Testing single components | Very Fast | High |
| Sliced Test | Testing specific layers | Fast | Medium |
| Integration Test | Testing multiple components together | Slow | Low |
| End-to-End Test | Testing complete functionality | Very Slow | Very Low |

---

# Pitfall #2: JUnit 4 vs 5 Issues

❌ Problem:
- Mixing JUnit 4 and JUnit 5 annotations
- Using wrong extension mechanisms
- Incompatible lifecycle methods

✅ Solution:
- Use JUnit 5 consistently
- Use appropriate extension mechanisms
- Understand lifecycle differences

```java
// Problematic: Mixing JUnit 4 and 5
@SpringBootTest
@RunWith(SpringRunner.class)  // JUnit 4 style
class MixedTest {
    @Rule  // JUnit 4 concept
    public ExpectedException thrown = ExpectedException.none();
    
    @Test  // JUnit 5 style test
    void testWithMixedApproach() {
        thrown.expect(IllegalArgumentException.class);
        // code that throws exception
    }
}

// Better: Consistent JUnit 5 approach
@SpringBootTest
class JUnit5Test {
    @Test
    void testWithConsistentApproach() {
        assertThrows(IllegalArgumentException.class, () -> {
            // code that throws exception
        });
    }
}
```

---

# JUnit 4 vs 5 Comparison

| JUnit 4 | JUnit 5 |
|---------|---------|
| @Before, @After | @BeforeEach, @AfterEach |
| @BeforeClass, @AfterClass | @BeforeAll, @AfterAll |
| @RunWith | @ExtendWith |
| @Rule, @ClassRule | @ExtendWith + custom Extension |
| @Ignore | @Disabled |
| @Category | @Tag |

---

# Pitfall #3: Not Using Context Caching

❌ Problem:
- Creating new contexts for every test class
- Slow test execution
- High resource usage

✅ Solution:
- Design tests to share contexts
- Consistent configuration across test classes
- Avoid unnecessary @DirtiesContext

```java
// Problematic: Breaking context caching unnecessarily
@SpringBootTest(properties = {
    "custom.property=test1",  // Unique property creates new context
    "spring.profiles.active=test"
})
class Test1 {}

@SpringBootTest(properties = {
    "custom.property=test2",  // Different property value = new context
    "spring.profiles.active=test"
})
class Test2 {}

// Better: Using consistent properties and active profiles
@SpringBootTest
@ActiveProfiles("test")  // Use annotations consistently
class Test1WithSharedContext {}

@SpringBootTest
@ActiveProfiles("test")  // Same configuration = shared context
class Test2WithSharedContext {}
```

---

# Visualizing Context Caching

- Same configuration = Same context
- Different configuration = Different context
- @DirtiesContext = New context

---

# Pitfall #4: Missing Test Isolation

❌ Problem:
- Tests affecting each other
- Order-dependent tests
- Shared mutable state

✅ Solution:
- Reset state between tests
- Use test-specific data
- Avoid static mutable state
- Clean up resources properly

```java
// Problematic: Tests depend on each other and shared state
@SpringBootTest
class NonIsolatedTests {
    @Autowired
    private BookRepository bookRepository;
    
    @Test
    void test1_createBooks() {
        bookRepository.save(new Book("123", "Test Book"));
        // No cleanup - affects other tests
    }
    
    @Test
    void test2_countBooks() {
        // Assumes test1 ran first and created data
        assertThat(bookRepository.count()).isGreaterThan(0);
    }
}

// Better: Each test properly isolated
@SpringBootTest
class IsolatedTests {
    @Autowired
    private BookRepository bookRepository;
    
    @BeforeEach
    void setUp() {
        // Clear data before each test
        bookRepository.deleteAll();
    }
    
    @Test
    void createAndVerifyBook() {
        bookRepository.save(new Book("123", "Test Book"));
        assertThat(bookRepository.count()).isEqualTo(1);
    }
}
```

---

# Test Isolation Techniques

- Database: Truncate tables or use transactions
- Files: Use temporary directories
- In-memory state: Reset before each test
- Static state: Avoid or reset explicitly
- Mocks: Create fresh instances for each test

---

# Pitfall #5: Brittle Tests

❌ Problem:
- Tests coupled to implementation details
- Breaking with minor refactoring
- Overuse of mocks and verification

✅ Solution:
- Test behavior, not implementation
- Use meaningful assertions
- Mock at boundaries, not internals
- Focus on outputs for given inputs

---

<!-- _class: code -->

# Behavior vs Implementation Testing

Implementation Testing:
```java
void testImplementationDetails() {
    service.processBook(book);
    
    verify(repository).findById(book.getId());
    verify(validator).validateBookData(book);
    verify(repository).save(book);
}
```

Behavior Testing:
```java
void testProcessBookBehavior() {
    when(repository.findById(book.getId())).thenReturn(Optional.empty());
    
    ProcessingResult result = service.processBook(book);
    
    assertEquals(ProcessingStatus.CREATED, result.getStatus());
    assertTrue(repository.existsById(book.getId()));
}
```

---

# Best Practices: Test Organization

1. Use descriptive test names
2. Follow a consistent pattern (Given-When-Then, Arrange-Act-Assert)
3. Group related tests in nested classes
4. Separate test utilities and fixtures
5. Keep test code clean and maintained
6. Apply testing pyramid principles

```java
// Using JUnit 5 nested classes for organization
@SpringBootTest
class BookServiceTest {
    
    @Nested
    class FindByIsbn {
        @Test
        void returnsBookWhenFound() { /* ... */ }
        
        @Test
        void returnsEmptyWhenNotFound() { /* ... */ }
        
        @Test
        void throwsExceptionWhenIsbnIsNull() { /* ... */ }
    }
    
    @Nested
    class CreateNewBook {
        @Test
        void savesValidBook() { /* ... */ }
        
        @Test
        void rejectsDuplicateIsbn() { /* ... */ }
        
        @Test
        void validatesRequiredFields() { /* ... */ }
    }
}
```

---

# Best Practice: Descriptive Test Names

```java
// Not descriptive
@Test
void testGetBook() { /* ... */ }

// Descriptive - explains behavior and expectations
@Test
void getBookByIsbn_whenBookExists_returnsBookDetails() { /* ... */ }

// Alternative with @DisplayName
@Test
@DisplayName("Get book by ISBN returns book details when book exists")
void getBookByIsbn() { /* ... */ }
```

---

# Best Practice: Test Structure

```java
@Test
void createBook_withValidData_savesBookAndReturnsCreated() {
    // Given
    Book newBook = new Book("123", "Title", "Author");
    when(repository.existsById("123")).thenReturn(false);
    
    // When
    ResponseEntity<Book> response = controller.createBook(newBook);
    
    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    verify(repository).save(newBook);
}
```

---

# Best Practice: Nested Tests

```java
@Nested
class GetBookByIsbnTests {
    @Test
    void returnsBookWhenFound() { /* ... */ }
    
    @Test
    void returns404WhenNotFound() { /* ... */ }
}

@Nested
class CreateBookTests {
    @Test
    void savesValidBook() { /* ... */ }
    
    @Test
    void rejects400ForDuplicateIsbn() { /* ... */ }
}
```

---

- Avoid any config in the main class, show issue and how to fix
- Talk about the order and possibilities for the application.yml for testing
- Best ways to start Testcontainers

---

# AI-Assisted Testing

- Code generation for tests
- Test case identification
- Edge case suggestion
- Refactoring existing tests
- Documentation generation

## Example: AI-Generated Test Cases

```java
// AI can suggest tests for different edge cases
@Test
void getBookByIsbn_withNullIsbn_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> 
        bookService.getBookByIsbn(null));
}

@Test
void getBookByIsbn_withEmptyIsbn_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> 
        bookService.getBookByIsbn(""));
}

@Test
void getBookByIsbn_withInvalidFormatIsbn_throwsInvalidIsbnException() {
    assertThrows(InvalidIsbnException.class, () -> 
        bookService.getBookByIsbn("invalid-format"));
}
```

---

# Testing Asynchronous Operations

- Spring applications often use async operations
- CompletableFuture, WebFlux, @Async methods
- Special considerations for testing async code
- Avoid flaky tests with proper synchronization

```java
@Test
void testAsyncOperation() {
    // Given
    CompletableFuture<String> future = bookService.processAsyncRequest("123");
    
    // When & Then
    String result = future.join(); // Wait for completion
    assertThat(result).isEqualTo("processed:123");
}

@Test
void testReactiveStream() {
    // Given
    Flux<Book> bookFlux = bookService.findAllReactive();
    
    // When & Then
    StepVerifier.create(bookFlux)
                .expectNextCount(3)
                .verifyComplete();
}
```

---

# Continuous Integration Testing

- Configure CI pipelines for test execution
- Run tests on each pull request 
- Use JaCoCo for test coverage metrics
- Consider test execution order and dependencies
- Faster feedback with test splitting strategies

```xml
<!-- pom.xml JaCoCo configuration -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

# Workshop Recap

- Unit testing with JUnit 5 and Mockito
- Sliced testing with @WebMvcTest and @DataJpaTest
- Integration testing with @SpringBootTest and Testcontainers
- WebClient and reactive testing with WebTestClient
- Testing asynchronous operations
- Test context caching optimization
- Best practices and common pitfalls

---

# Next Steps

1. Review workshop materials and lab exercises
2. Apply techniques to your own projects
3. Explore advanced topics:
   - Contract testing
   - Performance testing
   - Chaos engineering
   - Test-driven development

---

# Resources

- [Official Spring Testing Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html)
- [Testing Spring Boot Applications Masterclass](https://your-course-url.com)
- [Spring Boot Testing Starter Guide](https://spring.io/guides/gs/testing-web/)
- [Testcontainers Documentation](https://www.testcontainers.org/)

---

<!-- _class: title -->

# Thank You!

Questions?

[Your contact information]
