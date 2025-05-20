package pragmatech.digital.workshops.lab4.experiment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pragmatech.digital.workshops.lab4.client.OpenLibraryApiClient;
import pragmatech.digital.workshops.lab4.dto.BookMetadataResponse;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.service.BookInfoService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This test demonstrates the usage of @MockBean annotation from Spring Boot Test.
 *
 * Key characteristics of @MockBean:
 * - Spring Boot Test specific annotation
 * - Creates a Mockito mock and adds it to the Spring application context
 * - Replaces any existing bean of the same type in the context with the mock
 * - Requires a full Spring application context to be started
 * - Slower than @Mock as it needs to start Spring context
 * - Useful for integration tests where you want to mock specific beans in the context
 * - When you modify mocks between tests, it triggers context reload (slower)
 */
@SpringBootTest
@DisplayName("@MockBean Annotation Test")
class MockBeanAnnotationTest {

    @MockBean
    private OpenLibraryApiClient openLibraryApiClient;

    @Autowired
    private BookInfoService bookInfoService;

    @Test
    @DisplayName("should enrich book with external info from mocked client")
    void shouldEnrichBookWithExternalInfoFromMockedClient() {
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
    }

    @Test
    @DisplayName("should return original book when API returns null")
    void shouldReturnOriginalBookWhenApiReturnsNull() {
        // Arrange
        Book book = new Book();
        book.setIsbn("9999999999");
        book.setTitle("Original Title");

        when(openLibraryApiClient.getBookByIsbn("9999999999")).thenReturn(null);

        // Act
        Book result = bookInfoService.enrichBookWithExternalInfo(book);

        // Assert
        assertThat(result).isSameAs(book);
        assertThat(result.getTitle()).isEqualTo("Original Title");
        assertThat(result.getDescription()).isNull();

        verify(openLibraryApiClient, times(1)).getBookByIsbn("9999999999");
    }
}
