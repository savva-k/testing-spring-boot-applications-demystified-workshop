package pragmatech.digital.workshops.lab3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a book in the library system.
 */
@Entity
@Table(name = "books")
public class Book {
  
  @Id
  private String isbn;
  
  @Column(nullable = false)
  private String title;
  
  @Column(nullable = false)
  private String author;
  
  @Column(name = "published_date", nullable = false)
  private LocalDate publishedDate;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookStatus status = BookStatus.AVAILABLE;
  
  @OneToMany(mappedBy = "book")
  private List<BookLoan> loans = new ArrayList<>();
  
  @OneToMany(mappedBy = "book")
  private List<BookReview> reviews = new ArrayList<>();
  
  // Default constructor required by JPA
  public Book() {
  }
  
  // Constructor with required fields
  public Book(String isbn, String title, String author, LocalDate publishedDate) {
    this.isbn = isbn;
    this.title = title;
    this.author = author;
    this.publishedDate = publishedDate;
  }
  
  // Getters and setters
  public String getIsbn() {
    return isbn;
  }
  
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getAuthor() {
    return author;
  }
  
  public void setAuthor(String author) {
    this.author = author;
  }
  
  public LocalDate getPublishedDate() {
    return publishedDate;
  }
  
  public void setPublishedDate(LocalDate publishedDate) {
    this.publishedDate = publishedDate;
  }
  
  public BookStatus getStatus() {
    return status;
  }
  
  public void setStatus(BookStatus status) {
    this.status = status;
  }
  
  public List<BookLoan> getLoans() {
    return loans;
  }
  
  public void setLoans(List<BookLoan> loans) {
    this.loans = loans;
  }
  
  public List<BookReview> getReviews() {
    return reviews;
  }
  
  public void setReviews(List<BookReview> reviews) {
    this.reviews = reviews;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    Book book = (Book) o;
    return isbn.equals(book.isbn);
  }
  
  @Override
  public int hashCode() {
    return isbn.hashCode();
  }
}