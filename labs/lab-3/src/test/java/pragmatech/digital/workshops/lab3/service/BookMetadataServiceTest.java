package pragmatech.digital.workshops.lab3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import pragmatech.digital.workshops.lab3.dto.BookMetadataDTO;
import pragmatech.digital.workshops.lab3.entity.Book;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class BookMetadataServiceTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();
    
    private BookMetadataService cut;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .baseUrl(wireMockServer.baseUrl())
                .build();
        
        cut = new BookMetadataService(webClient);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }
    
    @Nested
    @DisplayName("getBookMetadata")
    class GetBookMetadata {
        
        @Test
        @DisplayName("should return book metadata when OpenLibrary API returns valid response")
        void shouldReturnBookMetadataWhenApiReturnsValidResponse() throws IOException {
            // Arrange
            String isbn = "9780132350884";
            
            // The stub is loaded from mappings/openlibrary-mappings.json
            
            // Act
            BookMetadataDTO result = cut.getBookMetadata(isbn);
            
            // Assert
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Clean Code: A Handbook of Agile Software Craftsmanship");
            
            // Check convenience methods
            assertThat(result.getMainIsbn()).isEqualTo("9780132350884");
            assertThat(result.getPublisher()).isEqualTo("Prentice Hall");
            
            // Check mapped fields
            assertThat(result.numberOfPages()).isEqualTo(431);
            assertThat(result.publishDate()).isEqualTo("2008");
            assertThat(result.description()).contains("Even bad code can function.");
        }
        
        @Test
        @DisplayName("should return null when book metadata not found")
        void shouldReturnNullWhenBookMetadataNotFound() {
            // Arrange
            String isbn = "9999999999";
            
            // The stub is loaded from mappings/openlibrary-mappings.json
            
            // Act & Assert
            assertThat(cut.getBookMetadata(isbn)).isNull();
        }
    }
    
    @Nested
    @DisplayName("enrichBookWithMetadata")
    class EnrichBookWithMetadata {
        
        @Test
        @DisplayName("should enrich book entity with metadata from OpenLibrary")
        void shouldEnrichBookEntityWithMetadata() {
            // Arrange
            String isbn = "9780132350884";
            
            // The stub is loaded from mappings/openlibrary-mappings.json
            
            Book book = new Book();
            book.setIsbn(isbn);
            book.setTitle("Clean Code"); // Existing title that won't be overwritten
            
            // Act
            Book enrichedBook = cut.enrichBookWithMetadata(book);
            
            // Assert
            assertThat(enrichedBook).isNotNull();
            assertThat(enrichedBook.getTitle()).isEqualTo("Clean Code"); // Original title preserved
            assertThat(enrichedBook.getDescription()).contains("Even bad code can function");
            assertThat(enrichedBook.getPublisher()).isEqualTo("Prentice Hall");
            assertThat(enrichedBook.getPageCount()).isEqualTo(431);
            
            // Since publishDate is '2008', it should parse to January 1, 2008
            LocalDate expectedDate = LocalDate.of(2008, 1, 1);
            assertThat(enrichedBook.getPublishedDate()).isEqualTo(expectedDate);
            
            // OpenLibrary response has "Robert C. Martin" as author name
            assertThat(enrichedBook.getAuthor()).isEqualTo("Robert C. Martin");
        }
        
        @Test
        @DisplayName("should return original book when metadata not found")
        void shouldReturnOriginalBookWhenMetadataNotFound() {
            // Arrange
            String isbn = "9999999999";
            
            // The stub is loaded from mappings/openlibrary-mappings.json
            
            Book book = new Book();
            book.setIsbn(isbn);
            book.setTitle("Unknown Book");
            
            // Act
            Book result = cut.enrichBookWithMetadata(book);
            
            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Unknown Book");
            assertThat(result.getDescription()).isNull();
        }
    }
}