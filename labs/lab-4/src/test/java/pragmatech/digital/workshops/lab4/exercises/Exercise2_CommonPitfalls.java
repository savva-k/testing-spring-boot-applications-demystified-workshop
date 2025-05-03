package pragmatech.digital.workshops.lab4.exercises;

/**
 * Exercise 2: Common Spring Boot Testing Pitfalls
 *
 * In this exercise, you will learn about common pitfalls when testing Spring Boot applications
 * and how to avoid them. You'll analyze and fix problematic test code that demonstrates
 * these pitfalls.
 *
 * Tasks:
 * 1. Identify and fix issues in test code that overuses @SpringBootTest.
 * 2. Address problems related to mixing JUnit 4 and JUnit 5 testing styles.
 * 3. Fix tests that don't leverage Spring's test context caching effectively.
 * 4. Correct test classes that have excessive setup and poor organization.
 * 5. Resolve issues with test data management and cleanup.
 */
public class Exercise2_CommonPitfalls {

    /**
     * Pitfall 1: Overusing @SpringBootTest
     * 
     * Problem: Using @SpringBootTest for simple component tests
     * wastes resources and slows down the test suite.
     * 
     * Fix: Use more focused test slice annotations like @WebMvcTest,
     * @DataJpaTest, or @JsonTest when appropriate.
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
    
    /**
     * Pitfall 2: Mixing JUnit 4 and JUnit 5
     * 
     * Problem: Mixing annotations and patterns from different JUnit versions
     * leads to confusion and potential issues.
     * 
     * Fix: Consistently use JUnit 5 style with its annotations and extensions.
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
    
    /**
     * Pitfall 3: Poor Test Context Caching
     * 
     * Problem: Creating a new Spring context for each test class
     * when they could share one significantly slows down test execution.
     * 
     * Fix: Structure tests to maximize context reuse and avoid
     * unnecessary @DirtiesContext usage.
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
    
    /**
     * Pitfall 4: Excessive Setup and Poor Organization
     * 
     * Problem: Tests with too much setup code are hard to understand and maintain.
     * 
     * Fix: Use fixture methods, test helper classes, and test data builders
     * to simplify test methods.
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
    
    /**
     * Pitfall 5: Poor Test Data Management
     * 
     * Problem: Tests that don't clean up after themselves or rely on
     * specific database state can be flaky and hard to debug.
     * 
     * Fix: Use @Transactional tests, proper cleanup, or isolated test databases.
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
}