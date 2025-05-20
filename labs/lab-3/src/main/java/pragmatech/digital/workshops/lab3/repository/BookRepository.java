package pragmatech.digital.workshops.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.entity.BookStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Book entities.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  
  /**
   * Find a book by its ISBN.
   * 
   * @param isbn The ISBN of the book
   * @return The book if found
   */
  Optional<Book> findByIsbn(String isbn);
  
  /**
   * Find all books by a specific author.
   * 
   * @param author The author's name
   * @return List of books by the author
   */
  List<Book> findByAuthor(String author);
  
  /**
   * Find all books with a specific status.
   * 
   * @param status The book status
   * @return List of books with the specific status
   */
  List<Book> findByStatus(BookStatus status);
  
  /**
   * Check if a book with the given ISBN exists.
   * 
   * @param isbn The ISBN to check
   * @return true if the book exists, false otherwise
   */
  boolean existsByIsbn(String isbn);
}