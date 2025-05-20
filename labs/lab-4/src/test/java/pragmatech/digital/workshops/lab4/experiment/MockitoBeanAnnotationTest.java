package pragmatech.digital.workshops.lab4.experiment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoBean;
import pragmatech.digital.workshops.lab4.client.OpenLibraryApiClient;
import pragmatech.digital.workshops.lab4.dto.BookMetadataResponse;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.service.BookInfoService;
import pragmatech.digital.workshops.lab4.service.BookService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This test demonstrates the usage of @MockitoBean annotation (a synonym of @MockBean).
 *
 * Key characteristics of @MockitoBean vs @MockBean:
 * - Functionally equivalent to @MockBean (they do the same thing)
 * - @MockitoBean was introduced in later versions of Spring Boot Test
 * - The Spring team recommends using @MockBean for consistency
 * - They both replace beans in the Spring context with Mockito mocks
 * - Both create a cache entry per test class, so tests can influence each other
 * - No technical advantage of one over the other
 *
 * Note: This test also demonstrates how injected mocks work with dependencies between Spring beans
 */
@SpringBootTest
@DisplayName("@MockitoBean Annotation Test")
class MockitoBeanAnnotationTest {

    @MockitoBean
    private OpenLibraryApiClient openLibraryApiClient;

    @Autowired
    private BookInfoService bookInfoService;

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("should demonstrate how mocked dependencies affect other services")
    void shouldDemonstrateHowMockedDependenciesAffectOtherServices() {
        // Arrange
        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Original Title");

        BookMetadataResponse metadata = new BookMetadataResponse(
                "/books/123",
                "Enriched Title",
                null, null, null, null, null, null, null,
                "Book description", null, null);

        // Set up the mock that's used by BookInfoService
        when(openLibraryApiClient.getBookByIsbn("1234567890")).thenReturn(metadata);

        // Act - Use BookService which internally uses BookInfoService (which uses our mocked client)
        // This demonstrates how the mock propagates through the dependency injection chain
        // Note: We're not setting up the actual BookService method call here for simplicity
        bookInfoService.enrichBookWithExternalInfo(book);

        // Assert that our mock was called
        verify(openLibraryApiClient, times(1)).getBookByIsbn("1234567890");

        // And the book was enriched with description from our mocked response
        assertThat(book.getDescription()).isEqualTo("Book description");
    }

    @Test
    @DisplayName("should reset mock behavior between tests")
    void shouldResetMockBehaviorBetweenTests() {
        // This mock was set up in the previous test with different behavior
        // But Mockito automatically resets mocks between tests, so we don't see that behavior here

        // Arrange
        Book book = new Book();
        book.setIsbn("1234567890");

        // No mock setup - using default behavior (returns null)

        // Act
        Book result = bookInfoService.enrichBookWithExternalInfo(book);

        // Assert
        assertThat(result).isSameAs(book);
        // Description is null because our mock returns null by default in this test
        assertThat(result.getDescription()).isNull();

        // Still called the mock
        verify(openLibraryApiClient, times(1)).getBookByIsbn("1234567890");
    }
}
