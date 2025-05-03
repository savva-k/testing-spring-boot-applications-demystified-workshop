package pragmatech.digital.workshops.lab3.solutions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Solution for Exercise 2: Understanding and Optimizing Spring Test Context Caching
 */
@SpringBootTest
@ActiveProfiles("test")
public class Solution2_TestContextCaching {

    /**
     * Problem 1: Too many test configurations
     * 
     * Solution: Use a common configuration for all test classes.
     * 
     * - Use the same properties or profiles across test classes
     * - Extract common configuration to a base class or use @TestConfiguration
     * - Make sure random ports don't create different contexts by using specific port settings
     */
    
    /*
     * Original Problem:
     * 
     * @SpringBootTest(properties = "spring.profiles.active=test1")
     * class Test1 {
     *     // Tests
     * }
     * 
     * @SpringBootTest(properties = "spring.profiles.active=test2")
     * class Test2 {
     *     // Similar tests
     * }
     * 
     * @SpringBootTest(properties = {"spring.profiles.active=test1", "server.port=0"})
     * class Test3 {
     *     // More similar tests
     * }
     */
    
    // Solution:
    
    @SpringBootTest(properties = "server.port=8181")
    @ActiveProfiles("test")
    static class Test1 {
        @Test
        void example() {
            // Tests
        }
    }
    
    @SpringBootTest(properties = "server.port=8181")
    @ActiveProfiles("test")
    static class Test2 {
        @Test
        void example() {
            // Similar tests
        }
    }
    
    @SpringBootTest(properties = "server.port=8181")
    @ActiveProfiles("test")
    static class Test3 {
        @Test
        void example() {
            // More similar tests
        }
    }
    
    /**
     * Problem 2: Unnecessary @DirtiesContext usage
     * 
     * Solution: Instead of marking the context dirty, use proper test cleanup
     * mechanisms like @BeforeEach/@AfterEach to reset the state.
     */
    
    /*
     * Original Problem:
     * 
     * @SpringBootTest
     * @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
     * class TestWithDirtContext1 {
     *     @Autowired
     *     private JdbcTemplate jdbcTemplate;
     *     
     *     @Test
     *     void testSomething() {
     *         // Test that modifies database state
     *     }
     * }
     * 
     * @SpringBootTest
     * @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
     * class TestWithDirtContext2 {
     *     @Autowired
     *     private JdbcTemplate jdbcTemplate;
     *     
     *     @Test
     *     void testSomethingElse() {
     *         // Another test that modifies database state
     *     }
     * }
     */
    
    // Solution:
    
    @SpringBootTest
    @ActiveProfiles("test")
    static class TestWithProperCleanup1 {
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @AfterEach
        void cleanup() {
            // Clean up database state instead of recreating the whole context
            jdbcTemplate.execute("DELETE FROM book_loans");
            jdbcTemplate.execute("DELETE FROM book_reviews");
            jdbcTemplate.execute("DELETE FROM books");
            jdbcTemplate.execute("DELETE FROM library_users");
        }
        
        @Test
        void testSomething() {
            // Test that modifies database state
        }
    }
    
    @SpringBootTest
    @ActiveProfiles("test")
    static class TestWithProperCleanup2 {
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @AfterEach
        void cleanup() {
            // Same cleanup logic as above
            jdbcTemplate.execute("DELETE FROM book_loans");
            jdbcTemplate.execute("DELETE FROM book_reviews");
            jdbcTemplate.execute("DELETE FROM books");
            jdbcTemplate.execute("DELETE FROM library_users");
        }
        
        @Test
        void testSomethingElse() {
            // Another test that modifies database state
        }
    }
    
    /**
     * Problem 3: Modifying Beans
     * 
     * Solution: Use test-specific instances or configurations to avoid modifying shared beans.
     */
    
    /*
     * Original Problem:
     * 
     * @SpringBootTest
     * class TestModifyingBeans1 {
     *     @Autowired
     *     private SomeService someService;
     *     
     *     @Test
     *     void testFeature() {
     *         // This modifies the internal state of the service
     *         someService.setProperty("test value");
     *         // Test
     *     }
     * }
     * 
     * @SpringBootTest
     * class TestModifyingBeans2 {
     *     @Autowired
     *     private SomeService someService;
     *     
     *     @Test
     *     void testAnotherFeature() {
     *         // This also modifies the internal state of the service
     *         someService.setProperty("different value");
     *         // Test
     *     }
     * }
     */
    
    // Solution:
    
    // Example service class
    static class SomeService {
        private String property;
        
        public void setProperty(String property) {
            this.property = property;
        }
        
        public String getProperty() {
            return property;
        }
    }
    
    // Create test-specific configurations for beans that might be modified
    @TestConfiguration
    static class TestSpecificConfiguration {
        
        @Bean
        SomeService someService() {
            return new SomeService();
        }
    }
    
    @SpringBootTest
    @ActiveProfiles("test")
    static class TestNotModifyingSharedBeans1 {
        // Don't use the shared bean, create a new instance for each test
        private SomeService someService = new SomeService();
        
        @Test
        void testFeature() {
            someService.setProperty("test value");
            // Test using this instance
        }
    }
    
    @SpringBootTest
    @ActiveProfiles("test")
    static class TestNotModifyingSharedBeans2 {
        // Create another instance for this test
        private SomeService someService = new SomeService();
        
        @Test
        void testAnotherFeature() {
            someService.setProperty("different value");
            // Test using this instance
        }
    }
    
    /**
     * Additional Tips for Spring Test Context Caching
     * 
     * 1. Use the same configuration across tests when possible
     * 2. Avoid unnecessary @DirtiesContext annotations
     * 3. Reset state instead of recreating contexts
     * 4. Don't modify shared beans
     * 5. Use test slices (@WebMvcTest, @DataJpaTest) when you only need parts of the app
     * 6. Group similar tests together to maximize context reuse
     * 7. Look at logs for "Spring test ApplicationContext cache statistics" to verify caching
     * 8. Consider using @MockBean consistently across test classes to avoid context recreation
     */
}