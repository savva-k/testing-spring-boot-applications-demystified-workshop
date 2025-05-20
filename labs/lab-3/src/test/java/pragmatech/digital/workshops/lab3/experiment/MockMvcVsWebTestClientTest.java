package pragmatech.digital.workshops.lab3.experiment;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.entity.BookStatus;
import pragmatech.digital.workshops.lab3.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This test class demonstrates key differences between MockMvc and WebTestClient:
 * <p>
 * 1. Thread Context:
 * - MockMvc runs in the same thread as the test method
 * - WebTestClient runs in a separate thread (reactive/non-blocking)
 * <p>
 * 2. Data Access:
 * - MockMvc shares the same transaction context as the test method
 * - WebTestClient runs in a separate thread with a separate transaction
 * <p>
 * 3. Transaction Rollback:
 * - Changes made through MockMvc are rolled back after the test completes
 * - Changes made through WebTestClient are committed and not rolled back
 * <p>
 * These differences are important to understand when choosing the appropriate
 * test approach, especially when testing transactional behavior or when the test
 * requires access to data created within the test transaction.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Comparing MockMvc vs WebTestClient")
class MockMvcVsWebTestClientTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private BookRepository bookRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @LocalServerPort
  private int port;

  private WebTestClient webTestClient;

  /**
   * Thread Context Tests demonstrate that:
   * - MockMvc executes the controller code in the same thread as the test
   * - WebTestClient executes the controller code in a different thread
   * <p>
   * This has implications for ThreadLocal variables and context propagation.
   */
  @Nested
  @DisplayName("Thread Context Tests")
  class ThreadContextTests {

    @Test
    @DisplayName("should remain in same thread when using MockMvc")
    void shouldRemainInSameThreadWhenUsingMockMvc() throws Exception {
      // Arrange
      AtomicReference<Long> testThreadId = new AtomicReference<>(Thread.currentThread().getId());
      AtomicReference<Long> controllerThreadId = new AtomicReference<>();

      // Act & Assert
      mockMvc.perform(get("/api/books/thread-id")
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(result -> {
          controllerThreadId.set(Long.valueOf(result.getResponse().getContentAsString()));
        });

      // Assert
      assertThat(controllerThreadId.get()).isEqualTo(testThreadId.get());
    }

    @Test
    @DisplayName("should use different thread when using WebTestClient")
    void shouldUseDifferentThreadWhenUsingWebTestClient() {
      // Arrange
      AtomicReference<Long> testThreadId = new AtomicReference<>(Thread.currentThread().getId());
      AtomicReference<Long> controllerThreadId = new AtomicReference<>();

      // Act & Assert
      webTestClient.get().uri("/api/books/thread-id")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .consumeWith(response -> {
          controllerThreadId.set(Long.valueOf(response.getResponseBody()));
        });

      // Assert
      assertThat(controllerThreadId.get()).isNotEqualTo(testThreadId.get());
    }
  }

  /**
   * Data Access Tests demonstrate that:
   * - With MockMvc, the controller can access data created in the test transaction
   * - With WebTestClient, the controller cannot access uncommitted data from the test transaction
   * <p>
   * This is because WebTestClient uses a separate transaction in a different thread.
   */
  @Nested
  @DisplayName("Data Access Tests")
  @Transactional
  class DataAccessTests {

    @Test
    @DisplayName("should access modified data during request with MockMvc")
    void shouldAccessModifiedDataDuringRequestWithMockMvc() throws Exception {
      // Arrange - Create a book in the test transaction
      Book book = new Book();
      book.setIsbn("1234567890");
      book.setTitle("Test Book");
      book.setAuthor("Test Author");
      book.setPublishedDate(LocalDate.now());
      book.setStatus(BookStatus.AVAILABLE);
      bookRepository.save(book);
      entityManager.flush();

      // Act & Assert - MockMvc can see the book because it shares the transaction
      mockMvc.perform(get("/api/books/data-access/{isbn}", book.getIsbn())
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should not access uncommitted data with WebTestClient")
    @DirtiesContext
    void shouldNotAccessUncommittedDataWithWebTestClient() {
      // Arrange - Create a book in the test transaction
      Book book = new Book();
      book.setIsbn("0987654321");
      book.setTitle("Test Book WebClient");
      book.setAuthor("Test Author");
      book.setPublishedDate(LocalDate.now());
      book.setStatus(BookStatus.AVAILABLE);
      bookRepository.save(book);
      entityManager.flush();

      // Act & Assert - WebTestClient cannot see the book because it uses a different transaction
      webTestClient.get().uri("/api/books/data-access/{isbn}", book.getIsbn())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
    }
  }

  /**
   * Transaction Rollback Tests demonstrate that:
   * - Changes made through MockMvc are rolled back with the test transaction
   * - Changes made through WebTestClient are committed and not rolled back
   * <p>
   * This is because WebTestClient operates in a separate thread with its own
   * transaction that gets committed independently of the test transaction.
   */
  @Nested
  @DisplayName("Transaction Rollback Tests")
  @Transactional
  class TransactionRollbackTests {

    @Test
    @DisplayName("should rollback changes after MockMvc test")
    void shouldRollbackChangesAfterMockMvcTest() throws Exception {
      // Arrange
      String isbn = "1122334455";

      // Act - Create a book through the controller with MockMvc
      mockMvc.perform(get("/api/books/create-for-test/{isbn}/{title}", isbn, "Rollback Test MockMvc")
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

      // Assert - Book is visible in the test transaction, but will be rolled back after test
      assertThat(bookRepository.findByIsbn(isbn)).isPresent();
      assertThat(TestTransaction.isActive()).isTrue();
      // Note: After this test completes, the transaction will be rolled back
    }

    @Test
    @DisplayName("should not rollback changes created by WebTestClient")
    @DirtiesContext
    void shouldNotRollbackChangesCreatedByWebTestClient() throws Exception {
      // Arrange
      String isbn = "5544332211";
      assertThat(bookRepository.findByIsbn(isbn)).isEmpty();

      // Act - Create a book through the controller with WebTestClient in a separate thread
      CountDownLatch latch = new CountDownLatch(1);
      ExecutorService executor = Executors.newSingleThreadExecutor();

      executor.submit(() -> {
        try {
          webTestClient.get().uri("/api/books/create-for-test/{isbn}/{title}", isbn, "Rollback Test WebClient")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk();
          latch.countDown();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      });

      latch.await();
      executor.shutdown();

      // Assert - Book is committed by the WebTestClient thread and remains in the database
      // We commit and restart the test transaction to see the committed changes
      TestTransaction.flagForCommit();
      TestTransaction.end();
      TestTransaction.start();

      assertThat(bookRepository.findByIsbn(isbn)).isPresent();
      assertThat(bookRepository.findByIsbn(isbn).get().getTitle()).isEqualTo("Rollback Test WebClient");
      // Note: This book will remain in the database even after the test completes
    }
  }
}
