package pragmatech.digital.workshops.lab2.experiment;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookStatus;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager testEntityManager;

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test")
    .withInitScript("init-postgres.sql");

    @Test
    void shouldVerifyJpaEntityCanBePersistedAndRead() {
        // given
        Book book = new Book("978-1-2345-6789-0", "Spring Boot Testing", "Test Author", LocalDate.of(2023, 1, 1));
        book.setStatus(BookStatus.AVAILABLE);

        // when
        testEntityManager.persistAndFlush(book);
        testEntityManager.clear(); // Clear persistence context to force a database read

        // then
        Book foundBook = testEntityManager.find(Book.class, book.getId());

        assertNotNull(foundBook);
        assertEquals("Spring Boot Testing", foundBook.getTitle());
        assertEquals("Test Author", foundBook.getAuthor());

        // Verify entity state is properly managed
        boolean managed = testEntityManager.getEntityManager()
                .contains(foundBook);
        assertTrue(managed, "Entity should be in managed state after find()");
    }

    @Test
    @Sql("/data/sample-books.sql")
    void shouldLoadDataFromSqlScript() {
        // when
        List<Book> books = bookRepository.findAll();

        // then
        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(3);

        // Verify specific book was loaded from script
        Optional<Book> cleanCode = books.stream()
                .filter(b -> b.getIsbn().equals("978-0-13-235088-4"))
                .findFirst();

        assertTrue(cleanCode.isPresent());
        assertEquals("Clean Code", cleanCode.get().getTitle());
        assertEquals("Robert C. Martin", cleanCode.get().getAuthor());
    }

    @Test
    void shouldDemonstrateLazyLoading() {
        // given
        Book book = new Book("978-1-2345-6789-0", "Spring Boot Testing", "Test Author", LocalDate.of(2023, 1, 1));
        testEntityManager.persistAndFlush(book);
        testEntityManager.clear();

        // when - load entity but don't access lazy collections
        Book loadedBook = testEntityManager.find(Book.class, book.getId());

        // Access ID which doesn't trigger lazy loading
        Long id = loadedBook.getId();
        assertNotNull(id);

        // Assert the Hibernate proxy works correctly
        EntityManager em = testEntityManager.getEntityManager();
        boolean initialized = em.getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .isLoaded(loadedBook, "reviews");

        assertFalse(initialized, "Collection should not be initialized until accessed");

        // This would trigger lazy loading if needed
        // int reviewCount = loadedBook.getReviews().size();
    }

    @Test
    void shouldUpdateExistingEntity() {
        // given
        Book book = new Book("978-1-2345-6789-0", "Original Title", "Original Author", LocalDate.of(2023, 1, 1));
        Book savedBook = testEntityManager.persistAndFlush(book);
        testEntityManager.clear();

        // when
        Book foundBook = testEntityManager.find(Book.class, savedBook.getId());
        foundBook.setTitle("Updated Title");
        foundBook.setAuthor("Updated Author");
        testEntityManager.persistAndFlush(foundBook);
        testEntityManager.clear();

        // then
        Book updatedBook = testEntityManager.find(Book.class, savedBook.getId());
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Updated Author", updatedBook.getAuthor());
        assertEquals("978-1-2345-6789-0", updatedBook.getIsbn(), "ISBN should not change");
    }
}
