package pragmatech.digital.workshops.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Book entities.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    
    /**
     * Find books by author.
     * 
     * @param author the author name to search for
     * @return list of books by the given author
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    /**
     * Find books by title.
     * 
     * @param title the title to search for
     * @return list of books with a title containing the given text
     */
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find books by status.
     * 
     * @param status the status to search for
     * @return list of books with the given status
     */
    List<Book> findByStatus(BookStatus status);
    
    /**
     * Find books published after a given date.
     * 
     * @param date the date threshold
     * @return list of books published after the given date
     */
    List<Book> findByPublishedDateAfter(LocalDate date);
    
    /**
     * Find available books that match a given title.
     * 
     * @param title the title to search for
     * @return list of available books with a title containing the given text
     */
    List<Book> findByStatusAndTitleContainingIgnoreCase(BookStatus status, String title);
    
    /**
     * Custom query to find books with high ratings (average rating >= 4).
     * 
     * @return list of books with an average rating of 4 or higher
     */
    @Query("SELECT b FROM Book b JOIN b.reviews r GROUP BY b.isbn HAVING AVG(r.rating) >= 4.0")
    List<Book> findBooksWithHighRatings();
    
    /**
     * Find books by ISBN or title containing the given text.
     * 
     * @param isbnOrTitle the text to search for in ISBN or title
     * @return list of books matching the criteria
     */
    @Query("SELECT b FROM Book b WHERE b.isbn LIKE %:isbnOrTitle% OR LOWER(b.title) LIKE LOWER(CONCAT('%', :isbnOrTitle, '%'))")
    List<Book> findByIsbnOrTitle(@Param("isbnOrTitle") String isbnOrTitle);
    
    /**
     * Native SQL query to find books by author ordered by publication date.
     * 
     * @param author the author name to search for
     * @return list of books by the given author, ordered by publication date (newest first)
     */
    @Query(value = "SELECT * FROM books WHERE LOWER(author) LIKE LOWER(CONCAT('%', :author, '%')) ORDER BY published_date DESC", 
           nativeQuery = true)
    List<Book> findByAuthorOrderByPublishedDateDesc(@Param("author") String author);
}