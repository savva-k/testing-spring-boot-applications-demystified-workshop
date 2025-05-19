package pragmatech.digital.workshops.lab2.service;

import org.springframework.stereotype.Service;
import pragmatech.digital.workshops.lab2.dto.BookCreationRequest;
import pragmatech.digital.workshops.lab2.dto.BookUpdateRequest;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookStatus;
import pragmatech.digital.workshops.lab2.exception.BookAlreadyExistsException;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

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
     * @return the ISBN of the created book
     * @throws BookAlreadyExistsException if the book already exists
     */
    public String createBook(BookCreationRequest request) {
        if (bookRepository.existsById(request.isbn())) {
            throw new BookAlreadyExistsException(request.isbn());
        }
        
        Book book = new Book(
            request.isbn(),
            request.title(),
            request.author(),
            request.publishedDate()
        );
        
        Book savedBook = bookRepository.save(book);
        return savedBook.getIsbn();
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
     * Get a book by ISBN
     * 
     * @param isbn the ISBN of the book
     * @return the book, if found
     */
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn);
    }
    
    /**
     * Update a book using a DTO
     * 
     * @param isbn the ISBN of the book to update
     * @param request the update request with new book details
     * @return the updated book if found, or empty if not found
     */
    public Optional<Book> updateBook(String isbn, BookUpdateRequest request) {
        return bookRepository.findById(isbn)
                .map(book -> {
                    book.setTitle(request.getTitle());
                    book.setAuthor(request.getAuthor());
                    book.setPublishedDate(request.getPublishedDate());
                    book.setStatus(request.getStatus());
                    return bookRepository.save(book);
                });
    }
    
    /**
     * Delete a book
     * 
     * @param isbn the ISBN of the book to delete
     * @return true if book was deleted, false if book was not found
     */
    public boolean deleteBook(String isbn) {
        return bookRepository.findById(isbn)
                .map(book -> {
                    bookRepository.delete(book);
                    return true;
                })
                .orElse(false);
    }
}