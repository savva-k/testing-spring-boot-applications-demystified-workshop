package pragmatech.digital.workshops.lab1.domain;

import java.time.LocalDateTime;

/**
 * Represents a review for a book.
 */
public class BookReview {
    private final String id;
    private final String reviewerName;
    private final int rating;
    private final String comment;
    private final LocalDateTime reviewDate;
    
    public BookReview(String id, String reviewerName, int rating, String comment, LocalDateTime reviewDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Review ID cannot be null or blank");
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
        
        this.id = id;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment; // Comment can be null or blank
        this.reviewDate = reviewDate;
    }
    
    public String getId() {
        return id;
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
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "BookReview{" +
                "id='" + id + '\'' +
                ", reviewerName='" + reviewerName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                '}';
    }
}