package pragmatech.digital.workshops.lab1.solutions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.domain.BookReview;
import pragmatech.digital.workshops.lab1.domain.BookStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Solution for Exercise 1: Basic Unit Testing with JUnit 5
 */
public class Solution1JUnitTest {

    @Test
    @DisplayName("Book constructor should create a book with correct properties")
    void bookConstructorShouldCreateBookWithCorrectProperties() {
        // Arrange & Act
        String isbn = "978-3-16-148410-0";
        String title = "Clean Code";
        String author = "Robert C. Martin";
        LocalDate publishedDate = LocalDate.of(2008, 8, 1);

        Book book = new Book(isbn, title, author, publishedDate);

        // Assert
        assertEquals(isbn, book.getIsbn());
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(publishedDate, book.getPublishedDate());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertTrue(book.getReviews().isEmpty());
    }

    @Test
    @DisplayName("Book constructor should throw exception for null or blank ISBN")
    void bookConstructorShouldThrowExceptionForInvalidIsbn() {
        // Arrange
        String title = "Clean Code";
        String author = "Robert C. Martin";
        LocalDate publishedDate = LocalDate.of(2008, 8, 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Book(null, title, author, publishedDate));

        assertThrows(IllegalArgumentException.class, () ->
                new Book("", title, author, publishedDate));

        assertThrows(IllegalArgumentException.class, () ->
                new Book("   ", title, author, publishedDate));
    }

    @Test
    @DisplayName("Book constructor should throw exception for null or blank title")
    void bookConstructorShouldThrowExceptionForInvalidTitle() {
        // Arrange
        String isbn = "978-3-16-148410-0";
        String author = "Robert C. Martin";
        LocalDate publishedDate = LocalDate.of(2008, 8, 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, null, author, publishedDate));

        assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, "", author, publishedDate));

        assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, "   ", author, publishedDate));
    }

    @Test
    @DisplayName("Book constructor should throw exception for null or blank author")
    void bookConstructorShouldThrowExceptionForInvalidAuthor() {
        // Arrange
        String isbn = "978-3-16-148410-0";
        String title = "Clean Code";
        LocalDate publishedDate = LocalDate.of(2008, 8, 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, title, null, publishedDate));

        assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, title, "", publishedDate));

        assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, title, "   ", publishedDate));
    }

    @Test
    @DisplayName("Book constructor should throw exception for null published date")
    void bookConstructorShouldThrowExceptionForNullPublishedDate() {
        // Arrange
        String isbn = "978-3-16-148410-0";
        String title = "Clean Code";
        String author = "Robert C. Martin";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Book(isbn, title, author, null));
    }

    @Test
    @DisplayName("Adding a review should store it in the book's reviews")
    void addingReviewShouldStoreItInBookReviews() {
        // Arrange
        Book book = new Book("978-3-16-148410-0", "Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1));

        BookReview review = new BookReview("123", "John Doe", 5, "Excellent book!",
                LocalDateTime.of(2023, 5, 15, 10, 30));

        // Act
        book.addReview(review);

        // Assert
        assertEquals(1, book.getReviews().size());
        assertTrue(book.getReviews().contains(review));
    }

    @Test
    @DisplayName("Adding null review should throw IllegalArgumentException")
    void addingNullReviewShouldThrowException() {
        // Arrange
        Book book = new Book("978-3-16-148410-0", "Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> book.addReview(null));
    }

    @Test
    @DisplayName("isAvailable should return true when status is AVAILABLE")
    void isAvailableShouldReturnTrueWhenStatusIsAvailable() {
        // Arrange
        Book book = new Book("978-3-16-148410-0", "Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1));

        // Act & Assert
        assertTrue(book.isAvailable());

        // Change status to something else
        book.setStatus(BookStatus.BORROWED);
        assertFalse(book.isAvailable());

        // Change back to AVAILABLE
        book.setStatus(BookStatus.AVAILABLE);
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("Book status should be updated correctly")
    void bookStatusShouldBeUpdatedCorrectly() {
        // Arrange
        Book book = new Book("978-3-16-148410-0", "Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1));

        // Assert initial status
        assertEquals(BookStatus.AVAILABLE, book.getStatus());

        // Act
        book.setStatus(BookStatus.BORROWED);

        // Assert updated status
        assertEquals(BookStatus.BORROWED, book.getStatus());
    }

    @Test
    @DisplayName("Setting null status should throw IllegalArgumentException")
    void settingNullStatusShouldThrowException() {
        // Arrange
        Book book = new Book("978-3-16-148410-0", "Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> book.setStatus(null));
    }
}
