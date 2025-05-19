package pragmatech.digital.workshops.lab2.experiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookStatus;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test class demonstrates testing JPA repositories with H2 in-memory database.
 * It also showcases the limitations when using PostgreSQL-specific features.
 */
@DataJpaTest
class BookRepositoryInMemoryTest {

    private static final Logger log =
      LoggerFactory.getLogger(BookRepositoryInMemoryTest.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSaveAndRetrieveBook() {
        // given
        Book book = new Book("978-1-2345-6789-0", "Test Book", "Test Author", LocalDate.of(2020, 1, 1));
        book.setStatus(BookStatus.AVAILABLE);

        // when
        Book savedBook = bookRepository.save(book);
        Book retrievedBook = bookRepository.findById(savedBook.getId()).orElseThrow();

        // then
        assertThat(retrievedBook).isNotNull();
        assertThat(retrievedBook.getIsbn()).isEqualTo("978-1-2345-6789-0");
        assertThat(retrievedBook.getTitle()).isEqualTo("Test Book");
        assertThat(retrievedBook.getAuthor()).isEqualTo("Test Author");
        assertThat(retrievedBook.getPublishedDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(retrievedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    void shouldFindBookByIsbn() {
        // given
        Book book = new Book("978-1-2345-6789-0", "Test Book", "Test Author", LocalDate.of(2020, 1, 1));
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        // when
        Book retrievedBook = bookRepository.findByIsbn("978-1-2345-6789-0").orElseThrow();

        // then
        assertThat(retrievedBook).isNotNull();
        assertThat(retrievedBook.getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldFailWhenUsingPostgresSpecificFullTextSearch() {
        // given
        Book book1 = new Book("978-1-1111-1111-1", "Advanced Java Programming", "Java Expert", LocalDate.of(2020, 1, 1));
        Book book2 = new Book("978-2-2222-2222-2", "Java for Beginners", "Another Author", LocalDate.of(2021, 2, 2));
        bookRepository.saveAll(List.of(book1, book2));

        // when & then
        Exception exception = assertThrows(Exception.class, () -> {
            List<Book> books = bookRepository.searchBooksByTitleWithRanking("java");
            log.info("Books found: {}", books);
        });

        log.error("Expected exception when using PostgreSQL-specific features: {}", exception.getMessage());
        assertThat(exception.getMessage()).contains("to_tsvector");
    }

    @Test
    void shouldFailWhenUsingTrigramSimilaritySearch() {
        // given
        Book book = new Book("978-1-1111-1111-1", "Advanced Java Programming", "Java Expert", LocalDate.of(2020, 1, 1));
        bookRepository.save(book);

        // when & then
        Exception exception = assertThrows(Exception.class, () -> {
            List<Book> books = bookRepository.findBooksByTitleFuzzy("Advanced Javaa", 0.7);
            log.info("Books found: {}", books);
        });

        log.error("Expected exception when using PostgreSQL-specific features: {}", exception.getMessage());
        assertThat(exception.getMessage()).contains("similarity");
    }

    @Test
    void shouldDemonstrateH2CompatibilityMode() {
        // Get H2 version and PostgreSQL compatibility mode
//        String dbInfo = jdbcTemplate.queryForObject("SELECT H2VERSION() || ' (Mode: ' || MODE() || ')'", String.class);
//        log.info("Running tests with: {}", dbInfo);

        // Verify we're using PostgreSQL mode
        String mode = jdbcTemplate.queryForObject("SELECT MODE()", String.class);

        assertEquals("PostgreSQL", mode, "H2 should be running in PostgreSQL mode");
    }
}
