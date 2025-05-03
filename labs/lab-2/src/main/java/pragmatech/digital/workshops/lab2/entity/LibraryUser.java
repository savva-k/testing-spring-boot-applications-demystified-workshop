package pragmatech.digital.workshops.lab2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user of the library.
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
    private Set<BookLoan> loans = new HashSet<>();
    
    // Default constructor for JPA
    protected LibraryUser() {
    }
    
    public LibraryUser(String name, String email, String membershipNumber, LocalDate memberSince) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Email must be a valid email address");
        }
        if (membershipNumber == null || membershipNumber.isBlank()) {
            throw new IllegalArgumentException("Membership number cannot be null or blank");
        }
        if (memberSince == null) {
            throw new IllegalArgumentException("Member since date cannot be null");
        }
        
        this.name = name;
        this.email = email;
        this.membershipNumber = membershipNumber;
        this.memberSince = memberSince;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Email must be a valid email address");
        }
        this.email = email;
    }
    
    public String getMembershipNumber() {
        return membershipNumber;
    }
    
    public void setMembershipNumber(String membershipNumber) {
        if (membershipNumber == null || membershipNumber.isBlank()) {
            throw new IllegalArgumentException("Membership number cannot be null or blank");
        }
        this.membershipNumber = membershipNumber;
    }
    
    public LocalDate getMemberSince() {
        return memberSince;
    }
    
    public void setMemberSince(LocalDate memberSince) {
        if (memberSince == null) {
            throw new IllegalArgumentException("Member since date cannot be null");
        }
        this.memberSince = memberSince;
    }
    
    public Set<BookLoan> getLoans() {
        return loans;
    }
    
    public int getNumberOfCurrentLoans() {
        return (int) loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .count();
    }
    
    public boolean hasBorrowedBook(Book book) {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .anyMatch(loan -> loan.getBook().equals(book));
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
    
    @Override
    public String toString() {
        return "LibraryUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", membershipNumber='" + membershipNumber + '\'' +
                ", memberSince=" + memberSince +
                '}';
    }
}