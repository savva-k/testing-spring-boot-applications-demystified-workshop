package pragmatech.digital.workshops.lab3.exercises;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import pragmatech.digital.workshops.lab3.config.PostgresTestContainer;

/**
 * Exercise 1: Integration Testing with @SpringBootTest
 *
 * In this exercise, you will learn how to use @SpringBootTest for integration testing of your Spring Boot application.
 * Unlike sliced tests, @SpringBootTest loads the entire application context and allows you to test
 * the interaction between different layers of your application.
 *
 * You'll use TestContainers to create a PostgreSQL database for your tests, ensuring that
 * they run in an environment as close as possible to production.
 *
 * Tasks:
 * 1. Set up a proper test class that uses @SpringBootTest and extends PostgresTestContainer.
 * 2. Configure the test to use a specific test profile.
 * 3. Inject and test the BookController, ensuring all layers of the application are working together.
 * 4. Test data cleanup between tests to ensure test isolation.
 * 5. Use TestRestTemplate to test the API from an external client perspective.
 * 6. Assert that the database is properly updated when making API calls.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class Exercise1_SpringBootTest extends PostgresTestContainer {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // Example test that will pass
    @Test
    void contextLoads() {
        // This test just verifies that the Spring context loads correctly
    }
    
    // TODO: Set up test data and clean up after each test
    
    // TODO: Write test for GET /api/books
    
    // TODO: Write test for POST /api/books
    
    // TODO: Write test for PUT /api/books/{isbn}
    
    // TODO: Write test for DELETE /api/books/{isbn}
    
    // TODO: Write test for booking a loan and verifying database state
}