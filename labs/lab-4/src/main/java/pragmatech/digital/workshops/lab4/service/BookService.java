package pragmatech.digital.workshops.lab4.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.entity.BookStatus;
import pragmatech.digital.workshops.lab4.repository.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class BookService {
    
    private final BookRepository bookRepository;
    private final BookInfoService bookInfoService;
    
    @Autowired
    public BookService(BookRepository bookRepository, BookInfoService bookInfoService) {
        this.bookRepository = bookRepository;
        this.bookInfoService = bookInfoService;
    }
    
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        return bookRepository.findById(isbn);
    }
    
    public List<Book> findAllAvailableBooks() {
        return bookRepository.findByStatus(BookStatus.AVAILABLE);
    }
    
    public Book createBook(Book book) {
        if (bookRepository.existsById(book.getIsbn())) {
            throw new IllegalStateException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }
    
    public Book enrichBookWithExternalInfo(String isbn) {
        // Get book from database
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        
        // Enrich with external info
        return bookInfoService.enrichBookWithDetails(book).block();
    }
    
    public CompletableFuture<String> processAsyncRequest(String isbn) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate processing time
                Thread.sleep(100);
                return "processed:" + isbn;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Processing interrupted", e);
            }
        });
    }
}