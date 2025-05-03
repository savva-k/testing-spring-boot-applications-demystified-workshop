package pragmatech.digital.workshops.lab1.service;

import org.springframework.stereotype.Service;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.domain.BookStatus;
import pragmatech.digital.workshops.lab1.util.TimeProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing books in the library.
 */
@Service
public class BookService {
    
    private final Map<String, Book> books = new HashMap<>();
    private final TimeProvider timeProvider;
    
    public BookService(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }
    
    public Book addBook(String isbn, String title, String author, LocalDate publishedDate) {
        if (books.containsKey(isbn)) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " already exists");
        }
        
        Book book = new Book(isbn, title, author, publishedDate);
        books.put(isbn, book);
        return book;
    }
    
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }
    
    public List<Book> findByAuthor(String author) {
        return books.values().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .toList();
    }
    
    public List<Book> findAllBooks() {
        return new ArrayList<>(books.values());
    }
    
    public List<Book> findAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .toList();
    }
    
    public Book updateBookStatus(String isbn, BookStatus status) {
        Book book = books.get(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found");
        }
        book.setStatus(status);
        return book;
    }
    
    public void removeBook(String isbn) {
        if (!books.containsKey(isbn)) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found");
        }
        books.remove(isbn);
    }
    
    public List<Book> findRecentBooks(int months) {
        LocalDate cutoffDate = timeProvider.getCurrentDate().minusMonths(months);
        return books.values().stream()
                .filter(book -> !book.getPublishedDate().isBefore(cutoffDate))
                .toList();
    }
}