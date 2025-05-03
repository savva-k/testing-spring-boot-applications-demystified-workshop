package pragmatech.digital.workshops.lab4.exercises;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Exercise 1: Testing with WebClient and WebTestClient
 *
 * In this exercise, you will learn how to test applications that use WebClient
 * for making HTTP requests to external services. You'll use WebTestClient
 * to test WebFlux-based endpoints, and WireMock to mock external HTTP services.
 *
 * Scenario:
 * The library system needs to integrate with an external book information service
 * to retrieve additional details about books. This integration is implemented
 * using WebClient. Your task is to test this integration properly.
 *
 * Tasks:
 * 1. Create a test for a service that uses WebClient to fetch book information.
 * 2. Use WireMock to mock the external book information service.
 * 3. Test different scenarios like successful responses, errors, and timeouts.
 * 4. Use WebTestClient to test your own reactive endpoints.
 * 5. Implement proper error handling and test it.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class Exercise1_WebClientTesting {

    @LocalServerPort
    private int port;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    // Example test that will pass
    @Test
    void contextLoads() {
        // This test just verifies that the spring context loads correctly
    }
    
    // TODO: Set up WireMock stubs
    
    // TODO: Create test for fetching book information from external service
    
    // TODO: Test successful response scenario
    
    // TODO: Test error response scenario
    
    // TODO: Test timeout scenario
    
    // TODO: Use WebTestClient to test your own endpoints
}