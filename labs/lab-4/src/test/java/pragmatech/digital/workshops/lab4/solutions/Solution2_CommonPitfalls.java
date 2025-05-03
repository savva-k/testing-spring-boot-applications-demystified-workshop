package pragmatech.digital.workshops.lab4.solutions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Solution for Exercise 2: Common Spring Boot Testing Pitfalls
 */
public class Solution2_CommonPitfalls {

    /**
     * Pitfall 1: Overusing @SpringBootTest
     * 
     * Solution: Use more focused test slice annotations
     */
    
    /* Original code with pitfall:
    
    @SpringBootTest
    class SimpleControllerTest {
        @Autowired
        private BookController bookController;
        
        @Test
        void testGetBookById() {
            // Test a single controller method
        }
    }
    
    */
    
    // Solution:
    
    @WebMvcTest(controllers = BookControllerStub.class)
    static class ImprovedControllerTest {
        
        @Autowired
        private MockMvc mockMvc;
        
        @MockBean
        private BookServiceStub bookService;
        
        @Test
        void testGetBookById() throws Exception {
            when(bookService.findById("1")).thenReturn(new BookStub("1", "Test Book"));
            
            mockMvc.perform(get("/api/books/1"))
                    .andExpect(status().isOk());
        }
    }
    
    /**
     * Pitfall 2: Mixing JUnit 4 and JUnit 5
     * 
     * Solution: Consistently use JUnit 5 style
     */
    
    /* Original code with pitfall:
    
    @SpringBootTest
    @RunWith(SpringRunner.class)  // JUnit 4 style
    class MixedJUnitStylesTest {
        
        @Rule  // JUnit 4 style
        public ExpectedException thrown = ExpectedException.none();
        
        @Test
        void testSomething() {
            // Test that uses JUnit 5 style assertion
            assertThrows(IllegalArgumentException.class, () -> {
                // Code that throws exception
            });
            
            // And also JUnit 4 style
            thrown.expect(IllegalArgumentException.class);
            // Code that throws exception
        }
    }
    
    */
    
    // Solution:
    
    @SpringBootTest
    // No @RunWith annotation needed in JUnit 5
    static class ImprovedJUnit5StyleTest {
        
        @Test
        void testExceptionIsThrownForInvalidInput() {
            // Use JUnit 5 assertion style consistently
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                // Code that throws exception
                throw new IllegalArgumentException("Invalid input");
            });
            
            // You can also assert on the exception message
            String expectedMessage = "Invalid input";
            String actualMessage = exception.getMessage();
            org.junit.jupiter.api.Assertions.assertEquals(expectedMessage, actualMessage);
        }
    }
    
    /**
     * Pitfall 3: Poor Test Context Caching
     * 
     * Solution: Structure tests to maximize context reuse
     */
    
    /* Original code with pitfall:
    
    @SpringBootTest
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    class TestWithUnneededDirtyContext {
        
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @Test
        void test1() {
            // Test that could clean up after itself
        }
        
        @Test
        void test2() {
            // Another test that could clean up after itself
        }
    }
    
    */
    
    // Solution:
    
    @SpringBootTest
    static class ImprovedTestContextCaching {
        
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @BeforeEach
        void setUp() {
            // Set up test data
            jdbcTemplate.execute("INSERT INTO books (isbn, title) VALUES ('1', 'Test Book')");
        }
        
        @AfterEach
        void tearDown() {
            // Clean up after each test instead of recreating the entire context
            jdbcTemplate.execute("DELETE FROM books");
        }
        
        @Test
        void test1() {
            // Test logic using the data
        }
        
        @Test
        void test2() {
            // Another test using the data
        }
    }
    
    /**
     * Pitfall 4: Excessive Setup and Poor Organization
     * 
     * Solution: Use fixture methods, test helper classes, and test data builders
     */
    
    /* Original code with pitfall:
    
    @SpringBootTest
    class TestWithExcessiveSetup {
        
        @Autowired
        private BookRepository bookRepository;
        
        @Autowired
        private UserRepository userRepository;
        
        @Test
        void testCreateLoan() {
            // Verbose inline setup
            Book book = new Book();
            book.setIsbn("978-1-11111-111-1");
            book.setTitle("Clean Code");
            book.setAuthor("Robert C. Martin");
            book.setPublishedDate(LocalDate.of(2008, 8, 1));
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);
            
            LibraryUser user = new LibraryUser();
            user.setName("John Doe");
            user.setEmail("john@example.com");
            user.setMembershipNumber("LIB-1234");
            user.setMemberSince(LocalDate.now().minusYears(1));
            userRepository.save(user);
            
            // Now the actual test begins
            // ...more verbose code...
        }
    }
    
    */
    
    // Solution:
    
    @SpringBootTest
    static class ImprovedTestSetup {
        
        @Autowired
        private BookRepositoryStub bookRepository;
        
        @Autowired
        private UserRepositoryStub userRepository;
        
        // Helper method for creating a standard test book
        private BookStub createTestBook() {
            return new BookStub(
                "978-1-11111-111-1",
                "Clean Code",
                "Robert C. Martin",
                LocalDate.of(2008, 8, 1)
            );
        }
        
        // Helper method for creating a standard test user
        private UserStub createTestUser() {
            return new UserStub(
                "John Doe",
                "john@example.com",
                "LIB-1234",
                LocalDate.now().minusYears(1)
            );
        }
        
        @Test
        void testCreateLoan() {
            // Simplified setup using helper methods
            BookStub book = bookRepository.save(createTestBook());
            UserStub user = userRepository.save(createTestUser());
            
            // Now the test is more focused on what we're actually testing
            // ...test logic...
        }
    }
    
    /**
     * Pitfall 5: Poor Test Data Management
     * 
     * Solution: Use @Transactional tests, proper cleanup, or isolated test databases
     */
    
    /* Original code with pitfall:
    
    @SpringBootTest
    class TestWithPoorDataManagement {
        
        @Autowired
        private BookRepository bookRepository;
        
        @Test
        void test1() {
            // Creates data but doesn't clean up
            bookRepository.save(new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", 
                    LocalDate.of(2008, 8, 1)));
            
            // Test logic...
        }
        
        @Test
        void test2() {
            // Assumes the database is empty or in a specific state
            assertEquals(0, bookRepository.count());
            
            // More test logic...
        }
    }
    
    */
    
    // Solution 1: Using @Transactional tests
    
    @DataJpaTest // Uses an embedded database by default
    @Transactional
    static class ImprovedDataManagementWithTransactions {
        
        @Autowired
        private BookRepositoryStub bookRepository;
        
        @Test
        void test1() {
            // Creates data, but will be rolled back after the test
            bookRepository.save(new BookStub("978-1-11111-111-1", "Clean Code"));
            
            // Test logic...
            
            // No cleanup needed - transaction will be rolled back
        }
        
        @Test
        void test2() {
            // This test starts with a clean database due to transaction rollback
            org.junit.jupiter.api.Assertions.assertEquals(0, bookRepository.count());
            
            // More test logic...
        }
    }
    
    // Solution 2: Using SQL scripts for setup and cleanup
    
    @SpringBootTest
    @TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate", 
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE"
    })
    @Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    static class ImprovedDataManagementWithSqlScripts {
        
        @Autowired
        private BookRepositoryStub bookRepository;
        
        @Test
        void testWithPredefinedData() {
            // Test using data defined in test-data.sql
            // ...
        }
    }
    
    // Solution 3: Using programmatic transaction management
    
    @DataJpaTest
    static class ImprovedDataManagementWithProgrammaticTransactions {
        
        @Autowired
        private BookRepositoryStub bookRepository;
        
        @Test
        void testWithExplicitTransactionControl() {
            // Begin a transaction
            TestTransaction.start();
            
            // Perform test setup
            bookRepository.save(new BookStub("978-1-11111-111-1", "Clean Code"));
            
            // Test logic
            // ...
            
            // Don't forget to flag the transaction for rollback
            TestTransaction.flagForRollback();
            TestTransaction.end();
        }
    }
    
    // Stub classes to make the examples compile
    
    static class BookStub {
        private String isbn;
        private String title;
        private String author;
        private LocalDate publishedDate;
        
        public BookStub(String isbn, String title) {
            this.isbn = isbn;
            this.title = title;
        }
        
        public BookStub(String isbn, String title, String author, LocalDate publishedDate) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.publishedDate = publishedDate;
        }
    }
    
    static class UserStub {
        private String name;
        private String email;
        private String membershipNumber;
        private LocalDate memberSince;
        
        public UserStub(String name, String email, String membershipNumber, LocalDate memberSince) {
            this.name = name;
            this.email = email;
            this.membershipNumber = membershipNumber;
            this.memberSince = memberSince;
        }
    }
    
    static class BookControllerStub {
    }
    
    static class BookServiceStub {
        public BookStub findById(String id) {
            return new BookStub(id, "Test Book");
        }
    }
    
    interface BookRepositoryStub {
        BookStub save(BookStub book);
        long count();
    }
    
    interface UserRepositoryStub {
        UserStub save(UserStub user);
    }
}