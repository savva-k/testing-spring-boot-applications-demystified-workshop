package pragmatech.digital.workshops.lab2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Entity representing a loan of a book to a user.
 */
@Entity
@Table(name = "book_loans")
public class BookLoan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private LibraryUser user;
    
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    // Default constructor for JPA
    protected BookLoan() {
    }
    
    public BookLoan(Book book, LibraryUser user, LocalDate loanDate, LocalDate dueDate) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (loanDate == null) {
            throw new IllegalArgumentException("Loan date cannot be null");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        if (dueDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("Due date cannot be before loan date");
        }
        
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }
    
    public Long getId() {
        return id;
    }
    
    public Book getBook() {
        return book;
    }
    
    public LibraryUser getUser() {
        return user;
    }
    
    public LocalDate getLoanDate() {
        return loanDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public boolean isReturned() {
        return returnDate != null;
    }
    
    public boolean isOverdue(LocalDate currentDate) {
        return !isReturned() && currentDate.isAfter(dueDate);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        BookLoan bookLoan = (BookLoan) o;
        return id != null && id.equals(bookLoan.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "BookLoan{" +
                "id=" + id +
                ", book=" + (book != null ? book.getIsbn() : "null") +
                ", user=" + (user != null ? user.getName() : "null") +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}