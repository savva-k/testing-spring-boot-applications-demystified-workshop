package pragmatech.digital.workshops.lab3.exercises;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Exercise 3: Advanced TestContainers Usage
 *
 * In this exercise, you will learn how to use TestContainers for more complex
 * testing scenarios. You'll set up and use TestContainers to provide realistic
 * external dependencies for your integration tests.
 *
 * Tasks:
 * 1. Configure a PostgreSQL container for database testing.
 * 2. Use Flyway for schema and data migration in the container.
 * 3. Set up a custom TestContainers-based test with multiple containers.
 * 4. Configure container startup and shutdown strategies for test optimization.
 * 5. Use TestContainers' networking features to connect containers.
 * 6. Write a test that verifies database state after API operations.
 */
@SpringBootTest
@Testcontainers
public class Exercise3_TestContainers {

    // Example container definition
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    // Example of dynamically setting Spring properties from container
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
    
    // Example test that will pass
    @Test
    void containerShouldBeRunning() {
        // This test just verifies that the container is running
        assert postgresContainer.isRunning();
    }
    
    // TODO: Configure Flyway to initialize the database with test data
    
    // TODO: Set up container startup and shutdown strategies
    
    // TODO: Create test that uses the container and verifies database state
    
    // BONUS: Use container networking to connect multiple containers
}