package pragmatech.digital.workshops.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab3.entity.LibraryUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for LibraryUser entities.
 */
@Repository
public interface LibraryUserRepository extends JpaRepository<LibraryUser, Long> {
  
  /**
   * Find a user by their email address.
   * 
   * @param email The email address
   * @return The user, if found
   */
  Optional<LibraryUser> findByEmail(String email);
  
  /**
   * Find a user by their membership number.
   * 
   * @param membershipNumber The membership number
   * @return The user, if found
   */
  Optional<LibraryUser> findByMembershipNumber(String membershipNumber);
  
  /**
   * Find users who have joined after a specific date.
   * 
   * @param date The date to check against
   * @return List of users who joined after the given date
   */
  List<LibraryUser> findByMemberSinceAfter(LocalDate date);
  
  /**
   * Find users with active loans.
   * 
   * @return List of users with active loans
   */
  @Query("SELECT DISTINCT u FROM LibraryUser u JOIN u.loans l WHERE l.returnDate IS NULL")
  List<LibraryUser> findUsersWithActiveLoans();
  
  /**
   * Find users with overdue loans.
   * 
   * @param currentDate The current date to check against due dates
   * @return List of users with overdue loans
   */
  @Query("SELECT DISTINCT u FROM LibraryUser u JOIN u.loans l WHERE l.returnDate IS NULL AND l.dueDate < ?1")
  List<LibraryUser> findUsersWithOverdueLoans(LocalDate currentDate);
}