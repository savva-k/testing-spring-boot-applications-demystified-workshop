package pragmatech.digital.workshops.lab2.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pragmatech.digital.workshops.lab2.dto.BookCreationRequest;
import pragmatech.digital.workshops.lab2.dto.BookUpdateRequest;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.service.BookService;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  /**
   * Get all books.
   *
   * @return list of books
   */
  @GetMapping
  public List<Book> getAllBooks() {
    return bookService.getAllBooks();
  }

  /**
   * Get a book by ISBN.
   *
   * @param isbn the ISBN of the book
   * @return the book, if found
   */
  @GetMapping("/{isbn}")
  public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
    return bookService.getBookByIsbn(isbn)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Create a new book.
   *
   * @param request the validated book creation request
   * @return 201 Created with location header pointing to the new resource
   */
  @PostMapping
  public ResponseEntity<Void> createBook(@Valid @RequestBody BookCreationRequest request) {
    String isbn = bookService.createBook(request);
    
    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{isbn}")
      .buildAndExpand(isbn)
      .toUri();
      
    return ResponseEntity.created(location).build();
  }

  /**
   * Update a book.
   *
   * @param isbn the ISBN of the book to update
   * @param request the validated book update request
   * @return the updated book
   */
  @PutMapping("/{isbn}")
  public ResponseEntity<Book> updateBook(
      @PathVariable String isbn,
      @Valid @RequestBody BookUpdateRequest request) {
    return bookService.updateBook(isbn, request)
      .map(ResponseEntity::ok)
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
    return bookService.deleteBook(isbn)
      ? ResponseEntity.noContent().build()
      : ResponseEntity.notFound().build();
  }
}