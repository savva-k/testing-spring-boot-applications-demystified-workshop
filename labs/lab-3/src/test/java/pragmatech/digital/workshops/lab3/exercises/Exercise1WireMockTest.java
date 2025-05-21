package pragmatech.digital.workshops.lab3.exercises;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import pragmatech.digital.workshops.lab3.client.OpenLibraryApiClient;
import pragmatech.digital.workshops.lab3.dto.BookMetadataResponse;
import wiremock.org.apache.hc.core5.http.HttpHeaders;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Exercise 1: Testing the OpenLibraryApiClient with WireMock
 * <p>
 * In this exercise, you will test the OpenLibraryApiClient using WireMock without Spring context.
 * <p>
 * Tasks:
 * 1. Create tests to verify successful API calls (status code 200)
 * 2. Create tests to verify error handling (status code 500)
 * 3. Optional: Modify the OpenLibraryApiClient implementation to handle 404 responses
 * by returning null instead of throwing an exception
 * <p>
 * Hints:
 * - You can use the WireMock JUnit 5 extension (@RegisterExtension)
 * or bootstrap the WireMock server manually
 * - Use WebClient.builder() to create a WebClient instance that points to your WireMock server
 * - Check the __files directory in test resources for sample response JSON files
 * - To modify the client for 404 handling, you'll need to use .onStatus() in the WebClient call
 */
public class Exercise1WireMockTest {

  @RegisterExtension
  static WireMockExtension wm1 = WireMockExtension
    .newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  private OpenLibraryApiClient openLibraryApiClient;

  @BeforeEach
  public void init() {
    WebClient webClient = WebClient
      .builder()
      .baseUrl(wm1.baseUrl())
      .build();

    openLibraryApiClient = new OpenLibraryApiClient(webClient);
  }

  @Test
  void shouldReturnBookMetadataWhenApiReturnsValidResponse() {
    String isbn = "9780132350884";

    wm1.stubFor(
      get("/isbn/" + isbn)
        .willReturn(aResponse()
          .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .withBodyFile(isbn + "-success.json")
        )
    );

    BookMetadataResponse bookMetadataResponse = openLibraryApiClient.getBookByIsbn(isbn);
    assertEquals("Clean Code", bookMetadataResponse.title());
  }

  @Test
  void shouldHandleServerErrorWhenApiReturns500() {
    // TODO:
    // 1. Set up WireMock server
    // 2. Configure WebClient and OpenLibraryApiClient
    // 3. Stub a 500 response for a specific ISBN
    // 4. Call the client and verify that the expected exception is thrown
  }

  @Test
  void shouldReturnNullWhenBookNotFound() {
    // TODO: (Optional - requires modifying the client)
    // 1. Set up WireMock server
    // 2. Configure WebClient and OpenLibraryApiClient
    // 3. Stub a 404 response for a specific ISBN
    // 4. Call the client and verify that null is returned
    // Note: You'll need to modify the OpenLibraryApiClient.java first
  }
}
