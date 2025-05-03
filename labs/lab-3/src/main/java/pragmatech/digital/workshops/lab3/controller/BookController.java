package pragmatech.digital.workshops.lab3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.entity.BookStatus;
import pragmatech.digital.workshops.lab3.service.BookService;

import java.util.List;

/**
 * REST controller for managing books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
  
  private final BookService bookService;
  
  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }
  
  /**
   * GET /api/books - Get all books
   * 
   * @return List of all books
   */
  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    List<Book> books = bookService.getAllBooks();
    return ResponseEntity.ok(books);
  }
  
  /**
   * GET /api/books/{isbn} - Get a specific book by ISBN
   * 
   * @param isbn The book's ISBN
   * @return The book if found, 404 Not Found otherwise
   */
  @GetMapping("/{isbn}")
  public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
    return bookService.getBookByIsbn(isbn)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
  
  /**
   * GET /api/books/search - Search books by title or author
   * 
   * @param title The title to search for
   * @param author The author to search for
   * @return List of matching books
   */
  @GetMapping("/search")
  public ResponseEntity<List<Book>> searchBooks(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String author) {
    
    if (title != null && !title.isEmpty()) {
      return ResponseEntity.ok(bookService.searchBooksByTitle(title));
    } else if (author != null && !author.isEmpty()) {
      return ResponseEntity.ok(bookService.searchBooksByAuthor(author));
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
  
  /**
   * GET /api/books/status/{status} - Get books by status
   * 
   * @param status The book status
   * @return List of books with the given status
   */
  @GetMapping("/status/{status}")
  public ResponseEntity<List<Book>> getBooksByStatus(@PathVariable String status) {
    try {
      BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());
      List<Book> books = bookService.getBooksByStatus(bookStatus);
      return ResponseEntity.ok(books);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }
  
  /**
   * POST /api/books - Create a new book
   * 
   * @param book The book to create
   * @return The created book
   */
  @PostMapping
  public ResponseEntity<Book> createBook(@RequestBody Book book) {
    Book newBook = bookService.createBook(book);
    return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
  }
  
  /**
   * PUT /api/books/{isbn} - Update an existing book
   * 
   * @param isbn The book's ISBN
   * @param book The updated book details
   * @return The updated book if found, 404 Not Found otherwise
   */
  @PutMapping("/{isbn}")
  public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book book) {
    return bookService.updateBook(isbn, book)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
  
  /**
   * PUT /api/books/{isbn}/status - Update a book's status
   * 
   * @param isbn The book's ISBN
   * @param status The new status
   * @return The updated book if found, 404 Not Found otherwise
   */
  @PutMapping("/{isbn}/status")
  public ResponseEntity<Book> updateBookStatus(
      @PathVariable String isbn,
      @RequestParam String status) {
    
    try {
      BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());
      return bookService.updateBookStatus(isbn, bookStatus)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }
  
  /**
   * DELETE /api/books/{isbn} - Delete a book
   * 
   * @param isbn The book's ISBN
   * @return 204 No Content if deleted, 404 Not Found otherwise
   */
  @DeleteMapping("/{isbn}")
  public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
    boolean deleted = bookService.deleteBook(isbn);
    return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}