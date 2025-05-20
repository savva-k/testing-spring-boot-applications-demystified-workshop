package pragmatech.digital.workshops.lab4.experiment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pragmatech.digital.workshops.lab4.client.OpenLibraryApiClient;
import pragmatech.digital.workshops.lab4.dto.BookMetadataResponse;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.repository.BookRepository;
import pragmatech.digital.workshops.lab4.service.BookInfoService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This test demonstrates the usage of @Mock annotation from Mockito with JUnit 5.
 *
 * Key characteristics of @Mock:
 * - A plain Mockito annotation, not Spring-specific
 * - Creates mock objects but doesn't register them in Spring context
 * - Requires MockitoExtension to be activated (@ExtendWith(MockitoExtension.class))
 * - Typically faster as it doesn't start Spring context
 * - Suitable for plain unit tests that don't need Spring features
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("@Mock Annotation Test")
class MockAnnotationTest {

    @Mock
    private OpenLibraryApiClient openLibraryApiClient;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookInfoService bookInfoService;

    @Test
    @DisplayName("should return book when enriching with metadata")
    void shouldReturnBookWhenEnrichingWithMetadata() {
        // Arrange
        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Original Title");

        BookMetadataResponse metadata = new BookMetadataResponse(
                "/books/123",
                "Enriched Title",
                null, null, null, null, null, null, null,
                "Book description", null, null);

        when(openLibraryApiClient.getBookByIsbn("1234567890")).thenReturn(metadata);

        // Act
        Book enrichedBook = bookInfoService.enrichBookWithExternalInfo(book);

        // Assert
        assertThat(enrichedBook).isNotNull();
        assertThat(enrichedBook.getTitle()).isEqualTo("Original Title"); // Title shouldn't change
        assertThat(enrichedBook.getDescription()).isEqualTo("Book description"); // Description should be added

        verify(openLibraryApiClient, times(1)).getBookByIsbn(anyString());

        // Note: bookRepository was not used, so we don't verify interactions with it
    }

    @Test
    @DisplayName("should demonstrate setting up multiple mock behaviors")
    void shouldDemonstrateMultipleMockBehaviors() {
        // Arrange
        when(bookRepository.findByIsbn("123")).thenReturn(Optional.of(new Book()));
        when(bookRepository.findByIsbn("456")).thenReturn(Optional.empty());

        // We can also set up mocks to throw exceptions
        when(bookRepository.findByIsbn("error")).thenThrow(new RuntimeException("Simulated error"));

        // Act & Assert
        assertThat(bookRepository.findByIsbn("123")).isPresent();
        assertThat(bookRepository.findByIsbn("456")).isEmpty();

        try {
            bookRepository.findByIsbn("error");
            // If we reach here, the test should fail
            assertThat(false).isTrue(); // This will always fail
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Simulated error");
        }
    }
}
