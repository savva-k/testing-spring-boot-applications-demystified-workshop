package pragmatech.digital.workshops.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab3.entity.BookReview;

import java.util.List;

/**
 * Repository for BookReview entities.
 */
@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
  
  /**
   * Find all reviews for a specific book.
   * 
   * @param isbn The book's ISBN
   * @return List of reviews
   */
  @Query("SELECT r FROM BookReview r WHERE r.book.isbn = ?1 ORDER BY r.reviewDate DESC")
  List<BookReview> findReviewsForBook(String isbn);
  
  /**
   * Find reviews by a specific reviewer.
   * 
   * @param reviewerName The reviewer's name
   * @return List of reviews by the reviewer
   */
  List<BookReview> findByReviewerNameContainingIgnoreCase(String reviewerName);
  
  /**
   * Find reviews with a specific rating.
   * 
   * @param rating The rating (1-5)
   * @return List of reviews with the specific rating
   */
  List<BookReview> findByRating(Integer rating);
  
  /**
   * Calculate the average rating for a specific book.
   * 
   * @param isbn The book's ISBN
   * @return The average rating or null if no reviews exist
   */
  @Query("SELECT AVG(r.rating) FROM BookReview r WHERE r.book.isbn = ?1")
  Double calculateAverageRatingForBook(String isbn);
  
  /**
   * Count the number of reviews for a specific book.
   * 
   * @param isbn The book's ISBN
   * @return The count of reviews
   */
  long countByBookIsbn(String isbn);
}