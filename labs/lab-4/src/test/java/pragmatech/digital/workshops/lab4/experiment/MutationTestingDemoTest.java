package pragmatech.digital.workshops.lab4.experiment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.entity.BookStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test demonstrates bad tests that won't catch mutations.
 * Mutation testing tools like PIT (Pitest) will create mutations (changes)
 * in your code to check if your tests can detect those changes.
 * 
 * A "bad" test passes even when the code is mutated, indicating that
 * the test isn't properly verifying the behavior of the code.
 */
@DisplayName("Mutation Testing Demo")
class MutationTestingDemoTest {

    /**
     * Vulnerable to mutations because:
     * 1. It doesn't test the behavior of the calculateTotalValue method
     * 2. It doesn't verify the correct calculation
     * 3. It only checks if the result is greater than zero
     */
    @Test
    @DisplayName("Bad test that will pass mutation testing")
    void badTestForCalculateTotalValue() {
        // Arrange
        Book book1 = createBook("123", 10.0);
        Book book2 = createBook("456", 15.0);
        Book book3 = createBook("789", 20.0);
        
        List<Book> books = List.of(book1, book2, book3);
        
        // Act
        double result = calculateTotalValue(books);
        
        // Assert - BAD ASSERTION!
        // This will pass even if the calculation logic is wrong
        assertThat(result).isGreaterThan(0);
    }
    
    /**
     * This is a better test that will catch mutations:
     * 1. It checks the exact value of the calculation
     * 2. It verifies edge cases (empty list)
     * 3. It tests different scenarios
     */
    @Test
    @DisplayName("Good test that will catch mutations")
    void goodTestForCalculateTotalValue() {
        // Arrange
        Book book1 = createBook("123", 10.0);
        Book book2 = createBook("456", 15.0);
        Book book3 = createBook("789", 20.0);
        
        // Act & Assert - Specific value check
        assertThat(calculateTotalValue(List.of(book1, book2, book3))).isEqualTo(45.0);
        
        // Edge case - empty list
        assertThat(calculateTotalValue(new ArrayList<>())).isEqualTo(0.0);
        
        // Single book
        assertThat(calculateTotalValue(List.of(book1))).isEqualTo(10.0);
    }
    
    /**
     * Another bad test for the isDiscountEligible method.
     * Vulnerable because:
     * 1. It doesn't test both branches (true and false cases)
     * 2. It only tests one condition
     */
    @Test
    @DisplayName("Bad test for discount eligibility")
    void badTestForIsDiscountEligible() {
        // Arrange
        Book book = createBook("123", 100.0);
        
        // Act
        boolean result = isDiscountEligible(book);
        
        // Assert - BAD ASSERTION!
        // This only tests one branch of the condition
        assertThat(result).isTrue();
    }
    
    /**
     * A better test for isDiscountEligible that will catch mutations:
     * 1. Tests both true and false branches
     * 2. Tests boundary values
     */
    @Test
    @DisplayName("Good test for discount eligibility")
    void goodTestForIsDiscountEligible() {
        // Arrange & Act & Assert
        
        // Eligible books (price >= 50 AND available)
        Book eligibleBook1 = createBook("123", 50.0); // boundary
        eligibleBook1.setStatus(BookStatus.AVAILABLE);
        assertThat(isDiscountEligible(eligibleBook1)).isTrue();
        
        Book eligibleBook2 = createBook("456", 100.0); // well above boundary
        eligibleBook2.setStatus(BookStatus.AVAILABLE);
        assertThat(isDiscountEligible(eligibleBook2)).isTrue();
        
        // Ineligible books
        
        // Price too low
        Book ineligibleBook1 = createBook("789", 49.99); // just below boundary
        ineligibleBook1.setStatus(BookStatus.AVAILABLE);
        assertThat(isDiscountEligible(ineligibleBook1)).isFalse();
        
        // Not available
        Book ineligibleBook2 = createBook("012", 100.0);
        ineligibleBook2.setStatus(BookStatus.BORROWED);
        assertThat(isDiscountEligible(ineligibleBook2)).isFalse();
        
        // Both conditions failing
        Book ineligibleBook3 = createBook("345", 10.0);
        ineligibleBook3.setStatus(BookStatus.BORROWED);
        assertThat(isDiscountEligible(ineligibleBook3)).isFalse();
    }
    
    /*
     * Sample methods that would be in the real production code.
     * For this demo, they're included in the test class.
     */
    
    /**
     * Calculates the total value of a list of books.
     * This method is vulnerable to mutations:
     * - Changing the + to - would not be caught by the bad test
     * - Changing comparison operators would not be caught
     */
    private double calculateTotalValue(List<Book> books) {
        double total = 0.0;
        
        for (Book book : books) {
            total += book.getPrice();
        }
        
        return total;
    }
    
    /**
     * Determines if a book is eligible for a discount.
     * A book is eligible if:
     * - It costs at least $50
     * - It is currently available
     * 
     * This method is vulnerable to mutations:
     * - Changing >= to > would not be caught by the bad test
     * - Changing AND to OR would not be caught
     */
    private boolean isDiscountEligible(Book book) {
        return book.getPrice() >= 50.0 && book.getStatus() == BookStatus.AVAILABLE;
    }
    
    /**
     * Helper method to create a book with price.
     */
    private Book createBook(String isbn, double price) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublishedDate(LocalDate.now());
        book.setStatus(BookStatus.AVAILABLE);
        book.setPrice(price);
        
        return book;
    }
}