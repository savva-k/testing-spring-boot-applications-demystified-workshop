package pragmatech.digital.workshops.lab1.exercises;

import java.time.LocalDate;

/**
 * Exercise 3: Refactoring Time-Dependent Code for Better Testing
 *
 * In this exercise, you'll refactor code that directly uses LocalDate.now() to use a TimeProvider,
 * making it easier to test.
 *
 * Below is a poorly designed service class that is difficult to test because
 * it directly uses LocalDate.now() to get the current date.
 *
 * Tasks:
 * 1. Create a new version of this class that uses the TimeProvider interface.
 * 2. Write tests for the refactored class that verify its behavior.
 * 3. Demonstrate how the refactored code is more testable by writing a test
 *    that simulates a different current date.
 */
public class Exercise3_RefactorTimeDependent {

    /**
     * Poorly designed class that directly uses LocalDate.now(), making it hard to test.
     */
    public static class OverdueChecker {
        
        private static final double DAILY_FINE_RATE = 0.50; // $0.50 per day
        
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
            
            LocalDate today = LocalDate.now(); // This makes testing difficult!
            
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
            
            return dueDate.isBefore(LocalDate.now()); // This makes testing difficult!
        }
    }
    
    // TODO: Implement a refactored version of the OverdueChecker class that uses TimeProvider
    
    // TODO: Write tests for the refactored class
}