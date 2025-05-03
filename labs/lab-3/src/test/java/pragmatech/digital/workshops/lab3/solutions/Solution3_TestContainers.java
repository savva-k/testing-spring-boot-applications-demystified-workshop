package pragmatech.digital.workshops.lab3.solutions;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Solution for Exercise 3: Advanced TestContainers Usage
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class Solution3_TestContainers {

    /**
     * Create a network for container communication
     */
    static final Network network = Network.newNetwork();
    
    /**
     * Configure a PostgreSQL container with explicit startup strategy
     */
    @Container
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 2));
    
    /**
     * Provide dynamic properties to Spring based on the container
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
    
    /**
     * Configuration to start all containers in parallel
     */
    static {
        Startables.deepStart(Stream.of(postgresContainer)).join();
        
        // Initialize Flyway to set up the database schema
        Flyway flyway = Flyway.configure()
                .dataSource(postgresContainer.getJdbcUrl(), 
                        postgresContainer.getUsername(), 
                        postgresContainer.getPassword())
                .locations("classpath:db/migration")
                .load();
        
        // Clean and migrate to ensure a fresh database for each test run
        flyway.clean();
        flyway.migrate();
    }
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        public JdbcTemplate jdbcTemplate() {
            // Create a DataSource from the container's connection info
            org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = 
                new org.springframework.jdbc.datasource.DriverManagerDataSource();
            dataSource.setUrl(postgresContainer.getJdbcUrl());
            dataSource.setUsername(postgresContainer.getUsername());
            dataSource.setPassword(postgresContainer.getPassword());
            return new JdbcTemplate(dataSource);
        }
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @BeforeEach
    void setUp() {
        // Clean up tables between tests
        jdbcTemplate.execute("DELETE FROM book_loans");
        jdbcTemplate.execute("DELETE FROM book_reviews");
        jdbcTemplate.execute("DELETE FROM books");
        jdbcTemplate.execute("DELETE FROM library_users");
        
        // Insert test data
        jdbcTemplate.execute("INSERT INTO books (isbn, title, author, published_date, status) " +
                "VALUES ('978-1-11111-111-1', 'Clean Code', 'Robert C. Martin', '2008-08-01', 'AVAILABLE')");
        
        jdbcTemplate.execute("INSERT INTO library_users (id, name, email, membership_number, member_since) " +
                "VALUES (1, 'Test User', 'test@example.com', 'LIB-TEST', '2022-01-01')");
    }
    
    @Test
    @DisplayName("GET /api/books should return books from the database")
    void getBooksReturnsDataFromDatabase() {
        // Act
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "/api/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("978-1-11111-111-1", response.getBody().get(0).get("isbn"));
        assertEquals("Clean Code", response.getBody().get(0).get("title"));
    }
    
    @Test
    @DisplayName("Database should be correctly initialized with Flyway migrations")
    void databaseShouldBeCorrectlyInitialized() {
        // Verify tables were created correctly
        assertDoesNotThrow(() -> {
            // Check books table
            int booksCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM books", Integer.class);
            assertEquals(1, booksCount);
            
            // Check library_users table
            int usersCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM library_users", Integer.class);
            assertEquals(1, usersCount);
            
            // Check constraints and relationships
            boolean bookLoansForeignKeyExists = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.table_constraints " +
                            "WHERE constraint_name LIKE 'book_loans%fk%' AND table_name = 'book_loans')",
                    Boolean.class);
            assertTrue(bookLoansForeignKeyExists);
        });
    }
    
    @Test
    @DisplayName("Created book should be stored in the database")
    void createdBookShouldBeStoredInDatabase() {
        // Arrange
        Map<String, Object> newBook = Map.of(
                "isbn", "978-2-22222-222-2",
                "title", "Effective Java",
                "author", "Joshua Bloch",
                "publishedDate", "2017-10-24",
                "status", "AVAILABLE"
        );
        
        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity("/api/books", newBook, Map.class);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        // Verify the book was added to the database
        Map<String, Object> bookInDb = jdbcTemplate.queryForMap(
                "SELECT * FROM books WHERE isbn = ?",
                "978-2-22222-222-2"
        );
        
        assertEquals(newBook.get("title"), bookInDb.get("title"));
        assertEquals(newBook.get("author"), bookInDb.get("author"));
    }
    
    /**
     * BONUS: Example of a multi-container setup
     * 
     * In a real-world scenario, you might have multiple containers,
     * such as PostgreSQL for the database and Redis for caching.
     * Here's an example of how to set up and connect multiple containers.
     */
    
    /*
    // Redis container example
    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2-alpine")
            .withExposedPorts(6379)
            .withNetwork(network)
            .withNetworkAliases("redis");
    
    // Additional dynamic property for Redis
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
    }
    
    @Test
    @DisplayName("Test with multiple containers")
    void testWithMultipleContainers() {
        // Test that uses both PostgreSQL and Redis containers
        
        // Example: Create a book
        // ... (API call to create a book)
        
        // Verify it's in the database
        // ... (JDBC query to verify)
        
        // Verify it's cached in Redis
        // ... (Redis client to verify)
    }
    */
}