package pragmatech.digital.workshops.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab2.entity.LibraryUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for LibraryUser entities.
 */
@Repository
public interface LibraryUserRepository extends JpaRepository<LibraryUser, Long> {
    
    /**
     * Find a user by email.
     * 
     * @param email the email to search for
     * @return an Optional containing the user, if found
     */
    Optional<LibraryUser> findByEmail(String email);
    
    /**
     * Find a user by membership number.
     * 
     * @param membershipNumber the membership number to search for
     * @return an Optional containing the user, if found
     */
    Optional<LibraryUser> findByMembershipNumber(String membershipNumber);
    
    /**
     * Find users by name.
     * 
     * @param name the name to search for
     * @return list of users with a name containing the given text
     */
    List<LibraryUser> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find users who became members after a given date.
     * 
     * @param date the date threshold
     * @return list of users who became members after the given date
     */
    List<LibraryUser> findByMemberSinceAfter(LocalDate date);
    
    /**
     * Custom query to find users with no current loans.
     * 
     * @return list of users with no current loans
     */
    @Query("SELECT u FROM LibraryUser u WHERE NOT EXISTS (SELECT l FROM BookLoan l WHERE l.user = u AND l.returnDate IS NULL)")
    List<LibraryUser> findUsersWithNoCurrentLoans();
    
    /**
     * Custom query to find users with at least one overdue loan.
     * 
     * @param currentDate the current date
     * @return list of users with at least one overdue loan
     */
    @Query("SELECT DISTINCT u FROM LibraryUser u JOIN u.loans l WHERE l.returnDate IS NULL AND l.dueDate < :currentDate")
    List<LibraryUser> findUsersWithOverdueLoans(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Native SQL query to find the top borrowers (users with the most loans).
     * 
     * @param limit the maximum number of users to return
     * @return list of top borrowers
     */
    @Query(value = "SELECT u.* FROM library_users u " +
                   "JOIN book_loans l ON u.id = l.user_id " +
                   "GROUP BY u.id " +
                   "ORDER BY COUNT(l.id) DESC " +
                   "LIMIT :limit", 
           nativeQuery = true)
    List<LibraryUser> findTopBorrowers(@Param("limit") int limit);
}