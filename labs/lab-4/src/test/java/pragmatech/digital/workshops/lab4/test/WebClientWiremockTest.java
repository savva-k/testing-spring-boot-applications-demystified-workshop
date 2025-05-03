package pragmatech.digital.workshops.lab4.test;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.service.BookInfoService;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@WireMockTest(httpPort = 8080)
@TestPropertySource(properties = "book.info.service.url=http://localhost:8080")
@ActiveProfiles("test")
public class WebClientWiremockTest {

    @Autowired
    private BookInfoService bookInfoService;

    @Test
    void getBookDetails_returnBookInfo_whenApiSucceeds() {
        // Set up mock response
        stubFor(get(urlEqualTo("/api/books/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"isbn\":\"123\",\"title\":\"Spring Boot Testing\",\"author\":\"Test Author\"}")));

        // Call the service
        Book book = bookInfoService.getBookDetails("123").block();

        // Verify the result
        assertThat(book).isNotNull();
        assertThat(book.getIsbn()).isEqualTo("123");
        assertThat(book.getTitle()).isEqualTo("Spring Boot Testing");
        assertThat(book.getAuthor()).isEqualTo("Test Author");

        // Verify the request was made as expected
        verify(getRequestedFor(urlEqualTo("/api/books/123")));
    }

    @Test
    void getBookDetails_throwsException_whenApiReturnsError() {
        // Set up mock response
        stubFor(get(urlEqualTo("/api/books/error"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"Internal Server Error\"}")));

        // Verify that the service throws an exception
        assertThrows(WebClientResponseException.class, () -> {
            bookInfoService.getBookDetails("error").block();
        });
    }

    @Test
    void getBookDetails_throwsException_whenApiTimesOut() {
        // Set up mock response with a delay
        stubFor(get(urlEqualTo("/api/books/timeout"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(3000) // Delay in milliseconds
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"isbn\":\"timeout\",\"title\":\"Slow Book\",\"author\":\"Slow Author\"}")));

        // Verify that the service throws an exception (timeout)
        assertThrows(Exception.class, () -> {
            bookInfoService.getBookDetails("timeout").timeout(java.time.Duration.ofMillis(100)).block();
        });
    }

    @Test
    void enrichBookWithDetails_returnsOriginalBook_whenApiFailsWithError() {
        // Set up mock response
        stubFor(get(urlEqualTo("/api/books/fail"))
                .willReturn(aResponse()
                        .withStatus(500)));

        // Create a book to enrich
        Book originalBook = new Book("fail", "Original Title", "Original Author");

        // Call the service
        Book enrichedBook = bookInfoService.enrichBookWithDetails(originalBook).block();

        // Verify that we get the original book back
        assertThat(enrichedBook).isSameAs(originalBook);
        assertThat(enrichedBook.getTitle()).isEqualTo("Original Title");
    }
}