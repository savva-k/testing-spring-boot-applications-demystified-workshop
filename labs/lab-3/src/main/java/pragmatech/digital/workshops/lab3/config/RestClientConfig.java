package pragmatech.digital.workshops.lab3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class RestClientConfig {

  @Bean
  public WebClient openLibraryWebClient(
    @Value("${book.metadata.api.url:https://openlibrary.org}") String baseUrl,
    @Value("${book.metadata.api.timeout:5}") int timeoutSeconds) {

    return WebClient.builder()
      .baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .defaultHeader(HttpHeaders.USER_AGENT, "SPRNG-IO")
      .filter(logRequest())
      .codecs(configurer -> configurer
        .defaultCodecs()
        .maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer for larger responses
      .build();
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
      return Mono.just(clientRequest);
    });
  }
}
