package pragmatech.digital.workshops.lab3.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pragmatech.digital.workshops.lab3.dto.BookMetadataResponse;
import reactor.core.publisher.Mono;

/**
 * Client for interacting with the OpenLibrary API.
 */
@Component
public class OpenLibraryApiClient {

  private static final Logger logger = LoggerFactory.getLogger(OpenLibraryApiClient.class);

  private final WebClient webClient;

  @Autowired
  public OpenLibraryApiClient(WebClient openLibraryWebClient) {
    this.webClient = openLibraryWebClient;
  }

  /**
   * Get book metadata from OpenLibrary by ISBN.
   *
   * @param isbn The ISBN of the book
   * @return BookMetadataDTO or null if not found
   */
  public BookMetadataResponse getBookByIsbn(String isbn) {
    return webClient.get()
      .uri("/isbn/{isbn}", isbn)
      .retrieve()
      .bodyToMono(BookMetadataResponse.class)
      .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
        logger.warn("Book not found for ISBN: {}", isbn);
        return Mono.empty();
      })
      .onErrorResume(Exception.class, ex -> {
        logger.error("Error fetching book metadata for ISBN: {}", isbn, ex);
        return Mono.empty();
      })
      .block();
  }
}
