package pragmatech.digital.workshops.lab2.exercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Exercise 2: Data JPA Test
 * <p>
 * This exercise demonstrates testing a Spring Data JPA repository with Testcontainers
 * <p>
 * Tasks:
 * 1. Test the native query searchBooksByTitleWithRanking in BookRepository
 * 2. Ensure proper test data setup
 * 3. Ensure no data exists after the tests (test isolation)
 * 4. Use Testcontainers for a real PostgreSQL instance
 */
@DataJpaTest
class Exercise2DataJpaTest {

  @Autowired
  private BookRepository bookRepository;

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test")
    .withInitScript("init-postgres.sql");

  @Nested
  @DisplayName("Full text search tests")
  class FullTextSearchTests {

    @Test
    @DisplayName("Should find books by title with proper ranking")
    void shouldFindBooksByTitleWithProperRanking() {
      Book book1 = new Book("123", "Test1", "Test1", LocalDate.now());
      Book book2 = new Book("321", "Test2", "Test2", LocalDate.now());
      String searchTerm = "Test1";

      bookRepository.save(book1);
      bookRepository.save(book2);

      List<Book> books = bookRepository.searchBooksByTitleWithRanking(searchTerm);

      assertEquals(1, books.size());
      assertEquals(searchTerm, book1.getTitle());
    }

    @Test
    @DisplayName("Should return empty list when no books match search term")
    void shouldReturnEmptyListWhenNoBooksMatchSearchTerm() {
      List<Book> books = bookRepository.findAll();
      assertEquals(0, books.size());
    }
  }
}
