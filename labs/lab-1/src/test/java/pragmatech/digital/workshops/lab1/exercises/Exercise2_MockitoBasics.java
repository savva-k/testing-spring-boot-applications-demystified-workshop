package pragmatech.digital.workshops.lab1.exercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pragmatech.digital.workshops.lab1.service.BookService;
import pragmatech.digital.workshops.lab1.service.LoanService;
import pragmatech.digital.workshops.lab1.service.UserService;
import pragmatech.digital.workshops.lab1.util.TimeProvider;

/**
 * Exercise 2: Basic Mockito Usage
 *
 * In this exercise, you will learn how to use Mockito to mock dependencies in your tests.
 * You'll test the LoanService which has dependencies on BookService, UserService, and TimeProvider.
 *
 * Tasks:
 * 1. Set up the test class with proper annotations for using Mockito.
 * 2. Create mock objects for BookService, UserService, and TimeProvider.
 * 3. Implement tests for the LoanService's methods, using mocks for dependencies.
 * 4. Use verify() to ensure that the mocks were called as expected.
 */
@ExtendWith(MockitoExtension.class)  // You'll need this annotation for JUnit 5 with Mockito
public class Exercise2_MockitoBasics {

    // TODO: Add necessary fields for mocks and class under test
    @Mock
    private BookService bookService; // This is how you create a mock
    
    @Mock
    private UserService userService;
    
    @Mock
    private TimeProvider timeProvider;
    
    @InjectMocks
    private LoanService loanService; // This will inject the mocks above
    
    // Setup is automatically handled by MockitoExtension and @Mock/@InjectMocks annotations
    
    @Test
    @DisplayName("Test borrowBook creates loan when conditions are met")
    void testBorrowBook() {
        // TODO: Implement a test for borrowBook method in LoanService
        // The test should verify:
        // - Book is found by ISBN
        // - User is found by ID
        // - Book is available
        // - User has not reached maximum loans
        // - A new loan is created
        // - Book status is updated
    }
    
    @Test
    @DisplayName("Test returnBook marks loan as returned")
    void testReturnBook() {
        // TODO: Implement a test for returnBook method in LoanService
        // The test should verify:
        // - Loan is found by ID
        // - Loan is not already returned
        // - Return date is set
        // - Book status is updated
    }
    
    @Test
    @DisplayName("Test findOverdueLoans returns loans that are past due date")
    void testFindOverdueLoans() {
        // TODO: Implement a test for findOverdueLoans method in LoanService
        // The test should verify:
        // - Current date is retrieved from TimeProvider
        // - Loans that are overdue are returned
    }
}