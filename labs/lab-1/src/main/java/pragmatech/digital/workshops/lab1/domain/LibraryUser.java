package pragmatech.digital.workshops.lab1.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user of the library system.
 */
public class LibraryUser {
    private final String id;
    private String name;
    private String email;
    private String membershipNumber;
    private LocalDate memberSince;
    private final List<BookLoan> borrowedBooks;
    
    public LibraryUser(String id, String name, String email, String membershipNumber, LocalDate memberSince) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
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
        
        this.id = id;
        this.name = name;
        this.email = email;
        this.membershipNumber = membershipNumber;
        this.memberSince = memberSince;
        this.borrowedBooks = new ArrayList<>();
    }
    
    public String getId() {
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
    
    public List<BookLoan> getBorrowedBooks() {
        return new ArrayList<>(borrowedBooks);
    }
    
    public void addBookLoan(BookLoan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null");
        }
        borrowedBooks.add(loan);
    }
    
    public void removeBookLoan(BookLoan loan) {
        borrowedBooks.remove(loan);
    }
    
    public int getNumberOfCurrentLoans() {
        return (int) borrowedBooks.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .count();
    }
    
    public boolean hasBorrowedBook(Book book) {
        return borrowedBooks.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .anyMatch(loan -> loan.getBook().equals(book));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        LibraryUser user = (LibraryUser) o;
        return id.equals(user.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "LibraryUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", membershipNumber='" + membershipNumber + '\'' +
                ", memberSince=" + memberSince +
                '}';
    }
}