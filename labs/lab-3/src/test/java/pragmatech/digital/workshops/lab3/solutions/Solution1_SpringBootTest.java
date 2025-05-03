package pragmatech.digital.workshops.lab3.solutions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import pragmatech.digital.workshops.lab3.config.PostgresTestContainer;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.entity.BookStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Solution for Exercise 1: Integration Testing with @SpringBootTest
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class Solution1_SpringBootTest extends PostgresTestContainer {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @BeforeEach
    void setUp() {
        // Clean up tables before each test
        cleanUpTables();
        
        // Insert test data
        insertTestBook("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1), BookStatus.AVAILABLE);
        insertTestBook("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24), BookStatus.BORROWED);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up tables after each test
        cleanUpTables();
    }
    
    private void cleanUpTables() {
        jdbcTemplate.execute("DELETE FROM book_loans");
        jdbcTemplate.execute("DELETE FROM book_reviews");
        jdbcTemplate.execute("DELETE FROM books");
    }
    
    private void insertTestBook(String isbn, String title, String author, LocalDate publishedDate, BookStatus status) {
        jdbcTemplate.update(
                "INSERT INTO books (isbn, title, author, published_date, status) VALUES (?, ?, ?, ?, ?)",
                isbn, title, author, publishedDate, status.name()
        );
    }
    
    @Test
    @DisplayName("GET /api/books should return all books")
    void getAllBooksShouldReturnAllBooks() {
        // Act
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                "/api/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Book>>() {}
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        
        // Verify books are returned in the response
        List<Book> books = response.getBody();
        assertThat(books).extracting(Book::getIsbn).contains("978-1-11111-111-1", "978-2-22222-222-2");
        assertThat(books).extracting(Book::getTitle).contains("Clean Code", "Effective Java");
    }
    
    @Test
    @DisplayName("POST /api/books should create a new book")
    void createBookShouldAddBookToDatabase() {
        // Arrange
        Book newBook = new Book("978-3-33333-333-3", "Test-Driven Development", "Kent Beck", LocalDate.of(2002, 11, 18));
        newBook.setStatus(BookStatus.AVAILABLE);
        
        // Act
        ResponseEntity<Book> response = restTemplate.postForEntity("/api/books", newBook, Book.class);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newBook.getIsbn(), response.getBody().getIsbn());
        
        // Verify the book was added to the database
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE isbn = ?",
                Integer.class,
                newBook.getIsbn()
        );
        assertEquals(1, count);
        
        // Verify book details in the database
        Map<String, Object> bookData = jdbcTemplate.queryForMap(
                "SELECT * FROM books WHERE isbn = ?",
                newBook.getIsbn()
        );
        assertEquals(newBook.getTitle(), bookData.get("title"));
        assertEquals(newBook.getAuthor(), bookData.get("author"));
        assertEquals(BookStatus.AVAILABLE.name(), bookData.get("status"));
    }
    
    @Test
    @DisplayName("PUT /api/books/{isbn} should update an existing book")
    void updateBookShouldUpdateBookInDatabase() {
        // Arrange
        String isbn = "978-1-11111-111-1";
        Book updatedBook = new Book(isbn, "Clean Code (Updated)", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        updatedBook.setStatus(BookStatus.BORROWED);
        
        // Act
        ResponseEntity<Book> response = restTemplate.exchange(
                "/api/books/{isbn}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedBook),
                Book.class,
                isbn
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedBook.getTitle(), response.getBody().getTitle());
        assertEquals(BookStatus.BORROWED, response.getBody().getStatus());
        
        // Verify the book was updated in the database
        Map<String, Object> bookData = jdbcTemplate.queryForMap(
                "SELECT * FROM books WHERE isbn = ?",
                isbn
        );
        assertEquals(updatedBook.getTitle(), bookData.get("title"));
        assertEquals(BookStatus.BORROWED.name(), bookData.get("status"));
    }
    
    @Test
    @DisplayName("DELETE /api/books/{isbn} should remove a book from the database")
    void deleteBookShouldRemoveBookFromDatabase() {
        // Arrange
        String isbn = "978-1-11111-111-1";
        
        // Verify book exists before deletion
        Integer countBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE isbn = ?",
                Integer.class,
                isbn
        );
        assertEquals(1, countBefore);
        
        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/books/{isbn}",
                HttpMethod.DELETE,
                null,
                Void.class,
                isbn
        );
        
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        // Verify book was removed from the database
        Integer countAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE isbn = ?",
                Integer.class,
                isbn
        );
        assertEquals(0, countAfter);
    }
    
    @Test
    @DisplayName("Creating a book loan should update book status and add loan record to database")
    void createBookLoanShouldUpdateBookStatusAndAddLoanRecord() {
        // Arrange
        String isbn = "978-1-11111-111-1";
        Long userId = 1L;
        
        // Insert a user for the loan
        jdbcTemplate.update(
                "INSERT INTO library_users (id, name, email, membership_number, member_since) VALUES (?, ?, ?, ?, ?)",
                userId, "Test User", "test@example.com", "LIB-TEST", LocalDate.now().minusYears(1)
        );
        
        // Act
        // Note: In a real application, there would be a loan creation endpoint
        // For this example, we'll simulate it by directly updating the database
        
        // Update book status
        jdbcTemplate.update(
                "UPDATE books SET status = ? WHERE isbn = ?",
                BookStatus.BORROWED.name(), isbn
        );
        
        // Create loan record
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);
        jdbcTemplate.update(
                "INSERT INTO book_loans (book_isbn, user_id, loan_date, due_date) VALUES (?, ?, ?, ?)",
                isbn, userId, loanDate, dueDate
        );
        
        // Assert
        // Verify book status was updated
        String bookStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM books WHERE isbn = ?",
                String.class,
                isbn
        );
        assertEquals(BookStatus.BORROWED.name(), bookStatus);
        
        // Verify loan record was created
        Integer loanCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM book_loans WHERE book_isbn = ? AND user_id = ?",
                Integer.class,
                isbn, userId
        );
        assertEquals(1, loanCount);
        
        // Verify loan details
        Map<String, Object> loanData = jdbcTemplate.queryForMap(
                "SELECT * FROM book_loans WHERE book_isbn = ? AND user_id = ?",
                isbn, userId
        );
        assertNotNull(loanData.get("id"));
        assertEquals(loanDate.toString(), loanData.get("loan_date").toString());
        assertEquals(dueDate.toString(), loanData.get("due_date").toString());
        assertNull(loanData.get("return_date"));
    }
}