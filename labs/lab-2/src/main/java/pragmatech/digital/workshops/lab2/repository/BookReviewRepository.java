package pragmatech.digital.workshops.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab2.entity.BookReview;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for BookReview entities.
 */
@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    
    /**
     * Find reviews by book ISBN.
     * 
     * @param isbn the ISBN of the book
     * @return list of reviews for the given book
     */
    List<BookReview> findByBookIsbn(String isbn);
    
    /**
     * Find reviews by reviewer name.
     * 
     * @param reviewerName the name of the reviewer
     * @return list of reviews by the given reviewer
     */
    List<BookReview> findByReviewerNameContainingIgnoreCase(String reviewerName);
    
    /**
     * Find reviews with a rating greater than or equal to the given value.
     * 
     * @param rating the minimum rating
     * @return list of reviews with a rating >= the given value
     */
    List<BookReview> findByRatingGreaterThanEqual(int rating);
    
    /**
     * Find reviews created after a given date/time.
     * 
     * @param dateTime the date/time threshold
     * @return list of reviews created after the given date/time
     */
    List<BookReview> findByReviewDateAfter(LocalDateTime dateTime);
    
    /**
     * Custom query to find the average rating for a book.
     * 
     * @param isbn the ISBN of the book
     * @return the average rating for the book
     */
    @Query("SELECT AVG(r.rating) FROM BookReview r WHERE r.book.isbn = :isbn")
    Double findAverageRatingByBookIsbn(@Param("isbn") String isbn);
    
    /**
     * Custom query to find the number of reviews for a book.
     * 
     * @param isbn the ISBN of the book
     * @return the number of reviews for the book
     */
    @Query("SELECT COUNT(r) FROM BookReview r WHERE r.book.isbn = :isbn")
    Long countReviewsByBookIsbn(@Param("isbn") String isbn);
    
    /**
     * Native SQL query to find the most reviewed books.
     * 
     * @param limit the maximum number of books to return
     * @return list of ISBN and review counts, ordered by review count (descending)
     */
    @Query(value = "SELECT b.isbn, COUNT(r.id) as review_count " +
                   "FROM books b " +
                   "JOIN book_reviews r ON b.isbn = r.book_isbn " +
                   "GROUP BY b.isbn " +
                   "ORDER BY review_count DESC " +
                   "LIMIT :limit", 
           nativeQuery = true)
    List<Object[]> findMostReviewedBooks(@Param("limit") int limit);
}