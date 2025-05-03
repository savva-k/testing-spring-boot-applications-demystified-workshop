package pragmatech.digital.workshops.lab2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a book in the library system.
 */
@Entity
@Table(name = "books")
public class Book {
    
    @Id
    private String isbn;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;
    
    @OneToMany(mappedBy = "book")
    private Set<BookReview> reviews = new HashSet<>();
    
    @OneToMany(mappedBy = "book")
    private Set<BookLoan> loans = new HashSet<>();
    
    // Default constructor for JPA
    protected Book() {
    }
    
    public Book(String isbn, String title, String author, LocalDate publishedDate) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or blank");
        }
        if (publishedDate == null) {
            throw new IllegalArgumentException("Published date cannot be null");
        }
        
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.status = BookStatus.AVAILABLE;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or blank");
        }
        this.author = author;
    }
    
    public LocalDate getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(LocalDate publishedDate) {
        if (publishedDate == null) {
            throw new IllegalArgumentException("Published date cannot be null");
        }
        this.publishedDate = publishedDate;
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }
    
    public Set<BookReview> getReviews() {
        return reviews;
    }
    
    public Set<BookLoan> getLoans() {
        return loans;
    }
    
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }
    
    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publishedDate=" + publishedDate +
                ", status=" + status +
                '}';
    }
}