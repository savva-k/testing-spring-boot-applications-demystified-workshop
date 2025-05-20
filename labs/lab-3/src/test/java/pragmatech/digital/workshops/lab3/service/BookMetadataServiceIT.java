package pragmatech.digital.workshops.lab3.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pragmatech.digital.workshops.lab3.config.WireMockContextInitializer;
import pragmatech.digital.workshops.lab3.dto.BookMetadataDTO;
import pragmatech.digital.workshops.lab3.entity.Book;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(initializers = {WireMockContextInitializer.class})
@ActiveProfiles("test")
class BookMetadataServiceIT {

    @Autowired
    private BookMetadataService cut;
    
    @Test
    @DisplayName("should retrieve book metadata from OpenLibrary API (stubbed with WireMock)")
    void shouldRetrieveBookMetadataFromExternalApi() {
        // Arrange
        String isbn = "9780132350884";
        
        // Act
        BookMetadataDTO result = cut.getBookMetadata(isbn);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Clean Code: A Handbook of Agile Software Craftsmanship");
        assertThat(result.getMainIsbn()).isEqualTo("9780132350884");
        assertThat(result.getPublisher()).isEqualTo("Prentice Hall");
        assertThat(result.description()).contains("Even bad code can function");
    }
    
    @Test
    @DisplayName("should return null when book not found in OpenLibrary API")
    void shouldReturnNullWhenBookNotFoundInExternalApi() {
        // Arrange
        String isbn = "9999999999"; // This ISBN is not stubbed for success
        
        // Act
        BookMetadataDTO result = cut.getBookMetadata(isbn);
        
        // Assert
        assertThat(result).isNull();
    }
    
    @Test
    @DisplayName("should enrich book with metadata from OpenLibrary API")
    void shouldEnrichBookWithMetadataFromExternalApi() {
        // Arrange
        Book book = new Book();
        book.setIsbn("9780132350884");
        book.setTitle("Clean Code");
        
        // Act
        Book enriched = cut.enrichBookWithMetadata(book);
        
        // Assert
        assertThat(enriched).isNotNull();
        assertThat(enriched.getDescription()).contains("Even bad code can function");
        assertThat(enriched.getPublisher()).isEqualTo("Prentice Hall");
        assertThat(enriched.getPageCount()).isEqualTo(431);
        
        // Since publishDate is '2008', it should parse to January 1, 2008
        LocalDate expectedDate = LocalDate.of(2008, 1, 1);
        assertThat(enriched.getPublishedDate()).isEqualTo(expectedDate);
        
        // Author from first authorRef
        assertThat(enriched.getAuthor()).isEqualTo("Robert C. Martin");
    }
    
    @Test
    @DisplayName("should return original book when metadata not found in OpenLibrary API")
    void shouldReturnOriginalBookWhenMetadataNotFound() {
        // Arrange
        Book book = new Book();
        book.setIsbn("9999999999");
        book.setTitle("Unknown Book");
        
        // Act
        Book result = cut.enrichBookWithMetadata(book);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Unknown Book");
        assertThat(result.getDescription()).isNull();
    }
}