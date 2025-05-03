package pragmatech.digital.workshops.lab4.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
public class Book {

    @Id
    private String isbn;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String author;
    
    private LocalDate publishedDate;
    
    private BookStatus status = BookStatus.AVAILABLE;
    
    public Book() {
        // Required by JPA
    }
    
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
    
    public Book(String isbn, String title, String author, LocalDate publishedDate) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
    }
    
    // Getters and setters
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public LocalDate getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookStatus status) {
        this.status = status;
    }
}