package pragmatech.digital.workshops.lab3.exercises;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Exercise 2: Understanding and Optimizing Spring Test Context Caching
 *
 * In this exercise, you will learn about Spring's test context caching mechanism,
 * which can significantly improve test performance by reusing application contexts
 * across test classes.
 *
 * You'll discover how to identify and fix issues that prevent effective context caching,
 * such as modifying beans, using different configurations, or using @DirtiesContext unnecessarily.
 *
 * Tasks:
 * 1. Understand what causes test contexts to be recreated instead of reused.
 * 2. Identify problems in three different test classes that prevent context caching.
 * 3. Fix the test classes to use a single shared context for better performance.
 * 4. Learn how to use Spring's ApplicationContextKey to see if contexts are shared.
 * 5. Demonstrate the performance difference before and after optimizations.
 *
 * Tip: Look at logs containing "Spring test ApplicationContext cache statistics"
 * to see if contexts are being reused or recreated.
 */
@SpringBootTest
public class Exercise2_TestContextCaching {

    /**
     * Problem 1: Too many test configurations
     * 
     * The tests below have different configurations, forcing Spring to create 
     * a separate context for each test class, even though they could share one.
     * 
     * Task: Identify what's different and fix it to use a common configuration.
     */
    
    /* Original Code:
    
    @SpringBootTest(properties = "spring.profiles.active=test1")
    class Test1 {
        // Tests
    }
    
    @SpringBootTest(properties = "spring.profiles.active=test2")
    class Test2 {
        // Similar tests
    }
    
    @SpringBootTest(properties = {"spring.profiles.active=test1", "server.port=0"})
    class Test3 {
        // More similar tests
    }
    
    */
    
    /**
     * Problem 2: Unnecessary @DirtiesContext usage
     * 
     * The tests below mark the context as "dirty", forcing it to be recreated,
     * when they could instead use other ways to clean up state between tests.
     * 
     * Task: Remove @DirtiesContext and implement proper cleanup mechanisms.
     */
    
    /* Original Code:
    
    @SpringBootTest
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    class TestWithDirtContext1 {
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @Test
        void testSomething() {
            // Test that modifies database state
        }
    }
    
    @SpringBootTest
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    class TestWithDirtContext2 {
        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        @Test
        void testSomethingElse() {
            // Another test that modifies database state
        }
    }
    
    */
    
    /**
     * Problem 3: Modifying Beans
     * 
     * These tests modify beans in the application context,
     * which causes the context to be marked as dirty.
     * 
     * Task: Restructure the tests to avoid modifying shared beans.
     */
    
    /* Original Code:
    
    @SpringBootTest
    class TestModifyingBeans1 {
        @Autowired
        private SomeService someService;
        
        @Test
        void testFeature() {
            // This modifies the internal state of the service
            someService.setProperty("test value");
            // Test
        }
    }
    
    @SpringBootTest
    class TestModifyingBeans2 {
        @Autowired
        private SomeService someService;
        
        @Test
        void testAnotherFeature() {
            // This also modifies the internal state of the service
            someService.setProperty("different value");
            // Test
        }
    }
    
    */
}