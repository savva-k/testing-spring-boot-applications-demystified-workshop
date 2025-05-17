package pragmatech.digital.workshops.lab2.experiment;


import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@AutoConfigureTestDatabase
@DataJpaTest(showSql = false)
class BookRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

  @Autowired
  TestEntityManager entityManager;

  @Autowired
  BookRepository bookRepository;

  @Test
  void findAllBooks() {

    bookRepository.save(new Book("1234567890123", "Test Book", "Test Author", LocalDate.now()));

    entityManager.flush();

    var all = bookRepository.findAll();

    assertThat(all).isNotEmpty();
  }

  @Test
  void checkBooks() {
    var all = bookRepository.findAll();
    assertThat(all).isEmpty();
  }

  @Test
  void checkBooksAgain() {
    var all = bookRepository.findAll();
    assertThat(all).isEmpty();
  }
}
