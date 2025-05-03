package pragmatech.digital.workshops.lab2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookStatus;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookRepository bookRepository;
    
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    /**
     * Get all books.
     * 
     * @return list of books
     */
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * Get a book by ISBN.
     * 
     * @param isbn the ISBN of the book
     * @return the book, if found
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return bookRepository.findById(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new book.
     * 
     * @param book the book to create
     * @return the created book
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        if (bookRepository.existsById(book.getIsbn())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(bookRepository.save(book));
    }
    
    /**
     * Update a book.
     * 
     * @param isbn the ISBN of the book to update
     * @param bookDetails the updated book details
     * @return the updated book
     */
    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book bookDetails) {
        return bookRepository.findById(isbn)
                .map(book -> {
                    book.setTitle(bookDetails.getTitle());
                    book.setAuthor(bookDetails.getAuthor());
                    book.setPublishedDate(bookDetails.getPublishedDate());
                    book.setStatus(bookDetails.getStatus());
                    return ResponseEntity.ok(bookRepository.save(book));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete a book.
     * 
     * @param isbn the ISBN of the book to delete
     * @return no content if successful, not found if the book doesn't exist
     */
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        return bookRepository.findById(isbn)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Search for books by various criteria.
     * 
     * @param title optional title to search for
     * @param author optional author to search for
     * @param status optional status to filter by
     * @return list of books matching the criteria
     */
    @GetMapping("/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BookStatus status) {
        
        if (title != null && !title.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null && !author.isEmpty()) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (status != null) {
            return bookRepository.findByStatus(status);
        } else {
            return bookRepository.findAll();
        }
    }
    
    /**
     * Get available books.
     * 
     * @return list of available books
     */
    @GetMapping("/available")
    public List<Book> getAvailableBooks() {
        return bookRepository.findByStatus(BookStatus.AVAILABLE);
    }
    
    /**
     * Update the status of a book.
     * 
     * @param isbn the ISBN of the book
     * @param status the new status
     * @return the updated book
     */
    @PatchMapping("/{isbn}/status")
    public ResponseEntity<Book> updateBookStatus(
            @PathVariable String isbn,
            @RequestParam BookStatus status) {
        
        Optional<Book> bookOptional = bookRepository.findById(isbn);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setStatus(status);
            return ResponseEntity.ok(bookRepository.save(book));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get recently published books.
     * 
     * @param months number of months to look back
     * @return list of recently published books
     */
    @GetMapping("/recent")
    public List<Book> getRecentBooks(@RequestParam(defaultValue = "12") int months) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);
        return bookRepository.findByPublishedDateAfter(cutoffDate);
    }
    
    /**
     * Get books with high ratings.
     * 
     * @return list of books with high ratings
     */
    @GetMapping("/top-rated")
    public List<Book> getTopRatedBooks() {
        return bookRepository.findBooksWithHighRatings();
    }
}