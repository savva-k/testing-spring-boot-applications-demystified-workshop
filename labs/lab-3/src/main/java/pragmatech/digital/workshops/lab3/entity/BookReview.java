package pragmatech.digital.workshops.lab3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entity representing a book review.
 */
@Entity
@Table(name = "book_reviews")
public class BookReview {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "book_isbn", nullable = false)
  private Book book;
  
  @Column(name = "reviewer_name", nullable = false)
  private String reviewerName;
  
  @Column(nullable = false)
  private Integer rating;
  
  @Column
  private String comment;
  
  @Column(name = "review_date", nullable = false)
  private LocalDateTime reviewDate;
  
  // Default constructor required by JPA
  public BookReview() {
  }
  
  // Constructor with required fields
  public BookReview(Book book, String reviewerName, Integer rating, LocalDateTime reviewDate) {
    this.book = book;
    this.reviewerName = reviewerName;
    this.rating = rating;
    this.reviewDate = reviewDate;
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
  
  public String getReviewerName() {
    return reviewerName;
  }
  
  public void setReviewerName(String reviewerName) {
    this.reviewerName = reviewerName;
  }
  
  public Integer getRating() {
    return rating;
  }
  
  public void setRating(Integer rating) {
    this.rating = rating;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public LocalDateTime getReviewDate() {
    return reviewDate;
  }
  
  public void setReviewDate(LocalDateTime reviewDate) {
    this.reviewDate = reviewDate;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    BookReview that = (BookReview) o;
    return id != null && id.equals(that.id);
  }
  
  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}