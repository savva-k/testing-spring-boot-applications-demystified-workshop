package pragmatech.digital.workshops.lab4.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.entity.BookStatus;
import pragmatech.digital.workshops.lab4.repository.BookRepository;
import pragmatech.digital.workshops.lab4.service.BookInfoService;
import pragmatech.digital.workshops.lab4.service.BookService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This test class demonstrates best practices for organizing and writing tests
 */
@ExtendWith(MockitoExtension.class)  // Use the right extension
@DisplayName("Book Service Tests")    // Use descriptive display names
class TestingBestPracticesTest {

    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private BookInfoService bookInfoService;
    
    @InjectMocks
    private BookService bookService;
    
    @Captor
    private ArgumentCaptor<Book> bookCaptor;
    
    // Organize related tests using nested classes
    @Nested
    @DisplayName("Finding books by ISBN")
    class FindByIsbnTests {
        
        @Test
        @DisplayName("Returns book when found in repository")
        void findByIsbn_returnsBook_whenFound() {
            // Given (Arrange)
            Book book = new Book("123", "Test Title", "Test Author");
            when(bookRepository.findById("123")).thenReturn(Optional.of(book));
            
            // When (Act)
            Optional<Book> result = bookService.findByIsbn("123");
            
            // Then (Assert)
            assertTrue(result.isPresent());
            assertEquals("Test Title", result.get().getTitle());
            verify(bookRepository).findById("123");
        }
        
        @Test
        @DisplayName("Returns empty when book not found")
        void findByIsbn_returnsEmpty_whenNotFound() {
            // Given
            when(bookRepository.findById("999")).thenReturn(Optional.empty());
            
            // When
            Optional<Book> result = bookService.findByIsbn("999");
            
            // Then
            assertTrue(result.isEmpty());
        }
        
        @ParameterizedTest
        @DisplayName("Throws IllegalArgumentException for invalid ISBNs")
        @ValueSource(strings = {"", "  "})
        void findByIsbn_throwsException_whenIsbnIsInvalid(String isbn) {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> 
                bookService.findByIsbn(isbn)
            );
        }
        
        @Test
        @DisplayName("Throws IllegalArgumentException for null ISBN")
        void findByIsbn_throwsException_whenIsbnIsNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> 
                bookService.findByIsbn(null)
            );
        }
    }
    
    @Nested
    @DisplayName("Creating books")
    class CreateBookTests {
        
        @Test
        @DisplayName("Saves and returns book when ISBN is unique")
        void createBook_savesAndReturnsBook_whenIsbnIsUnique() {
            // Given
            Book book = new Book("123", "Test Title", "Test Author");
            when(bookRepository.existsById("123")).thenReturn(false);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            
            // When
            Book createdBook = bookService.createBook(book);
            
            // Then
            assertNotNull(createdBook);
            assertEquals("Test Title", createdBook.getTitle());
            verify(bookRepository).save(book);
        }
        
        @Test
        @DisplayName("Throws exception when ISBN already exists")
        void createBook_throwsException_whenIsbnAlreadyExists() {
            // Given
            Book book = new Book("123", "Test Title", "Test Author");
            when(bookRepository.existsById("123")).thenReturn(true);
            
            // When & Then
            assertThrows(IllegalStateException.class, () -> 
                bookService.createBook(book)
            );
            verify(bookRepository, never()).save(any(Book.class));
        }
    }
    
    @Nested
    @DisplayName("Testing asynchronous operations")
    class AsyncOperationsTests {
        
        @Test
        @DisplayName("Complete successfully for valid ISBN")
        void processAsyncRequest_completesSuccessfully_forValidIsbn() throws Exception {
            // When
            CompletableFuture<String> future = bookService.processAsyncRequest("123");
            
            // Then
            String result = future.get(); // Wait for completion
            assertEquals("processed:123", result);
        }
        
        @ParameterizedTest
        @DisplayName("Process different ISBN values correctly")
        @CsvSource({
            "123, processed:123",
            "456, processed:456",
            "ABC, processed:ABC"
        })
        void processAsyncRequest_returnsExpectedResult_forDifferentIsbns(
                String isbn, String expectedResult) throws Exception {
            // When
            CompletableFuture<String> future = bookService.processAsyncRequest(isbn);
            
            // Then
            String result = future.get();
            assertEquals(expectedResult, result);
        }
    }
}