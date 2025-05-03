package pragmatech.digital.workshops.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab3.entity.BookLoan;
import pragmatech.digital.workshops.lab3.entity.LibraryUser;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for BookLoan entities.
 */
@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
  
  /**
   * Find active loans for a specific book.
   * 
   * @param isbn The book's ISBN
   * @return List of active loans
   */
  @Query("SELECT l FROM BookLoan l WHERE l.book.isbn = ?1 AND l.returnDate IS NULL")
  List<BookLoan> findActiveLoansForBook(String isbn);
  
  /**
   * Find all loans for a specific user.
   * 
   * @param user The library user
   * @return List of loans
   */
  List<BookLoan> findByUser(LibraryUser user);
  
  /**
   * Find active loans for a specific user.
   * 
   * @param userId The user's ID
   * @return List of active loans
   */
  @Query("SELECT l FROM BookLoan l WHERE l.user.id = ?1 AND l.returnDate IS NULL")
  List<BookLoan> findActiveLoansForUser(Long userId);
  
  /**
   * Find overdue loans.
   * 
   * @param currentDate The current date
   * @return List of overdue loans
   */
  @Query("SELECT l FROM BookLoan l WHERE l.returnDate IS NULL AND l.dueDate < ?1")
  List<BookLoan> findOverdueLoans(LocalDate currentDate);
  
  /**
   * Count active loans for a specific user.
   * 
   * @param userId The user's ID
   * @return Count of active loans
   */
  @Query("SELECT COUNT(l) FROM BookLoan l WHERE l.user.id = ?1 AND l.returnDate IS NULL")
  long countActiveLoansForUser(Long userId);
}