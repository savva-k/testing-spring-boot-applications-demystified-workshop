package pragmatech.digital.workshops.lab3.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pragmatech.digital.workshops.lab3.client.OpenLibraryApiClient;
import pragmatech.digital.workshops.lab3.dto.BookCreationRequest;
import pragmatech.digital.workshops.lab3.dto.BookMetadataDTO;
import pragmatech.digital.workshops.lab3.dto.BookUpdateRequest;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.exception.BookAlreadyExistsException;
import pragmatech.digital.workshops.lab3.repository.BookRepository;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final OpenLibraryApiClient openLibraryApiClient;

    @Autowired
    public BookService(BookRepository bookRepository, OpenLibraryApiClient openLibraryApiClient) {
        this.bookRepository = bookRepository;
        this.openLibraryApiClient = openLibraryApiClient;
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

        try {
            // Enrich the book with metadata from OpenLibrary
            logger.info("Enriching book with metadata from OpenLibrary: ISBN={}", request.isbn());
            enrichBookWithMetadata(book);
        } catch (Exception e) {
            // Don't fail the book creation if metadata enrichment fails
            logger.error("Failed to enrich book with metadata: ISBN={}", request.isbn(), e);
        }

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
    
    /**
     * Enrich a book entity with metadata from OpenLibrary.
     *
     * @param book The book entity to enrich
     * @return The enriched book
     */
    public Book enrichBookWithMetadata(Book book) {
        if (book == null || book.getIsbn() == null) {
            return book;
        }
        
        BookMetadataDTO metadata = openLibraryApiClient.getBookByIsbn(book.getIsbn());
        
        if (metadata == null) {
            return book;
        }
        
        // Only update fields that are not already set
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            book.setTitle(metadata.title());
        }
        
        if (metadata.description() != null && (book.getDescription() == null || book.getDescription().isEmpty())) {
            book.setDescription(metadata.description());
        }
        
        if (metadata.getPublisher() != null && (book.getPublisher() == null || book.getPublisher().isEmpty())) {
            book.setPublisher(metadata.getPublisher());
        }
        
        if (book.getPublishedDate() == null && metadata.publishDate() != null) {
            book.setPublishedDate(parsePublishDate(metadata.publishDate()));
        }
        
        if (metadata.getCoverUrl() != null && (book.getThumbnailUrl() == null || book.getThumbnailUrl().isEmpty())) {
            book.setThumbnailUrl(metadata.getCoverUrl());
        }
        
        if (metadata.numberOfPages() != null && book.getPageCount() == null) {
            book.setPageCount(metadata.numberOfPages());
        }
        
        // Extract author from author references if available
        if (metadata.authorRefs() != null && !metadata.authorRefs().isEmpty() 
                && (book.getAuthor() == null || book.getAuthor().isEmpty())) {
            // For now, just use the first author reference's name if available
            var authorRef = metadata.authorRefs().get(0);
            if (authorRef.containsKey("name")) {
                book.setAuthor(authorRef.get("name"));
            }
        }
        
        return book;
    }
    
    /**
     * Parse a publish date string from OpenLibrary into a LocalDate.
     *
     * @param publishDateStr The date string from OpenLibrary
     * @return LocalDate or null if parsing fails
     */
    private LocalDate parsePublishDate(String publishDateStr) {
        if (publishDateStr == null || publishDateStr.isEmpty()) {
            return null;
        }
        
        try {
            // Try parsing with common date formats
            String[] patterns = {
                "MMMM d, yyyy", 
                "MMM d, yyyy",
                "MMMM yyyy", 
                "MMM yyyy", 
                "yyyy",
                "MMMM d yyyy",
                "MMM d yyyy"
            };
            
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
                    return LocalDate.parse(publishDateStr, formatter);
                } catch (DateTimeParseException e) {
                    // Try the next pattern
                }
            }
            
            // If we can't parse the date, extract the year at least
            String yearStr = publishDateStr.replaceAll("[^0-9]", " ")
                    .trim()
                    .replaceAll("\\s+", " ");
            String[] parts = yearStr.split(" ");
            if (parts.length > 0) {
                // Assume the largest 4-digit number is the year
                for (String part : parts) {
                    if (part.length() == 4) {
                        return LocalDate.of(Integer.parseInt(part), 1, 1);
                    }
                }
            }
            
            // Default fallback
            return null;
        } catch (Exception e) {
            logger.warn("Failed to parse publish date: {}", publishDateStr, e);
            return null;
        }
    }
}
