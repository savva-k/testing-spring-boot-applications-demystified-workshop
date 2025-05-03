package pragmatech.digital.workshops.lab4.solutions;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Solution for Exercise 1: Testing with WebClient and WebTestClient
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class Solution1_WebClientTesting {

    /**
     * Set up WireMock server to mock external book information service
     */
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();
    
    /**
     * Sample book data for testing
     */
    private static final String BOOK_ISBN = "978-1-11111-111-1";
    private static final String BOOK_DETAILS_RESPONSE = """
            {
                "isbn": "978-1-11111-111-1",
                "title": "Clean Code",
                "author": "Robert C. Martin",
                "publishedDate": "2008-08-01",
                "publisher": "Prentice Hall",
                "pages": 464,
                "language": "English",
                "categories": ["Software Engineering", "Programming"],
                "description": "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees."
            }
            """;
    
    /**
     * Configure Spring to use the WireMock server URL for external service
     */
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("book.service.url", wireMockServer::baseUrl);
    }
    
    /**
     * Test configuration for WebClient
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        WebClient webClient() {
            return WebClient.builder()
                    .baseUrl(wireMockServer.baseUrl())
                    .build();
        }
        
        @Bean
        BookInfoService bookInfoService(WebClient webClient) {
            return new BookInfoService(webClient);
        }
    }
    
    @Autowired
    private BookInfoService bookInfoService;
    
    @Autowired
    private WebTestClient webTestClient;
    
    @BeforeEach
    void setUp() {
        // Reset WireMock before each test
        wireMockServer.resetAll();
    }
    
    @Test
    @DisplayName("getBookInfo should return book details when external service responds successfully")
    void getBookInfoReturnsBookDetailsWhenExternalServiceRespondsSuccessfully() {
        // Arrange
        wireMockServer.stubFor(WireMock.get("/books/" + BOOK_ISBN)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(BOOK_DETAILS_RESPONSE)));
        
        // Act
        Mono<BookInfo> result = bookInfoService.getBookInfo(BOOK_ISBN);
        
        // Assert
        StepVerifier.create(result)
                .assertNext(bookInfo -> {
                    assertEquals(BOOK_ISBN, bookInfo.isbn());
                    assertEquals("Clean Code", bookInfo.title());
                    assertEquals("Robert C. Martin", bookInfo.author());
                    assertEquals(464, bookInfo.pages());
                })
                .verifyComplete();
        
        // Verify the request was made
        wireMockServer.verify(getRequestedFor(urlEqualTo("/books/" + BOOK_ISBN)));
    }
    
    @Test
    @DisplayName("getBookInfo should return error when external service returns error")
    void getBookInfoReturnsErrorWhenExternalServiceReturnsError() {
        // Arrange
        wireMockServer.stubFor(WireMock.get("/books/" + BOOK_ISBN)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"error\": \"Book not found\"}")));
        
        // Act
        Mono<BookInfo> result = bookInfoService.getBookInfo(BOOK_ISBN);
        
        // Assert
        StepVerifier.create(result)
                .expectError(BookNotFoundException.class)
                .verify();
        
        // Verify the request was made
        wireMockServer.verify(getRequestedFor(urlEqualTo("/books/" + BOOK_ISBN)));
    }
    
    @Test
    @DisplayName("getBookInfo should return error when external service times out")
    void getBookInfoReturnsErrorWhenExternalServiceTimesOut() {
        // Arrange
        wireMockServer.stubFor(WireMock.get("/books/" + BOOK_ISBN)
                .willReturn(aResponse()
                        .withFixedDelay(5000) // 5 seconds delay
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(BOOK_DETAILS_RESPONSE)));
        
        // Act
        Mono<BookInfo> result = bookInfoService.getBookInfo(BOOK_ISBN);
        
        // Assert
        StepVerifier.create(result)
                .expectError(BookServiceTimeoutException.class)
                .verify();
        
        // Verify the request was made
        wireMockServer.verify(getRequestedFor(urlEqualTo("/books/" + BOOK_ISBN)));
    }
    
    @Test
    @DisplayName("GET /api/books/{isbn}/details should return book details")
    void getBookDetailsEndpointReturnsBookDetails() {
        // Arrange
        wireMockServer.stubFor(WireMock.get("/books/" + BOOK_ISBN)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(BOOK_DETAILS_RESPONSE)));
        
        // Act & Assert
        webTestClient.get()
                .uri("/api/books/{isbn}/details", BOOK_ISBN)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.isbn").isEqualTo(BOOK_ISBN)
                .jsonPath("$.title").isEqualTo("Clean Code")
                .jsonPath("$.author").isEqualTo("Robert C. Martin")
                .jsonPath("$.pages").isEqualTo(464);
    }
    
    @Test
    @DisplayName("GET /api/books/{isbn}/details should return 404 when book not found")
    void getBookDetailsEndpointReturns404WhenBookNotFound() {
        // Arrange
        wireMockServer.stubFor(WireMock.get("/books/" + BOOK_ISBN)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"error\": \"Book not found\"}")));
        
        // Act & Assert
        webTestClient.get()
                .uri("/api/books/{isbn}/details", BOOK_ISBN)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
    
    /**
     * Service class to be tested
     */
    static class BookInfoService {
        private final WebClient webClient;
        
        BookInfoService(WebClient webClient) {
            this.webClient = webClient;
        }
        
        Mono<BookInfo> getBookInfo(String isbn) {
            return webClient.get()
                    .uri("/books/{isbn}", isbn)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> {
                        if (response.statusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new BookNotFoundException("Book not found with ISBN: " + isbn));
                        }
                        return Mono.error(new RuntimeException("Client error: " + response.statusCode()));
                    })
                    .onStatus(HttpStatus::is5xxServerError, response -> 
                            Mono.error(new RuntimeException("Server error: " + response.statusCode())))
                    .bodyToMono(Map.class)
                    .map(map -> new BookInfo(
                            (String) map.get("isbn"),
                            (String) map.get("title"),
                            (String) map.get("author"),
                            (String) map.get("publishedDate"),
                            (String) map.get("publisher"),
                            ((Number) map.get("pages")).intValue(),
                            (String) map.get("language")))
                    .timeout(Duration.ofSeconds(3), 
                            Mono.error(new BookServiceTimeoutException("Request timed out for ISBN: " + isbn)));
        }
    }
    
    /**
     * Data class representing book information
     */
    record BookInfo(
            String isbn,
            String title,
            String author,
            String publishedDate,
            String publisher,
            int pages,
            String language
    ) {}
    
    /**
     * Custom exception for book not found
     */
    static class BookNotFoundException extends RuntimeException {
        BookNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Custom exception for timeout
     */
    static class BookServiceTimeoutException extends RuntimeException {
        BookServiceTimeoutException(String message) {
            super(message);
        }
    }
}