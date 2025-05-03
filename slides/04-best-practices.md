---
marp: true
theme: default
paginate: true
---

# Testing Spring Boot Applications Demystified

## Lab 4: Best Practices & Pitfalls

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

---

# Visualizing Context Caching

![Context Caching](https://spring.io/images/diagram-test-context-framework.svg)

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

# AI-Assisted Testing

- Code generation for tests
- Test case identification
- Edge case suggestion
- Refactoring existing tests
- Documentation generation

---

# AI for Test Generation

Prompt Example:
```
Write JUnit 5 tests for this Spring Boot controller method:

@GetMapping("/{isbn}")
public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
    return bookService.findByIsbn(isbn)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

---

# AI for Test Enhancement

Prompt Example:
```
Here's my current test for a book service. 
Can you suggest additional edge cases I should test?

@Test
void findByIsbn_returnsBook_whenFound() {
    when(repo.findById("123")).thenReturn(Optional.of(new Book("123", "Title", "Author")));
    Optional<Book> result = service.findByIsbn("123");
    assertTrue(result.isPresent());
}
```

---

# Workshop Recap

- Unit testing with JUnit 5 and Mockito
- Sliced testing with @WebMvcTest and @DataJpaTest
- Integration testing with @SpringBootTest and Testcontainers
- Test context caching optimization
- Best practices and pitfalls

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

# Testing Spring Boot Applications Masterclass

- Comprehensive online course
- 40+ video lessons
- Hands-on exercises
- Advanced testing techniques
- [Link to course]

---

# Consultancy Services

- Custom workshop delivery
- Code review and test strategy
- Team training and mentoring
- Test automation implementation

Contact: [Your contact information]

---

# Thank You!

Questions?

[Your contact information]