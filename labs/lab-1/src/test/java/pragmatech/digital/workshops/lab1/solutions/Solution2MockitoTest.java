package pragmatech.digital.workshops.lab1.solutions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.domain.BookLoan;
import pragmatech.digital.workshops.lab1.domain.BookStatus;
import pragmatech.digital.workshops.lab1.domain.LibraryUser;
import pragmatech.digital.workshops.lab1.service.BookService;
import pragmatech.digital.workshops.lab1.service.LoanService;
import pragmatech.digital.workshops.lab1.service.UserService;
import pragmatech.digital.workshops.lab1.util.TimeProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Solution for Exercise 2: Basic Mockito Usage
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class Solution2MockitoTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private LoanService loanService;

    // Test data
    private Book testBook;
    private LibraryUser testUser;
    private LocalDate currentDate;
    private LocalDate dueDate;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testBook = new Book("978-3-16-148410-0", "Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1));
        testUser = new LibraryUser("user1", "John Doe", "john@example.com", "LIB-1234",
                LocalDate.of(2023, 1, 1));
        currentDate = LocalDate.of(2023, 5, 15);
        dueDate = currentDate.plusDays(14);

        // Set up TimeProvider mock to return our test date
        when(timeProvider.getCurrentDate()).thenReturn(currentDate);
    }

    @Test
    @DisplayName("borrowBook should create a loan when book and user are valid")
    void borrowBookShouldCreateLoanWhenBookAndUserAreValid() {
        // Arrange
        String isbn = testBook.getIsbn();
        String userId = testUser.getId();

        when(bookService.findByIsbn(isbn)).thenReturn(Optional.of(testBook));
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        BookLoan loan = loanService.borrowBook(isbn, userId);

        // Assert
        assertNotNull(loan);
        assertEquals(testBook, loan.getBook());
        assertEquals(testUser, loan.getUser());
        assertEquals(currentDate, loan.getLoanDate());
        assertEquals(dueDate, loan.getDueDate());
        assertNull(loan.getReturnDate());

        // Verify interactions
        verify(bookService).findByIsbn(isbn);
        verify(userService).findById(userId);
        verify(bookService).updateBookStatus(isbn, BookStatus.BORROWED);
        verify(timeProvider).getCurrentDate();
    }

    @Test
    @DisplayName("borrowBook should throw exception when book is not found")
    void borrowBookShouldThrowExceptionWhenBookNotFound() {
        // Arrange
        String isbn = "non-existent-isbn";
        String userId = testUser.getId();

        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> loanService.borrowBook(isbn, userId));

        assertTrue(exception.getMessage().contains("not found"));

        // Verify interactions
        verify(bookService).findByIsbn(isbn);
        verify(userService, never()).findById(anyString());
        verify(bookService, never()).updateBookStatus(anyString(), any(BookStatus.class));
    }

    @Test
    @DisplayName("borrowBook should throw exception when user is not found")
    void borrowBookShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String isbn = testBook.getIsbn();
        String userId = "non-existent-user";

        when(bookService.findByIsbn(isbn)).thenReturn(Optional.of(testBook));
        when(userService.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> loanService.borrowBook(isbn, userId));

        assertTrue(exception.getMessage().contains("not found"));

        // Verify interactions
        verify(bookService).findByIsbn(isbn);
        verify(userService).findById(userId);
        verify(bookService, never()).updateBookStatus(anyString(), any(BookStatus.class));
    }

    @Test
    @DisplayName("borrowBook should throw exception when book is not available")
    void borrowBookShouldThrowExceptionWhenBookNotAvailable() {
        // Arrange
        String isbn = testBook.getIsbn();
        String userId = testUser.getId();

        testBook.setStatus(BookStatus.BORROWED); // Make book unavailable

        when(bookService.findByIsbn(isbn)).thenReturn(Optional.of(testBook));
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> loanService.borrowBook(isbn, userId));

        assertTrue(exception.getMessage().contains("not available"));

        // Verify interactions
        verify(bookService).findByIsbn(isbn);
        verify(userService).findById(userId);
        verify(bookService, never()).updateBookStatus(anyString(), any(BookStatus.class));
    }

    @Test
    @DisplayName("returnBook should update loan and book status")
    void returnBookShouldUpdateLoanAndBookStatus() {
        // Arrange
        String loanId = "loan1";
        BookLoan loan = new BookLoan(testBook, testUser, currentDate.minusDays(7), dueDate);

        // Use reflection to set the id field of the loan (normally set in the constructor)
        try {
            java.lang.reflect.Field idField = BookLoan.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(loan, loanId);
        } catch (Exception e) {
            fail("Failed to set loan ID using reflection: " + e.getMessage());
        }

        // Set up the mock to return our test loan
        // Note: This requires adding a method to expose the loans map in LoanService
        // or modifying the implementation to facilitate testing

        // For this solution, we'll use reflection to add the loan to the loans map
        try {
            java.lang.reflect.Field loansField = LoanService.class.getDeclaredField("loans");
            loansField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, BookLoan> loans = (java.util.Map<String, BookLoan>) loansField.get(loanService);
            loans.put(loanId, loan);
        } catch (Exception e) {
            fail("Failed to add loan to loans map using reflection: " + e.getMessage());
        }

        // Act
        BookLoan returnedLoan = loanService.returnBook(loanId);

        // Assert
        assertNotNull(returnedLoan);
        assertEquals(currentDate, returnedLoan.getReturnDate());

        // Verify interactions
        verify(bookService).updateBookStatus(testBook.getIsbn(), BookStatus.AVAILABLE);
        verify(timeProvider).getCurrentDate();
    }

    @Test
    @DisplayName("findOverdueLoans should return loans past their due date")
    void findOverdueLoansReturnsLoansPastTheirDueDate() {
        // Arrange
        // Create a loan that is overdue
        Book book1 = new Book("isbn1", "Title 1", "Author 1", LocalDate.of(2020, 1, 1));
        LibraryUser user1 = new LibraryUser("user1", "User 1", "user1@example.com", "LIB-1", LocalDate.of(2023, 1, 1));
        BookLoan overdueLoan = new BookLoan(book1, user1, currentDate.minusDays(30), currentDate.minusDays(15));

        // Create a loan that is not overdue
        Book book2 = new Book("isbn2", "Title 2", "Author 2", LocalDate.of(2020, 1, 1));
        LibraryUser user2 = new LibraryUser("user2", "User 2", "user2@example.com", "LIB-2", LocalDate.of(2023, 1, 1));
        BookLoan currentLoan = new BookLoan(book2, user2, currentDate.minusDays(5), currentDate.plusDays(10));

        // Create a loan that has been returned
        Book book3 = new Book("isbn3", "Title 3", "Author 3", LocalDate.of(2020, 1, 1));
        LibraryUser user3 = new LibraryUser("user3", "User 3", "user3@example.com", "LIB-3", LocalDate.of(2023, 1, 1));
        BookLoan returnedLoan = new BookLoan(book3, user3, currentDate.minusDays(30), currentDate.minusDays(15));
        returnedLoan.setReturnDate(currentDate.minusDays(20));

        // Set up the loans in the service using reflection
        try {
            java.lang.reflect.Field loansField = LoanService.class.getDeclaredField("loans");
            loansField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, BookLoan> loans = (java.util.Map<String, BookLoan>) loansField.get(loanService);
            loans.put("loan1", overdueLoan);
            loans.put("loan2", currentLoan);
            loans.put("loan3", returnedLoan);
        } catch (Exception e) {
            fail("Failed to set up loans using reflection: " + e.getMessage());
        }

        // Act
        List<BookLoan> overdueLoans = loanService.findOverdueLoans();

        // Assert
        assertEquals(1, overdueLoans.size());
        assertTrue(overdueLoans.contains(overdueLoan));
        assertFalse(overdueLoans.contains(currentLoan));
        assertFalse(overdueLoans.contains(returnedLoan));

        // Verify interactions
        verify(timeProvider).getCurrentDate();
    }
}
