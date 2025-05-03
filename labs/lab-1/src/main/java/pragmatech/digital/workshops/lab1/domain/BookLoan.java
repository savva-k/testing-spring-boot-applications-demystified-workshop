package pragmatech.digital.workshops.lab1.domain;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a loan of a book to a library user.
 */
public class BookLoan {
    private final String id;
    private final Book book;
    private final LibraryUser user;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    
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
        
        this.id = UUID.randomUUID().toString();
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }
    
    public String getId() {
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
        return id.equals(bookLoan.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "BookLoan{" +
                "id='" + id + '\'' +
                ", book=" + book.getTitle() +
                ", user=" + user.getName() +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}