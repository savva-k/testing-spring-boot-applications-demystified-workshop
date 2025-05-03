package pragmatech.digital.workshops.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.entity.BookStatus;

import java.util.List;

/**
 * Repository for Book entities.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
  
  /**
   * Find all books by a specific author.
   * 
   * @param author The author's name
   * @return List of books by the author
   */
  List<Book> findByAuthorContainingIgnoreCase(String author);
  
  /**
   * Find all books by title containing the given string (case insensitive).
   * 
   * @param title The title to search for
   * @return List of books matching the title
   */
  List<Book> findByTitleContainingIgnoreCase(String title);
  
  /**
   * Find all books with a specific status.
   * 
   * @param status The book status
   * @return List of books with the specific status
   */
  List<Book> findByStatus(BookStatus status);
  
  /**
   * Count the number of books by a specific author.
   * 
   * @param author The author's name
   * @return The count of books by the author
   */
  long countByAuthorContainingIgnoreCase(String author);
  
  /**
   * Find the most reviewed books.
   * 
   * @param limit The maximum number of books to return
   * @return List of books ordered by review count (descending)
   */
  @Query("SELECT b FROM Book b LEFT JOIN b.reviews r GROUP BY b.isbn ORDER BY COUNT(r) DESC")
  List<Book> findMostReviewedBooks(int limit);
}