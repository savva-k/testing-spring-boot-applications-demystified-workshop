package pragmatech.digital.workshops.lab3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.entity.BookStatus;
import pragmatech.digital.workshops.lab3.repository.BookRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing books.
 */
@Service
public class BookService {
  
  private final BookRepository bookRepository;
  
  @Autowired
  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }
  
  /**
   * Get all books.
   * 
   * @return List of all books
   */
  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }
  
  /**
   * Get a book by its ISBN.
   * 
   * @param isbn The book's ISBN
   * @return The book, if found
   */
  public Optional<Book> getBookByIsbn(String isbn) {
    return bookRepository.findById(isbn);
  }
  
  /**
   * Search books by title.
   * 
   * @param title The title to search for
   * @return List of matching books
   */
  public List<Book> searchBooksByTitle(String title) {
    return bookRepository.findByTitleContainingIgnoreCase(title);
  }
  
  /**
   * Search books by author.
   * 
   * @param author The author to search for
   * @return List of matching books
   */
  public List<Book> searchBooksByAuthor(String author) {
    return bookRepository.findByAuthorContainingIgnoreCase(author);
  }
  
  /**
   * Get books by status.
   * 
   * @param status The book status
   * @return List of books with the given status
   */
  public List<Book> getBooksByStatus(BookStatus status) {
    return bookRepository.findByStatus(status);
  }
  
  /**
   * Create a new book.
   * 
   * @param book The book to create
   * @return The created book
   */
  @Transactional
  public Book createBook(Book book) {
    if (book.getStatus() == null) {
      book.setStatus(BookStatus.AVAILABLE);
    }
    return bookRepository.save(book);
  }
  
  /**
   * Update an existing book.
   * 
   * @param isbn The book's ISBN
   * @param bookDetails The updated book details
   * @return The updated book, if found
   */
  @Transactional
  public Optional<Book> updateBook(String isbn, Book bookDetails) {
    return bookRepository.findById(isbn)
        .map(existingBook -> {
          existingBook.setTitle(bookDetails.getTitle());
          existingBook.setAuthor(bookDetails.getAuthor());
          existingBook.setPublishedDate(bookDetails.getPublishedDate());
          existingBook.setStatus(bookDetails.getStatus());
          return bookRepository.save(existingBook);
        });
  }
  
  /**
   * Update a book's status.
   * 
   * @param isbn The book's ISBN
   * @param status The new status
   * @return The updated book, if found
   */
  @Transactional
  public Optional<Book> updateBookStatus(String isbn, BookStatus status) {
    return bookRepository.findById(isbn)
        .map(existingBook -> {
          existingBook.setStatus(status);
          return bookRepository.save(existingBook);
        });
  }
  
  /**
   * Delete a book.
   * 
   * @param isbn The book's ISBN
   * @return true if the book was deleted, false otherwise
   */
  @Transactional
  public boolean deleteBook(String isbn) {
    return bookRepository.findById(isbn)
        .map(book -> {
          bookRepository.delete(book);
          return true;
        })
        .orElse(false);
  }
}