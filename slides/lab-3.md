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

![bg left:33%](assets/generated/lab-3.jpg)

# Lab 3

## Integration Testing

---

## Discuss Exercises from Lab 2

---
<!-- _class: section -->

# SECTION 3
# Integration Testing

---

# Integration Testing with Spring Boot

- Test multiple application layers together
- Verify component interactions
- Test with real external dependencies
- Ensure end-to-end functionality

- Intro to WireMock
- Intro to Testcontainers
- Enrich the API with calls to OpenLibrary to have stubs
- Stub on application start and during runtime

---

<!-- _class: code -->

# @SpringBootTest

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookApplicationIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private BookRepository bookRepository;
}
```

- Loads full application context
- Starts embedded server with random port
- Configures TestRestTemplate
- Much heavier than sliced tests

---

# Testing from the Outside

```java
@Test
void createBookShouldStoreInDatabase() {
    // Prepare test data
    Book newBook = new Book("123", "Test Book", "Test Author");
    
    // Make HTTP request
    ResponseEntity<Book> response = restTemplate.postForEntity(
        "/api/books", newBook, Book.class
    );
    
    // Verify HTTP response
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    
    // Verify database state
    Optional<Book> stored = bookRepository.findById("123");
    assertTrue(stored.isPresent());
    assertEquals("Test Book", stored.get().getTitle());
}
```

---

# External Dependencies in Tests

- Need real databases, message brokers, services
- Cannot use in-memory alternatives for everything
- Must be repeatable and isolated
- Should be fast and reliable

Enter: **Testcontainers**

---

# Testcontainers

- Docker containers for tests
- Real database instances
- Consistent test environment
- Automatic cleanup
- Wide range of supported systems

---

<!-- _class: code -->

# PostgreSQL with Testcontainers

```java
@SpringBootTest
@Testcontainers
class DatabaseIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

---

# Test Data Management

- Each test should start with a known state
- Tests should not interfere with each other
- Options:
  - Truncate tables between tests
  - Transaction rollback
  - Separate schemas per test
  - Database resets

---

# Test Data Cleanup Example

```java
@BeforeEach
void setUp() {
    // Clean database before test
    jdbcTemplate.execute("DELETE FROM book_loans");
    jdbcTemplate.execute("DELETE FROM book_reviews");
    jdbcTemplate.execute("DELETE FROM books");
    
    // Insert test data
    jdbcTemplate.update(
        "INSERT INTO books VALUES (?, ?, ?, ?, ?)",
        "123", "Test Book", "Test Author", LocalDate.now(), "AVAILABLE"
    );
}
```

---

# Spring Test Context Caching

- Creating application contexts is expensive
- Spring caches contexts between tests
- Same configuration = reused context
- Different configuration = new context

---

# Context Caching Issues

Common problems that break caching:

1. Different context configurations
2. @DirtiesContext usage
3. Modifying beans in tests
4. Different property settings
5. Different active profiles

---

# Optimizing Context Caching

```java
// Bad - different properties = different contexts
@SpringBootTest(properties = "spring.profiles.active=test1")
class Test1 { }

@SpringBootTest(properties = "spring.profiles.active=test2")
class Test2 { }

// Good - shared configuration = single context
@SpringBootTest
@ActiveProfiles("test")
class Test1 { }

@SpringBootTest
@ActiveProfiles("test")
class Test2 { }
```

---

# Avoiding @DirtiesContext

```java
// Bad - marks context as dirty after each test
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
void testThatModifiesState() {
    // ...
}

// Good - manually cleans up state
@BeforeEach
void setUp() {
    // Reset database or other state
    jdbcTemplate.execute("DELETE FROM books");
}
```

---

# Exercise: Fix Context Caching Issues

Identify and fix three integration tests that break context caching:
1. Tests with different property configurations
2. Tests using @DirtiesContext unnecessarily
3. Tests that modify shared beans

---

# Testing HTTP APIs

- TestRestTemplate - Synchronous REST client
- WebTestClient - Reactive REST client
- RestAssured - BDD-style REST testing

---

# TestRestTemplate Example

```java
@Test
void getBookByIsbnReturnsCorrectData() {
    // Prepare database with test data
    insertTestBook("123", "Test Title", "Test Author");
    
    // Make HTTP request
    ResponseEntity<Book> response = restTemplate.getForEntity(
        "/api/books/123", Book.class
    );
    
    // Verify response
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Test Title", response.getBody().getTitle());
}
```

---

# Working with Collections

```java
@Test
void getAllBooksReturnsAllBooks() {
    // Setup test data
    insertTestBooks();
    
    // Request collection
    ResponseEntity<List<Book>> response = restTemplate.exchange(
        "/api/books",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Book>>() {}
    );
    
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
}
```

---

# Exercise: Full HTTP API Test

Write a comprehensive test for the books API:
1. Set up test data with Testcontainers and PostgreSQL
2. Test CRUD operations via HTTP endpoints
3. Verify database state after operations
4. Handle collections and complex responses

---

# Lab 3: Summary

- Integration testing with full application context
- Using Testcontainers for real database testing
- Managing test data effectively
- Optimizing Spring Test Context caching
- Testing HTTP APIs from the outside

---

# Time For Some Exercises
## Lab 3

- Work with the same repository as in lab 1/lab 2
- Navigate to the `labs/lab-3` folder in the repository and complete the tasks as described in the `README` file of that folder
- Time boxed until the end of the coffee break (15:50 AM)
