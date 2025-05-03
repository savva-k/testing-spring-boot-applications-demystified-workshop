package pragmatech.digital.workshops.lab4.solutions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Solution for Exercise 3: Spring Boot Testing Best Practices
 * 
 * This class demonstrates best practices for organizing and writing tests for Spring Boot applications.
 */
public class Solution3_BestPractices {

    /**
     * 1. Unit Tests
     * 
     * Best practices for unit tests:
     * - Focus on testing a single unit of functionality
     * - Use @ExtendWith(MockitoExtension.class) for Mockito integration
     * - Mock dependencies
     * - Use meaningful test method names
     * - Use DisplayName annotation for better readability
     * - Organize related tests in nested classes
     * - Use assertions that provide clear failure messages
     */
    @Nested
    @DisplayName("Unit Tests for LoanService")
    @ExtendWith(MockitoExtension.class)
    class LoanServiceTests {
        
        @Mock
        private BookRepositoryStub bookRepository;
        
        @Mock
        private UserRepositoryStub userRepository;
        
        @Mock
        private TimeProviderStub timeProvider;
        
        @InjectMocks
        private LoanServiceStub loanService;
        
        @Nested
        @DisplayName("When borrowing a book")
        class BorrowingBook {
            
            @Test
            @DisplayName("should create a loan when book and user are valid")
            void shouldCreateLoanWhenBookAndUserAreValid() {
                // Arrange (Given)
                String isbn = "978-1-11111-111-1";
                Long userId = 1L;
                LocalDate today = LocalDate.of(2023, 5, 15);
                
                BookStub book = new BookStub(isbn, "Clean Code");
                UserStub user = new UserStub(userId, "John Doe");
                
                when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(timeProvider.getCurrentDate()).thenReturn(today);
                
                // Act (When)
                LoanStub loan = loanService.borrowBook(isbn, userId);
                
                // Assert (Then)
                assertNotNull(loan);
                assertEquals(book, loan.getBook());
                assertEquals(user, loan.getUser());
                assertEquals(today, loan.getLoanDate());
                assertEquals(today.plusDays(14), loan.getDueDate());
                
                // Use verify to ensure methods were called as expected
                verify(bookRepository).findByIsbn(isbn);
                verify(userRepository).findById(userId);
                verify(book).setStatus(BookStatusStub.BORROWED);
                verify(bookRepository).save(book);
            }
            
            @Test
            @DisplayName("should throw exception when book is not found")
            void shouldThrowExceptionWhenBookNotFound() {
                // Arrange
                String isbn = "non-existent-isbn";
                Long userId = 1L;
                
                when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
                
                // Act & Assert
                Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                        loanService.borrowBook(isbn, userId));
                
                assertTrue(exception.getMessage().contains("Book not found"));
                
                // Verify
                verify(bookRepository).findByIsbn(isbn);
                verify(userRepository).findById(any()); // Not called because book lookup failed
            }
            
            @ParameterizedTest
            @DisplayName("should throw exception when book status is invalid")
            @ValueSource(strings = {"BORROWED", "RESERVED", "LOST", "UNDER_REPAIR"})
            void shouldThrowExceptionWhenBookStatusIsInvalid(String status) {
                // Arrange
                String isbn = "978-1-11111-111-1";
                Long userId = 1L;
                
                BookStub book = new BookStub(isbn, "Clean Code");
                book.setStatus(BookStatusStub.valueOf(status));
                
                when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
                
                // Act & Assert
                Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                        loanService.borrowBook(isbn, userId));
                
                assertTrue(exception.getMessage().contains("not available"));
            }
        }
        
        @Nested
        @DisplayName("When returning a book")
        class ReturningBook {
            
            @Test
            @DisplayName("should update loan and book status when successful")
            void shouldUpdateLoanAndBookStatusWhenSuccessful() {
                // Arrange
                String loanId = "loan1";
                LocalDate today = LocalDate.of(2023, 5, 15);
                
                BookStub book = new BookStub("978-1-11111-111-1", "Clean Code");
                book.setStatus(BookStatusStub.BORROWED);
                
                UserStub user = new UserStub(1L, "John Doe");
                
                LoanStub loan = new LoanStub(loanId, book, user, today.minusDays(7), today.plusDays(7));
                
                when(loanService.findLoanById(loanId)).thenReturn(Optional.of(loan));
                when(timeProvider.getCurrentDate()).thenReturn(today);
                
                // Act
                LoanStub returnedLoan = loanService.returnBook(loanId);
                
                // Assert
                assertNotNull(returnedLoan);
                assertEquals(today, returnedLoan.getReturnDate());
                verify(book).setStatus(BookStatusStub.AVAILABLE);
                verify(bookRepository).save(book);
            }
            
            @Test
            @DisplayName("should throw exception when loan is not found")
            void shouldThrowExceptionWhenLoanNotFound() {
                // Arrange
                String loanId = "non-existent-loan";
                
                when(loanService.findLoanById(loanId)).thenReturn(Optional.empty());
                
                // Act & Assert
                Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                        loanService.returnBook(loanId));
                
                assertTrue(exception.getMessage().contains("Loan not found"));
            }
        }
    }
    
    /**
     * 2. Repository Integration Tests
     * 
     * Best practices for repository tests:
     * - Use @DataJpaTest for repository tests
     * - Configure test properties appropriately
     * - Use TestEntityManager for test setup when needed
     * - Clean up test data properly
     * - Test the actual SQL that is generated
     */
    @Nested
    @DisplayName("Repository Integration Tests")
    @DataJpaTest
    @ActiveProfiles("test")
    @Transactional
    class RepositoryTests {
        
        @Autowired
        private BookRepositoryStub bookRepository;
        
        @Test
        @DisplayName("findByAuthorContainingIgnoreCase should return books by matching author")
        void findByAuthorContainingIgnoreCaseShouldReturnBooksByMatchingAuthor() {
            // Arrange
            bookRepository.save(new BookStub("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1)));
            bookRepository.save(new BookStub("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24)));
            
            // Act
            List<BookStub> martinBooks = bookRepository.findByAuthorContainingIgnoreCase("martin");
            
            // Assert
            assertThat(martinBooks).hasSize(1);
            assertThat(martinBooks.get(0).getTitle()).isEqualTo("Clean Code");
        }
        
        @ParameterizedTest
        @DisplayName("findByPublishedDateBetween should return books published in date range")
        @CsvSource({
            "2000-01-01, 2010-01-01, 1",
            "2015-01-01, 2020-01-01, 1",
            "1990-01-01, 2000-01-01, 0"
        })
        void findByPublishedDateBetweenShouldReturnBooksInDateRange(LocalDate start, LocalDate end, int expectedCount) {
            // Arrange
            bookRepository.save(new BookStub("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1)));
            bookRepository.save(new BookStub("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24)));
            
            // Act
            List<BookStub> books = bookRepository.findByPublishedDateBetween(start, end);
            
            // Assert
            assertThat(books).hasSize(expectedCount);
        }
        
        @Test
        @DisplayName("Custom query should find books with high ratings")
        @Sql(scripts = {"/test-data.sql"}) // Use SQL scripts for complex data setup
        void customQueryShouldFindBooksWithHighRatings() {
            // Act
            List<BookStub> highRatedBooks = bookRepository.findBooksWithHighRatings();
            
            // Assert
            assertThat(highRatedBooks).hasSize(2);
            assertThat(highRatedBooks).extracting(BookStub::getTitle)
                    .containsExactlyInAnyOrder("Clean Code", "Effective Java");
        }
    }
    
    /**
     * 3. Controller Tests
     * 
     * Best practices for controller tests:
     * - Use @WebMvcTest to test only the web layer
     * - Mock the service layer
     * - Test HTTP-specific concerns like status codes, headers, and response bodies
     * - Test error handling and edge cases
     */
    @Nested
    @DisplayName("Controller Tests")
    @WebMvcTest(controllers = BookControllerStub.class)
    class ControllerTests {
        
        @Autowired
        private MockMvc mockMvc;
        
        @MockBean
        private BookServiceStub bookService;
        
        @Test
        @DisplayName("GET /api/books should return all books")
        void getShouldReturnAllBooks() throws Exception {
            // Arrange
            List<BookStub> books = Arrays.asList(
                    new BookStub("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1)),
                    new BookStub("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24))
            );
            
            when(bookService.findAllBooks()).thenReturn(books);
            
            // Act & Assert
            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].isbn").value("978-1-11111-111-1"))
                    .andExpect(jsonPath("$[1].isbn").value("978-2-22222-222-2"));
        }
        
        @Test
        @DisplayName("GET /api/books/{isbn} should return 404 when book not found")
        void getShouldReturn404WhenBookNotFound() throws Exception {
            // Arrange
            when(bookService.findBookByIsbn(anyString())).thenReturn(Optional.empty());
            
            // Act & Assert
            mockMvc.perform(get("/api/books/non-existent-isbn"))
                    .andExpect(status().isNotFound());
        }
    }
    
    /**
     * 4. End-to-End/Integration Tests
     * 
     * Best practices for E2E tests:
     * - Use @SpringBootTest to test the entire application
     * - Use TestContainers for external dependencies
     * - Focus on user flows rather than implementation details
     * - Clean up test data properly
     * - Use appropriate profiles and configurations
     */
    @Nested
    @DisplayName("End-to-End Integration Tests")
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("test")
    class E2ETests {
        
        @Autowired
        private WebTestClient webTestClient;
        
        @MockBean
        private BookServiceStub bookService;
        
        @Test
        @DisplayName("Full loan workflow should work correctly")
        void fullLoanWorkflowShouldWorkCorrectly() {
            // Arrange
            String isbn = "978-1-11111-111-1";
            Long userId = 1L;
            
            BookStub book = new BookStub(isbn, "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
            UserStub user = new UserStub(userId, "John Doe");
            
            when(bookService.findBookByIsbn(isbn)).thenReturn(Optional.of(book));
            
            // Act & Assert - Step 1: Check book availability
            webTestClient.get()
                    .uri("/api/books/{isbn}", isbn)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.isbn").isEqualTo(isbn)
                    .jsonPath("$.status").isEqualTo("AVAILABLE");
            
            // Act & Assert - Step 2: Create a loan
            /* 
               Note: In a real test, you would make the actual HTTP request 
               and verify the database state after each step.
               Simplified for example purposes.
            */
            
            // Act & Assert - Step 3: Verify the loan
            
            // Act & Assert - Step 4: Return the book
            
            // Act & Assert - Step 5: Verify the book is available again
        }
    }
    
    /**
     * 5. Test Configuration and Data Management Best Practices
     * 
     * - Use TestConfiguration for special beans needed in tests
     * - Use appropriate data management strategy (transaction, SQL scripts, etc.)
     * - Use test data builders or fixtures for test data
     */
    @TestConfiguration
    static class TestConfig {
        
        @Bean
        public TimeProviderStub timeProvider() {
            return new TimeProviderStub();
        }
        
        // Other test-specific beans...
    }
    
    /**
     * Stub helper classes for the examples
     */
    interface BookRepositoryStub {
        BookStub save(BookStub book);
        Optional<BookStub> findByIsbn(String isbn);
        List<BookStub> findByAuthorContainingIgnoreCase(String author);
        List<BookStub> findByPublishedDateBetween(LocalDate start, LocalDate end);
        List<BookStub> findBooksWithHighRatings();
    }
    
    interface UserRepositoryStub {
        Optional<UserStub> findById(Long id);
    }
    
    static class BookServiceStub {
        public List<BookStub> findAllBooks() {
            return null;
        }
        
        public Optional<BookStub> findBookByIsbn(String isbn) {
            return null;
        }
    }
    
    static class BookControllerStub {
    }
    
    static class BookStub {
        private String isbn;
        private String title;
        private String author;
        private LocalDate publishedDate;
        private BookStatusStub status = BookStatusStub.AVAILABLE;
        
        public BookStub(String isbn, String title) {
            this.isbn = isbn;
            this.title = title;
        }
        
        public BookStub(String isbn, String title, String author, LocalDate publishedDate) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.publishedDate = publishedDate;
        }
        
        public String getIsbn() {
            return isbn;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getAuthor() {
            return author;
        }
        
        public LocalDate getPublishedDate() {
            return publishedDate;
        }
        
        public BookStatusStub getStatus() {
            return status;
        }
        
        public void setStatus(BookStatusStub status) {
            this.status = status;
        }
    }
    
    static class UserStub {
        private Long id;
        private String name;
        
        public UserStub(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Long getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
    }
    
    static class LoanStub {
        private String id;
        private BookStub book;
        private UserStub user;
        private LocalDate loanDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        
        public LoanStub(String id, BookStub book, UserStub user, LocalDate loanDate, LocalDate dueDate) {
            this.id = id;
            this.book = book;
            this.user = user;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
        }
        
        public String getId() {
            return id;
        }
        
        public BookStub getBook() {
            return book;
        }
        
        public UserStub getUser() {
            return user;
        }
        
        public LocalDate getLoanDate() {
            return loanDate;
        }
        
        public LocalDate getDueDate() {
            return dueDate;
        }
        
        public LocalDate getReturnDate() {
            return returnDate;
        }
        
        public void setReturnDate(LocalDate returnDate) {
            this.returnDate = returnDate;
        }
    }
    
    enum BookStatusStub {
        AVAILABLE,
        BORROWED,
        RESERVED,
        LOST,
        UNDER_REPAIR
    }
    
    interface TimeProviderStub {
        LocalDate getCurrentDate();
    }
    
    static class LoanServiceStub {
        public LoanStub borrowBook(String isbn, Long userId) {
            return null;
        }
        
        public LoanStub returnBook(String loanId) {
            return null;
        }
        
        public Optional<LoanStub> findLoanById(String loanId) {
            return null;
        }
    }
}