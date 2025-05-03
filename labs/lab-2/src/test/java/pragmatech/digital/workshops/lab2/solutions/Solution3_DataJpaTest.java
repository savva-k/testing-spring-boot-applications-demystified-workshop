package pragmatech.digital.workshops.lab2.solutions;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookReview;
import pragmatech.digital.workshops.lab2.entity.BookStatus;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Solution for Exercise 3: Testing Spring Data JPA Repositories
 */
@DataJpaTest
public class Solution3_DataJpaTest {

    @Autowired
    private TestEntityManager testEntityManager;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    // Test data
    private Book cleanCode;
    private Book effectiveJava;
    private Book pragmaticProgrammer;
    private Book domainDrivenDesign;
    
    @BeforeEach
    void setUp() {
        // Create test books
        cleanCode = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        effectiveJava = new Book("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24));
        pragmaticProgrammer = new Book("978-3-33333-333-3", "The Pragmatic Programmer", "Andrew Hunt", LocalDate.of(1999, 10, 20));
        domainDrivenDesign = new Book("978-4-44444-444-4", "Domain-Driven Design", "Eric Evans", LocalDate.of(2003, 8, 30));
        
        // Set some statuses
        effectiveJava.setStatus(BookStatus.BORROWED);
        domainDrivenDesign.setStatus(BookStatus.RESERVED);
        
        // Persist books
        testEntityManager.persist(cleanCode);
        testEntityManager.persist(effectiveJava);
        testEntityManager.persist(pragmaticProgrammer);
        testEntityManager.persist(domainDrivenDesign);
        
        // Create and persist some reviews
        BookReview review1 = new BookReview(cleanCode, "John Doe", 5, "Excellent book!", LocalDateTime.now().minusDays(10));
        BookReview review2 = new BookReview(cleanCode, "Jane Smith", 4, "Very good book", LocalDateTime.now().minusDays(5));
        BookReview review3 = new BookReview(effectiveJava, "Bob Johnson", 5, "Must-read for Java developers", LocalDateTime.now().minusDays(15));
        BookReview review4 = new BookReview(pragmaticProgrammer, "Alice Williams", 3, "Good but outdated", LocalDateTime.now().minusDays(20));
        
        testEntityManager.persist(review1);
        testEntityManager.persist(review2);
        testEntityManager.persist(review3);
        testEntityManager.persist(review4);
        
        // Flush to ensure all entities are saved to the database
        testEntityManager.flush();
    }
    
    @Test
    @DisplayName("findById should return book when found")
    void findByIdShouldReturnBookWhenFound() {
        // Act
        Optional<Book> foundBook = bookRepository.findById(cleanCode.getIsbn());
        
        // Assert
        assertTrue(foundBook.isPresent());
        assertEquals(cleanCode.getIsbn(), foundBook.get().getIsbn());
        assertEquals(cleanCode.getTitle(), foundBook.get().getTitle());
    }
    
    @Test
    @DisplayName("findByAuthorContainingIgnoreCase should return books by matching author")
    void findByAuthorContainingIgnoreCaseShouldReturnBooksByMatchingAuthor() {
        // Act
        List<Book> martinBooks = bookRepository.findByAuthorContainingIgnoreCase("Martin");
        List<Book> blochBooks = bookRepository.findByAuthorContainingIgnoreCase("bloch");  // Testing case insensitivity
        
        // Assert
        assertEquals(1, martinBooks.size());
        assertEquals(cleanCode.getIsbn(), martinBooks.get(0).getIsbn());
        
        assertEquals(1, blochBooks.size());
        assertEquals(effectiveJava.getIsbn(), blochBooks.get(0).getIsbn());
    }
    
    @Test
    @DisplayName("findByTitleContainingIgnoreCase should return books by matching title")
    void findByTitleContainingIgnoreCaseShouldReturnBooksByMatchingTitle() {
        // Act
        List<Book> codeBooks = bookRepository.findByTitleContainingIgnoreCase("Code");
        List<Book> javaBooks = bookRepository.findByTitleContainingIgnoreCase("java");  // Testing case insensitivity
        
        // Assert
        assertEquals(1, codeBooks.size());
        assertEquals(cleanCode.getIsbn(), codeBooks.get(0).getIsbn());
        
        assertEquals(1, javaBooks.size());
        assertEquals(effectiveJava.getIsbn(), javaBooks.get(0).getIsbn());
    }
    
    @Test
    @DisplayName("findByStatus should return books with matching status")
    void findByStatusShouldReturnBooksWithMatchingStatus() {
        // Act
        List<Book> availableBooks = bookRepository.findByStatus(BookStatus.AVAILABLE);
        List<Book> borrowedBooks = bookRepository.findByStatus(BookStatus.BORROWED);
        List<Book> reservedBooks = bookRepository.findByStatus(BookStatus.RESERVED);
        
        // Assert
        assertEquals(2, availableBooks.size());
        assertThat(availableBooks).extracting("isbn").containsExactlyInAnyOrder(
                cleanCode.getIsbn(), pragmaticProgrammer.getIsbn());
        
        assertEquals(1, borrowedBooks.size());
        assertEquals(effectiveJava.getIsbn(), borrowedBooks.get(0).getIsbn());
        
        assertEquals(1, reservedBooks.size());
        assertEquals(domainDrivenDesign.getIsbn(), reservedBooks.get(0).getIsbn());
    }
    
    @Test
    @DisplayName("findByPublishedDateAfter should return books published after a date")
    void findByPublishedDateAfterShouldReturnBooksPublishedAfterDate() {
        // Act
        LocalDate cutoffDate = LocalDate.of(2010, 1, 1);
        List<Book> recentBooks = bookRepository.findByPublishedDateAfter(cutoffDate);
        
        // Assert
        assertEquals(1, recentBooks.size());
        assertEquals(effectiveJava.getIsbn(), recentBooks.get(0).getIsbn());
    }
    
    @Test
    @DisplayName("findBooksWithHighRatings should return books with average rating >= 4")
    void findBooksWithHighRatingsShouldReturnBooksWithHighRatings() {
        // Act
        List<Book> highRatedBooks = bookRepository.findBooksWithHighRatings();
        
        // Assert
        assertEquals(2, highRatedBooks.size());
        assertThat(highRatedBooks).extracting("isbn").containsExactlyInAnyOrder(
                cleanCode.getIsbn(), effectiveJava.getIsbn());
    }
    
    @Test
    @DisplayName("findByIsbnOrTitle should return books matching ISBN or title")
    void findByIsbnOrTitleShouldReturnBooksMatchingIsbnOrTitle() {
        // Act
        List<Book> books1 = bookRepository.findByIsbnOrTitle("111");  // Partial ISBN match
        List<Book> books2 = bookRepository.findByIsbnOrTitle("Clean");  // Title match
        
        // Assert
        assertEquals(1, books1.size());
        assertEquals(cleanCode.getIsbn(), books1.get(0).getIsbn());
        
        assertEquals(1, books2.size());
        assertEquals(cleanCode.getIsbn(), books2.get(0).getIsbn());
    }
    
    @Test
    @DisplayName("findByAuthorOrderByPublishedDateDesc should return books ordered by date")
    void findByAuthorOrderByPublishedDateDescShouldReturnBooksOrderedByDate() {
        // Add another book by the same author but with a different date
        Book cleanArchitecture = new Book("978-5-55555-555-5", "Clean Architecture", "Robert C. Martin", LocalDate.of(2017, 9, 10));
        testEntityManager.persist(cleanArchitecture);
        testEntityManager.flush();
        
        // Act
        List<Book> martinBooks = bookRepository.findByAuthorOrderByPublishedDateDesc("Martin");
        
        // Assert
        assertEquals(2, martinBooks.size());
        assertEquals(cleanArchitecture.getIsbn(), martinBooks.get(0).getIsbn());  // Most recent first
        assertEquals(cleanCode.getIsbn(), martinBooks.get(1).getIsbn());  // Older second
    }
    
    @Test
    @DisplayName("Demonstrate when SQL is fired using transaction boundaries")
    void demonstrateWhenSqlIsFired() {
        // Create a new book
        Book newBook = new Book("978-6-66666-666-6", "Test Driven Development", "Kent Beck", LocalDate.of(2002, 11, 18));
        
        // This save operation doesn't immediately fire an SQL INSERT
        Book savedBook = bookRepository.save(newBook);
        
        // The book exists in the persistence context, but not necessarily in the database yet
        assertNotNull(savedBook.getIsbn());
        
        // Force a flush to execute pending SQL statements
        entityManager.flush();
        
        // Clear the persistence context to force a database read
        entityManager.clear();
        
        // This will force a database read since the entity is no longer in the persistence context
        Optional<Book> foundBook = bookRepository.findById(newBook.getIsbn());
        
        // Verify the book was actually persisted to the database
        assertTrue(foundBook.isPresent());
        assertEquals(newBook.getIsbn(), foundBook.get().getIsbn());
        assertEquals(newBook.getTitle(), foundBook.get().getTitle());
    }
    
    @Test
    @DisplayName("Demonstrate lazy loading in repositories")
    void demonstrateLazyLoading() {
        // Get a book with reviews
        Book book = testEntityManager.find(Book.class, cleanCode.getIsbn());
        
        // The reviews should be lazily loaded
        assertFalse(isInitialized(book.getReviews()));
        
        // Access the reviews, which will trigger the lazy loading
        int reviewCount = book.getReviews().size();
        
        // Now the reviews should be initialized
        assertTrue(isInitialized(book.getReviews()));
        assertEquals(2, reviewCount);  // We added 2 reviews for Clean Code in setup
    }
    
    // Helper method to check if a collection is initialized
    private boolean isInitialized(Object o) {
        return !org.hibernate.Hibernate.isInitialized(o);
    }
}