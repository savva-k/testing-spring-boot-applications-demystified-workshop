package pragmatech.digital.workshops.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab2.entity.BookLoan;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for BookLoan entities.
 */
@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    
    /**
     * Find loans by user ID.
     * 
     * @param userId the ID of the user
     * @return list of loans for the given user
     */
    List<BookLoan> findByUserId(Long userId);
    
    /**
     * Find loans by book ISBN.
     * 
     * @param isbn the ISBN of the book
     * @return list of loans for the given book
     */
    List<BookLoan> findByBookIsbn(String isbn);
    
    /**
     * Find current (not returned) loans by user ID.
     * 
     * @param userId the ID of the user
     * @return list of current loans for the given user
     */
    List<BookLoan> findByUserIdAndReturnDateIsNull(Long userId);
    
    /**
     * Find loans made after a given date.
     * 
     * @param date the date threshold
     * @return list of loans made after the given date
     */
    List<BookLoan> findByLoanDateAfter(LocalDate date);
    
    /**
     * Find overdue loans (due date before the given date and not returned).
     * 
     * @param date the date to check against
     * @return list of overdue loans
     */
    List<BookLoan> findByDueDateBeforeAndReturnDateIsNull(LocalDate date);
    
    /**
     * Custom query to find loans that were returned before their due date.
     * 
     * @return list of loans returned before their due date
     */
    @Query("SELECT l FROM BookLoan l WHERE l.returnDate IS NOT NULL AND l.returnDate < l.dueDate")
    List<BookLoan> findLoansReturnedBeforeDueDate();
    
    /**
     * Custom query to find loans with longest overdue period.
     * 
     * @param limit the maximum number of loans to return
     * @return list of loans with the longest overdue period
     */
    @Query("SELECT l FROM BookLoan l WHERE l.returnDate IS NOT NULL AND l.returnDate > l.dueDate " +
           "ORDER BY (l.returnDate - l.dueDate) DESC")
    List<BookLoan> findLoansWithLongestOverduePeriod(@Param("limit") int limit);
    
    /**
     * Native SQL query to find the average loan duration in days.
     * 
     * @return the average loan duration in days
     */
    @Query(value = "SELECT AVG(DATEDIFF(COALESCE(return_date, CURRENT_DATE), loan_date)) " +
                   "FROM book_loans", 
           nativeQuery = true)
    Double findAverageLoanDurationInDays();
}