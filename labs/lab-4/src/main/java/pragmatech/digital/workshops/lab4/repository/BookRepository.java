package pragmatech.digital.workshops.lab4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.entity.BookStatus;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    
    List<Book> findByStatus(BookStatus status);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    @Query(value = "SELECT * FROM book WHERE " +
            "LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(author) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<Book> search(String keyword);
}