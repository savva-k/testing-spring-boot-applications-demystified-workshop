package pragmatech.digital.workshops.lab1.service;

import org.springframework.stereotype.Service;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.domain.BookLoan;
import pragmatech.digital.workshops.lab1.domain.BookStatus;
import pragmatech.digital.workshops.lab1.domain.LibraryUser;
import pragmatech.digital.workshops.lab1.util.TimeProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing book loans.
 */
@Service
public class LoanService {
    
    private static final int MAX_LOANS_PER_USER = 5;
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    
    private final Map<String, BookLoan> loans = new HashMap<>();
    private final BookService bookService;
    private final UserService userService;
    private final TimeProvider timeProvider;
    
    public LoanService(BookService bookService, UserService userService, TimeProvider timeProvider) {
        this.bookService = bookService;
        this.userService = userService;
        this.timeProvider = timeProvider;
    }
    
    /**
     * Borrow a book for a user.
     * 
     * @param isbn the ISBN of the book to borrow
     * @param userId the ID of the user borrowing the book
     * @return the created book loan
     * @throws IllegalArgumentException if the book is not available or the user has reached the maximum number of loans
     */
    public BookLoan borrowBook(String isbn, String userId) {
        Book book = bookService.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book with ISBN " + isbn + " not found"));
        
        LibraryUser user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        if (!book.isAvailable()) {
            throw new IllegalArgumentException("Book is not available for borrowing");
        }
        
        if (user.getNumberOfCurrentLoans() >= MAX_LOANS_PER_USER) {
            throw new IllegalArgumentException("User has reached the maximum number of loans");
        }
        
        LocalDate loanDate = timeProvider.getCurrentDate();
        LocalDate dueDate = loanDate.plusDays(DEFAULT_LOAN_PERIOD_DAYS);
        
        BookLoan loan = new BookLoan(book, user, loanDate, dueDate);
        loans.put(loan.getId(), loan);
        user.addBookLoan(loan);
        bookService.updateBookStatus(isbn, BookStatus.BORROWED);
        
        return loan;
    }
    
    /**
     * Return a borrowed book.
     * 
     * @param loanId the ID of the loan to return
     * @return the updated book loan
     * @throws IllegalArgumentException if the loan is not found or already returned
     */
    public BookLoan returnBook(String loanId) {
        BookLoan loan = loans.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan with ID " + loanId + " not found");
        }
        
        if (loan.isReturned()) {
            throw new IllegalArgumentException("Book has already been returned");
        }
        
        loan.setReturnDate(timeProvider.getCurrentDate());
        bookService.updateBookStatus(loan.getBook().getIsbn(), BookStatus.AVAILABLE);
        return loan;
    }
    
    /**
     * Extend the due date of a loan.
     * 
     * @param loanId the ID of the loan to extend
     * @param days the number of days to extend the loan
     * @return the updated book loan
     * @throws IllegalArgumentException if the loan is not found, already returned, or overdue
     */
    public BookLoan extendLoan(String loanId, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Extension period must be positive");
        }
        
        BookLoan loan = loans.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan with ID " + loanId + " not found");
        }
        
        if (loan.isReturned()) {
            throw new IllegalArgumentException("Cannot extend a returned loan");
        }
        
        LocalDate currentDate = timeProvider.getCurrentDate();
        if (loan.isOverdue(currentDate)) {
            throw new IllegalArgumentException("Cannot extend an overdue loan");
        }
        
        // Create a new loan with the updated due date
        BookLoan extendedLoan = new BookLoan(
                loan.getBook(),
                loan.getUser(),
                loan.getLoanDate(),
                loan.getDueDate().plusDays(days)
        );
        
        // Replace the old loan
        loans.put(loanId, extendedLoan);
        loan.getUser().removeBookLoan(loan);
        loan.getUser().addBookLoan(extendedLoan);
        
        return extendedLoan;
    }
    
    /**
     * Find all loans for a user.
     * 
     * @param userId the ID of the user
     * @return a list of the user's loans
     */
    public List<BookLoan> findLoansByUser(String userId) {
        return loans.values().stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
    
    /**
     * Find all current (not returned) loans for a user.
     * 
     * @param userId the ID of the user
     * @return a list of the user's current loans
     */
    public List<BookLoan> findCurrentLoansByUser(String userId) {
        return loans.values().stream()
                .filter(loan -> loan.getUser().getId().equals(userId) && !loan.isReturned())
                .collect(Collectors.toList());
    }
    
    /**
     * Find all overdue loans.
     * 
     * @return a list of overdue loans
     */
    public List<BookLoan> findOverdueLoans() {
        LocalDate currentDate = timeProvider.getCurrentDate();
        return loans.values().stream()
                .filter(loan -> loan.isOverdue(currentDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Find all loans.
     * 
     * @return a list of all loans
     */
    public List<BookLoan> findAllLoans() {
        return new ArrayList<>(loans.values());
    }
}