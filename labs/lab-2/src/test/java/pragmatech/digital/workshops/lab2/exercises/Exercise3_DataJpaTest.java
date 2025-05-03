package pragmatech.digital.workshops.lab2.exercises;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

/**
 * Exercise 3: Testing Spring Data JPA Repositories
 *
 * In this exercise, you will learn how to test Spring Data JPA repositories
 * using the @DataJpaTest annotation, which provides an in-memory database
 * for testing and configures relevant Spring components.
 *
 * Tasks:
 * 1. Set up a proper test class using @DataJpaTest to test the BookRepository.
 * 2. Create test data in the database using TestEntityManager.
 * 3. Test the various finder methods in the BookRepository:
 *    a. findByAuthorContainingIgnoreCase
 *    b. findByTitleContainingIgnoreCase
 *    c. findByStatus
 *    d. findByPublishedDateAfter
 * 4. Test custom JPQL queries:
 *    a. findBooksWithHighRatings
 *    b. findByIsbnOrTitle
 * 5. Test native SQL queries:
 *    a. findByAuthorOrderByPublishedDateDesc
 * 6. Understand when the SQL is fired and how the EntityManager behaves.
 */
@DataJpaTest
public class Exercise3_DataJpaTest {

    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private BookRepository bookRepository;
    
    // Example test to show how tests pass with empty implementation
    @Test
    void exampleTest() {
        // This test is intentionally empty but will pass
    }
    
    // TODO: Set up test data in the database
    
    // TODO: Test findByAuthorContainingIgnoreCase method
    
    // TODO: Test findByTitleContainingIgnoreCase method
    
    // TODO: Test findByStatus method
    
    // TODO: Test findByPublishedDateAfter method
    
    // TODO: Test findBooksWithHighRatings custom JPQL query
    
    // TODO: Test findByIsbnOrTitle custom JPQL query
    
    // TODO: Test findByAuthorOrderByPublishedDateDesc native SQL query
    
    // TODO: Write a test to demonstrate when SQL is fired (hint: use transaction boundaries)
    
    // TODO: Write a test to demonstrate how lazy loading works in repositories
}