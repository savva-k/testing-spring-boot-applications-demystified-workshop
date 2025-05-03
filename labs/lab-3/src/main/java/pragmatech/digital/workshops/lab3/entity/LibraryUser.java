package pragmatech.digital.workshops.lab3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a library user/member.
 */
@Entity
@Table(name = "library_users")
public class LibraryUser {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String name;
  
  @Column(nullable = false, unique = true)
  private String email;
  
  @Column(name = "membership_number", nullable = false, unique = true)
  private String membershipNumber;
  
  @Column(name = "member_since", nullable = false)
  private LocalDate memberSince;
  
  @OneToMany(mappedBy = "user")
  private List<BookLoan> loans = new ArrayList<>();
  
  // Default constructor required by JPA
  public LibraryUser() {
  }
  
  // Constructor with required fields
  public LibraryUser(String name, String email, String membershipNumber, LocalDate memberSince) {
    this.name = name;
    this.email = email;
    this.membershipNumber = membershipNumber;
    this.memberSince = memberSince;
  }
  
  // Getters and setters
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getMembershipNumber() {
    return membershipNumber;
  }
  
  public void setMembershipNumber(String membershipNumber) {
    this.membershipNumber = membershipNumber;
  }
  
  public LocalDate getMemberSince() {
    return memberSince;
  }
  
  public void setMemberSince(LocalDate memberSince) {
    this.memberSince = memberSince;
  }
  
  public List<BookLoan> getLoans() {
    return loans;
  }
  
  public void setLoans(List<BookLoan> loans) {
    this.loans = loans;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    LibraryUser user = (LibraryUser) o;
    return id != null && id.equals(user.id);
  }
  
  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}