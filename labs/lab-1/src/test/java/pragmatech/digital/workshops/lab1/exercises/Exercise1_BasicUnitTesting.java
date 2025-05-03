package pragmatech.digital.workshops.lab1.exercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.domain.BookStatus;

import java.time.LocalDate;

/**
 * Exercise 1: Basic Unit Testing with JUnit 5
 *
 * In this exercise, you will learn basic unit testing techniques using JUnit 5.
 * You'll test the Book class to verify its behavior.
 *
 * Tasks:
 * 1. Implement the missing unit tests for the Book class.
 * 2. Make sure all tests are passing.
 * 3. Aim for high code coverage (at least 90%).
 */
public class Exercise1_BasicUnitTesting {

    // TODO: Implement a test to verify the book constructor works correctly
    
    // TODO: Implement a test to verify that the book constructor throws IllegalArgumentException
    // when invalid parameters are provided
    
    // TODO: Implement a test to verify adding and retrieving book reviews
    
    // TODO: Implement a test to verify the isAvailable method
    
    // EXAMPLE TEST (use this as a guide for implementing the other tests)
    @Test
    @DisplayName("Book status should be updated correctly")
    void bookStatusShouldBeUpdatedCorrectly() {
        // TODO: Create a book with valid parameters
        
        // TODO: Verify initial status is AVAILABLE
        
        // TODO: Update status to BORROWED
        
        // TODO: Verify status was updated correctly
    }
}