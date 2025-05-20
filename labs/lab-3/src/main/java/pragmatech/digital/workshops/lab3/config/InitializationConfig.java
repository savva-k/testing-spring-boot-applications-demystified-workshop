package pragmatech.digital.workshops.lab3.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pragmatech.digital.workshops.lab3.client.OpenLibraryApiClient;
import pragmatech.digital.workshops.lab3.dto.BookMetadataDTO;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.repository.BookRepository;

import java.util.List;

/**
 * Configuration for application initialization tasks.
 */
@Configuration
public class InitializationConfig {

    private static final Logger logger = LoggerFactory.getLogger(InitializationConfig.class);

    private final OpenLibraryApiClient openLibraryApiClient;
    private final BookRepository bookRepository;

    @Autowired
    public InitializationConfig(OpenLibraryApiClient openLibraryApiClient, BookRepository bookRepository) {
        this.openLibraryApiClient = openLibraryApiClient;
        this.bookRepository = bookRepository;
    }

    /**
     * Bean to initialize book metadata from OpenLibrary during application startup.
     *
     * @return CommandLineRunner to execute during startup
     */
    @Bean
    public CommandLineRunner initializeBookMetadata() {
        return args -> {
            logger.info("Starting book metadata initialization...");

            // Sample ISBNs for well-known programming books
            List<String> sampleIsbns = List.of(
                "9780132350884",  // Clean Code by Robert C. Martin
                "9780201633610",  // Design Patterns by Gang of Four
                "9780134757599"   // Effective Java by Joshua Bloch
            );

            logger.info("Fetching metadata for {} books", sampleIsbns.size());

            for (String isbn : sampleIsbns) {
              BookMetadataDTO metadata = this.openLibraryApiClient.getBookByIsbn(isbn);
              logger.info("Fetched metadata for ISBN {} - {}", isbn, metadata);

            }

            logger.info("Book metadata initialization completed");
        };
    }
}
