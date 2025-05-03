package pragmatech.digital.workshops.lab4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAllAvailableBooks();
    }
    
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{isbn}/enriched")
    public ResponseEntity<Book> getEnrichedBook(@PathVariable String isbn) {
        try {
            Book enrichedBook = bookService.enrichBookWithExternalInfo(isbn);
            return ResponseEntity.ok(enrichedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}