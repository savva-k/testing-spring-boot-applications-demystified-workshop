package pragmatech.digital.workshops.lab3.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class WireMockContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(WireMockContextInitializer.class);
    
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        
        // Register a shutdown hook to stop WireMock when the context is closed
        applicationContext.addApplicationListener(event -> {
            if (event instanceof ContextClosedEvent) {
                logger.info("Stopping WireMock server");
                wireMockServer.stop();
            }
        });
        
        // Configure the stubs
        configureStubs(wireMockServer);
        
        // Set the RestClient base URL property to point to our WireMock server
        TestPropertyValues.of(
                "book.metadata.api.url=http://localhost:" + wireMockServer.port()
        ).applyTo(applicationContext);
        
        // Add WireMockServer as a bean for direct access in tests if needed
        applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);
        
        logger.info("WireMock initialized on port {}", wireMockServer.port());
    }
    
    private void configureStubs(WireMockServer wireMockServer) {
        // WireMock will automatically load mappings from src/test/resources/mappings
        // and response files from src/test/resources/__files
        // No need to manually configure stubs here
        logger.info("WireMock will use mappings from the standard mappings directory");
    }
}