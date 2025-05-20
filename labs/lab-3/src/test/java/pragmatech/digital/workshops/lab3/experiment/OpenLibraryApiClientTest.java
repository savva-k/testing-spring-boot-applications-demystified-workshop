package pragmatech.digital.workshops.lab3.experiment;

import java.io.IOException;
import java.nio.file.Files;

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
import pragmatech.digital.workshops.lab3.client.OpenLibraryApiClient;
import pragmatech.digital.workshops.lab3.dto.BookMetadataDTO;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class OpenLibraryApiClientTest {

  @RegisterExtension
  static WireMockExtension wireMockServer = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  private OpenLibraryApiClient cut;

  @BeforeEach
  void setUp() {
    WebClient webClient = WebClient.builder()
      .baseUrl(wireMockServer.baseUrl())
      .build();

    cut = new OpenLibraryApiClient(webClient);
  }

  @Nested
  @DisplayName("getBookByIsbn")
  class GetBookByIsbn {

    @Test
    @DisplayName("should return book metadata when API returns valid response")
    void shouldReturnBookMetadataWhenApiReturnsValidResponse() throws IOException {
      // Arrange
      String isbn = "9780132350884";
      String responseBody = Files.readString(
        new ClassPathResource("__files/openlibrary-book-response.json").getFile().toPath());

      wireMockServer.stubFor(
        get("/isbn/" + isbn + ".json")
          .willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(responseBody))
      );

      // Act
      BookMetadataDTO result = cut.getBookByIsbn(isbn);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.title()).isEqualTo("Clean Code: A Handbook of Agile Software Craftsmanship");
      assertThat(result.getMainIsbn()).isEqualTo("9780132350884");
      assertThat(result.getPublisher()).isEqualTo("Prentice Hall");
      assertThat(result.numberOfPages()).isEqualTo(431);
    }

    @Test
    @DisplayName("should return null when book not found")
    void shouldReturnNullWhenBookNotFound() {
      // Arrange
      String isbn = "9999999999";

      wireMockServer.stubFor(
        get("/isbn/" + isbn + ".json")
          .willReturn(aResponse()
            .withStatus(404))
      );

      // Act
      BookMetadataDTO result = cut.getBookByIsbn(isbn);

      // Assert
      assertThat(result).isNull();
    }
  }
}
