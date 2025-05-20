package pragmatech.digital.workshops.lab3.service;

import org.springframework.stereotype.Service;
import pragmatech.digital.workshops.lab3.dto.BookCreationRequest;
import pragmatech.digital.workshops.lab3.dto.BookUpdateRequest;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.entity.BookStatus;
import pragmatech.digital.workshops.lab3.exception.BookAlreadyExistsException;
import pragmatech.digital.workshops.lab3.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    /**
     * Create a new book from DTO
     * 
     * @param request the book creation request
     * @return the ID of the created book
     * @throws BookAlreadyExistsException if the book already exists
     */
    public Long createBook(BookCreationRequest request) {
        if (bookRepository.findByIsbn(request.isbn()).isPresent()) {
            throw new BookAlreadyExistsException(request.isbn());
        }
        
        Book book = new Book(
            request.isbn(),
            request.title(),
            request.author(),
            request.publishedDate()
        );
        
        Book savedBook = bookRepository.save(book);
        return savedBook.getId();
    }
    
    /**
     * Get all books
     * 
     * @return list of all books
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * Get a book by ID
     * 
     * @param id the ID of the book
     * @return the book, if found
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    /**
     * Get a book by ISBN
     * 
     * @param isbn the ISBN of the book
     * @return the book, if found
     */
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    /**
     * Update a book using a DTO
     * 
     * @param id the ID of the book to update
     * @param request the update request with new book details
     * @return the updated book if found, or empty if not found
     */
    public Optional<Book> updateBook(Long id, BookUpdateRequest request) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(request.title());
                    book.setAuthor(request.author());
                    book.setPublishedDate(request.publishedDate());
                    book.setStatus(request.status());
                    return bookRepository.save(book);
                });
    }
    
    /**
     * Update a book by ISBN using a DTO
     * 
     * @param isbn the ISBN of the book to update
     * @param request the update request with new book details
     * @return the updated book if found, or empty if not found
     */
    public Optional<Book> updateBookByIsbn(String isbn, BookUpdateRequest request) {
        return bookRepository.findByIsbn(isbn)
                .map(book -> {
                    book.setTitle(request.title());
                    book.setAuthor(request.author());
                    book.setPublishedDate(request.publishedDate());
                    book.setStatus(request.status());
                    return bookRepository.save(book);
                });
    }
    
    /**
     * Delete a book by ID
     * 
     * @param id the ID of the book to delete
     * @return true if book was deleted, false if book was not found
     */
    public boolean deleteBook(Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Delete a book by ISBN
     * 
     * @param isbn the ISBN of the book to delete
     * @return true if book was deleted, false if book was not found
     */
    public boolean deleteBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(book -> {
                    bookRepository.delete(book);
                    return true;
                })
                .orElse(false);
    }
}