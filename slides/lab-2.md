---
marp: true
theme: pragmatech
---

![bg](./assets/barcelona-spring-io.jpg)
<!-- header: "" -->
<!-- footer: ""-->



---

<!-- _class: title -->
![bg h:500 left:33%](assets/generated/demystify.png)

# Testing Spring Boot Applications Demystified

## Full-Day Workshop

_Spring I/O Conference Workshop 21.05.2025_

Philip Riecks - [PragmaTech GmbH](https://pragmatech.digital/) - [@rieckpil](https://x.com/rieckpil)


--- 

![bg left:33%](assets/generated/lab-2.jpg)

# Lab 2

## Sliced Testing

---

<!-- _class: section -->

# SECTION 2
# Sliced Testing

---

# Sliced Testing in Spring Boot

- Test a specific "slice" of the application
- Focus on one layer at a time
- Faster than full application tests
- Clearer test boundaries
- Simplified configuration

---

# Common Test Slices

- `@WebMvcTest` - Controller layer
- `@DataJpaTest` - Repository layer
- `@JsonTest` - JSON serialization/deserialization
- `@RestClientTest` - RestTemplate testing
- `@WebFluxTest` - WebFlux controller testing
- `@JdbcTest` - JDBC testing

---

# When Unit Tests Are Not Enough

- HTTP constraints and semantics
- Security rules and authentication
- Request/response processing
- Data persistence behavior
- Transaction boundaries

---

<!-- _class: code -->

# @WebMvcTest

```java
@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
}
```

- Tests only the web layer
- Auto-configures MockMvc
- Lightweight context with web components
- No full application context

---

<!-- _class: code -->

# HTTP Semantics with MockMvc

```java
@Test
void getBookByIsbnReturnsBookWhenFound() throws Exception {
    Book book = new Book("978-1-2345-6789-0", "Spring Boot Testing", "John Doe");
    when(bookService.findByIsbn("978-1-2345-6789-0")).thenReturn(Optional.of(book));
    
    mockMvc.perform(get("/api/books/978-1-2345-6789-0"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.isbn", is("978-1-2345-6789-0")))
           .andExpect(jsonPath("$.title", is("Spring Boot Testing")));
}
```

---

# Testing HTTP Status Codes

```java
@Test
void getBookByIsbnReturnsNotFoundWhenMissing() throws Exception {
    when(bookService.findByIsbn("unknown")).thenReturn(Optional.empty());
    
    mockMvc.perform(get("/api/books/unknown"))
           .andExpect(status().isNotFound());
}
```

---

# Testing Request Filters

```java
@WebMvcTest({BookController.class, RequestLoggingFilter.class})
class FilterTest {
    @Test
    void filterShouldProcessRequest() throws Exception {
        mockMvc.perform(get("/api/books")
               .header("User-Agent", "Test"))
               .andExpect(status().isOk());
               
        // Verify filter behavior
    }
}
```

---

# Testing Spring Security

```java
@Test
@WithMockUser(roles = "ADMIN")
void createBookRequiresAdminRole() throws Exception {
    mockMvc.perform(post("/api/books")
           .contentType(MediaType.APPLICATION_JSON)
           .content("{\"isbn\":\"123\",\"title\":\"Test\"}"))
           .andExpect(status().isCreated());
}

@Test
void createBookWithoutAuthReturns401() throws Exception {
    mockMvc.perform(post("/api/books")
           .contentType(MediaType.APPLICATION_JSON)
           .content("{\"isbn\":\"123\",\"title\":\"Test\"}"))
           .andExpect(status().isUnauthorized());
}
```

---

# Exercise: Testing a Filter and Secured Endpoints

1. Test a request logging filter
2. Test secured endpoints with different authentication scenarios
3. Verify correct HTTP status codes for various security conditions

---

<!-- _class: code -->

# @DataJpaTest

```java
@DataJpaTest
class BookRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private BookRepository bookRepository;
}
```

- Tests JPA repositories
- Auto-configures in-memory database
- Sets up appropriate transaction mgmt
- Provides TestEntityManager

---

# EntityManager vs. Repository

```java
@Test
void findByTitleShouldReturnBook() {
    // Using EntityManager to set up data
    Book savedBook = entityManager.persistAndFlush(
        new Book("123", "Spring Data JPA", "John Doe")
    );
    
    // Using Repository for the test
    List<Book> found = bookRepository.findByTitleContaining("Data");
    
    assertThat(found).hasSize(1);
    assertThat(found.get(0).getIsbn()).isEqualTo("123");
}
```

---

# Transaction Boundaries and Flushing

```java
@Test
void saveAndFlushImmediately() {
    Book book = new Book("123", "Test", "Author");
    bookRepository.save(book);  // No SQL executed yet
    
    entityManager.flush();  // Forces SQL execution
    entityManager.clear();  // Clears persistence context
    
    Book found = bookRepository.findById("123").orElse(null);
    assertThat(found).isNotNull();
}
```

---

# When SQL Gets Executed

- `save()` - No immediate SQL (only on flush)
- `saveAndFlush()` - Executes SQL immediately
- `findById()` - Executes SQL if entity not in context
- Transaction completion - Triggers flush
- `EntityManager.flush()` - Manual flush

---

# Testing Native Queries

```java
@Query(value = "SELECT * FROM books WHERE " +
               "LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
               "LOWER(author) LIKE LOWER(CONCAT('%', :keyword, '%'))",
       nativeQuery = true)
List<Book> search(String keyword);
```

How to test?
- Ensure correct data setup
- Verify query results with different parameters
- Test edge cases and special characters

---

<!-- _class: code -->

# Lazy Loading

```java
@Entity
public class Book {
    // ...
    
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<Review> reviews;
}

@Test
void demonstrateLazyLoading() {
    Book book = entityManager.find(Book.class, "123");
    
    // reviews not loaded yet
    assertFalse(Hibernate.isInitialized(book.getReviews()));
    
    // accessing the collection triggers loading
    int reviewCount = book.getReviews().size();
    
    // now it's loaded
    assertTrue(Hibernate.isInitialized(book.getReviews()));
}
```

---

# Entity Lifecycle Requirements

- Entities need no-arg constructor (can be protected)
- Prefer field access or property access consistently
- Understand cascade types
- Use proper fetch types (LAZY vs EAGER)

---

# Exercise: Working with JPA Tests

1. Write DataJpaTest for custom repository methods
2. Test lazy loading behavior
3. Test entity lifecycle and transaction boundaries
4. Write test for a native query

---

# Lab 2: Putting It All Together

- Test controllers with security constraints
- Test repository methods with proper transaction handling
- Understand when SQL statements are executed
- Verify proper HTTP semantics and JPA behavior
