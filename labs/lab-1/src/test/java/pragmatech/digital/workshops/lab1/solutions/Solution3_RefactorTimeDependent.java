package pragmatech.digital.workshops.lab1.solutions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pragmatech.digital.workshops.lab1.util.TimeProvider;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Solution for Exercise 3: Refactoring Time-Dependent Code for Better Testing
 */
@ExtendWith(MockitoExtension.class)
public class Solution3_RefactorTimeDependent {

    /**
     * Refactored version of OverdueChecker that uses TimeProvider.
     */
    public static class RefactoredOverdueChecker {
        
        private static final double DAILY_FINE_RATE = 0.50; // $0.50 per day
        private final TimeProvider timeProvider;
        
        public RefactoredOverdueChecker(TimeProvider timeProvider) {
            if (timeProvider == null) {
                throw new IllegalArgumentException("TimeProvider cannot be null");
            }
            this.timeProvider = timeProvider;
        }
        
        /**
         * Calculate the fine for an overdue book.
         * 
         * @param dueDate the date the book was due
         * @return the fine amount in dollars, or 0 if the book is not overdue
         */
        public double calculateFine(LocalDate dueDate) {
            if (dueDate == null) {
                throw new IllegalArgumentException("Due date cannot be null");
            }
            
            LocalDate today = timeProvider.getCurrentDate(); // Uses injected TimeProvider
            
            if (dueDate.isAfter(today) || dueDate.isEqual(today)) {
                return 0.0; // Not overdue
            }
            
            long daysOverdue = today.toEpochDay() - dueDate.toEpochDay();
            return daysOverdue * DAILY_FINE_RATE;
        }
        
        /**
         * Determine if a book is overdue.
         * 
         * @param dueDate the date the book was due
         * @return true if the book is overdue, false otherwise
         */
        public boolean isOverdue(LocalDate dueDate) {
            if (dueDate == null) {
                throw new IllegalArgumentException("Due date cannot be null");
            }
            
            return dueDate.isBefore(timeProvider.getCurrentDate()); // Uses injected TimeProvider
        }
    }
    
    // Tests for the refactored class
    
    @Mock
    private TimeProvider timeProvider;
    
    private RefactoredOverdueChecker overdueChecker;
    
    @BeforeEach
    void setUp() {
        overdueChecker = new RefactoredOverdueChecker(timeProvider);
    }
    
    @Test
    @DisplayName("Constructor should throw exception if TimeProvider is null")
    void constructorShouldThrowExceptionIfTimeProviderIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new RefactoredOverdueChecker(null));
    }
    
    @Test
    @DisplayName("calculateFine should throw exception if due date is null")
    void calculateFineShouldThrowExceptionIfDueDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> overdueChecker.calculateFine(null));
    }
    
    @Test
    @DisplayName("calculateFine should return 0 if book is not overdue")
    void calculateFineShouldReturnZeroIfBookIsNotOverdue() {
        // Arrange
        LocalDate today = LocalDate.of(2023, 5, 15);
        LocalDate dueDate = today.plusDays(5); // Due in the future
        
        when(timeProvider.getCurrentDate()).thenReturn(today);
        
        // Act
        double fine = overdueChecker.calculateFine(dueDate);
        
        // Assert
        assertEquals(0.0, fine);
        verify(timeProvider).getCurrentDate();
    }
    
    @Test
    @DisplayName("calculateFine should return 0 if book is due today")
    void calculateFineShouldReturnZeroIfBookIsDueToday() {
        // Arrange
        LocalDate today = LocalDate.of(2023, 5, 15);
        
        when(timeProvider.getCurrentDate()).thenReturn(today);
        
        // Act
        double fine = overdueChecker.calculateFine(today);
        
        // Assert
        assertEquals(0.0, fine);
        verify(timeProvider).getCurrentDate();
    }
    
    @Test
    @DisplayName("calculateFine should calculate correct fine for overdue book")
    void calculateFineShouldCalculateCorrectFineForOverdueBook() {
        // Arrange
        LocalDate today = LocalDate.of(2023, 5, 15);
        LocalDate dueDate = today.minusDays(5); // 5 days overdue
        
        when(timeProvider.getCurrentDate()).thenReturn(today);
        
        // Act
        double fine = overdueChecker.calculateFine(dueDate);
        
        // Assert
        assertEquals(5 * 0.5, fine); // 5 days * $0.50
        verify(timeProvider).getCurrentDate();
    }
    
    @Test
    @DisplayName("isOverdue should throw exception if due date is null")
    void isOverdueShouldThrowExceptionIfDueDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> overdueChecker.isOverdue(null));
    }
    
    @Test
    @DisplayName("isOverdue should return true if book is overdue")
    void isOverdueShouldReturnTrueIfBookIsOverdue() {
        // Arrange
        LocalDate today = LocalDate.of(2023, 5, 15);
        LocalDate dueDate = today.minusDays(1); // Due yesterday
        
        when(timeProvider.getCurrentDate()).thenReturn(today);
        
        // Act
        boolean overdue = overdueChecker.isOverdue(dueDate);
        
        // Assert
        assertTrue(overdue);
        verify(timeProvider).getCurrentDate();
    }
    
    @Test
    @DisplayName("isOverdue should return false if book is due today")
    void isOverdueShouldReturnFalseIfBookIsDueToday() {
        // Arrange
        LocalDate today = LocalDate.of(2023, 5, 15);
        
        when(timeProvider.getCurrentDate()).thenReturn(today);
        
        // Act
        boolean overdue = overdueChecker.isOverdue(today);
        
        // Assert
        assertFalse(overdue);
        verify(timeProvider).getCurrentDate();
    }
    
    @Test
    @DisplayName("isOverdue should return false if book is due in the future")
    void isOverdueShouldReturnFalseIfBookIsDueInFuture() {
        // Arrange
        LocalDate today = LocalDate.of(2023, 5, 15);
        LocalDate dueDate = today.plusDays(1); // Due tomorrow
        
        when(timeProvider.getCurrentDate()).thenReturn(today);
        
        // Act
        boolean overdue = overdueChecker.isOverdue(dueDate);
        
        // Assert
        assertFalse(overdue);
        verify(timeProvider).getCurrentDate();
    }
}