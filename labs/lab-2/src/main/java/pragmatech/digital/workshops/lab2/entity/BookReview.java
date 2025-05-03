package pragmatech.digital.workshops.lab2.entity;

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
 * Entity representing a review for a book.
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
    private int rating;
    
    @Column(length = 1000)
    private String comment;
    
    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;
    
    // Default constructor for JPA
    protected BookReview() {
    }
    
    public BookReview(Book book, String reviewerName, int rating, String comment, LocalDateTime reviewDate) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (reviewerName == null || reviewerName.isBlank()) {
            throw new IllegalArgumentException("Reviewer name cannot be null or blank");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (reviewDate == null) {
            throw new IllegalArgumentException("Review date cannot be null");
        }
        
        this.book = book;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment; // Comment can be null or blank
        this.reviewDate = reviewDate;
    }
    
    public Long getId() {
        return id;
    }
    
    public Book getBook() {
        return book;
    }
    
    public String getReviewerName() {
        return reviewerName;
    }
    
    public int getRating() {
        return rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public LocalDateTime getReviewDate() {
        return reviewDate;
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
    
    @Override
    public String toString() {
        return "BookReview{" +
                "id=" + id +
                ", book=" + (book != null ? book.getIsbn() : "null") +
                ", reviewerName='" + reviewerName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                '}';
    }
}