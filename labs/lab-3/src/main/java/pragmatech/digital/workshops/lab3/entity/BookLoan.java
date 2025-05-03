package pragmatech.digital.workshops.lab3.entity;

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
 * Entity representing a book loan record.
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
  
  // Default constructor required by JPA
  public BookLoan() {
  }
  
  // Constructor with required fields
  public BookLoan(Book book, LibraryUser user, LocalDate loanDate, LocalDate dueDate) {
    this.book = book;
    this.user = user;
    this.loanDate = loanDate;
    this.dueDate = dueDate;
  }
  
  // Getters and setters
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Book getBook() {
    return book;
  }
  
  public void setBook(Book book) {
    this.book = book;
  }
  
  public LibraryUser getUser() {
    return user;
  }
  
  public void setUser(LibraryUser user) {
    this.user = user;
  }
  
  public LocalDate getLoanDate() {
    return loanDate;
  }
  
  public void setLoanDate(LocalDate loanDate) {
    this.loanDate = loanDate;
  }
  
  public LocalDate getDueDate() {
    return dueDate;
  }
  
  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }
  
  public LocalDate getReturnDate() {
    return returnDate;
  }
  
  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }
  
  // Helper method to check if the loan is currently active
  public boolean isActive() {
    return returnDate == null;
  }
  
  // Helper method to check if the loan is overdue
  public boolean isOverdue() {
    return isActive() && LocalDate.now().isAfter(dueDate);
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
}