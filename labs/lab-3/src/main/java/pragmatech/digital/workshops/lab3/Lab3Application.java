package pragmatech.digital.workshops.lab3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pragmatech.digital.workshops.lab3.dto.BookMetadataDTO;
import pragmatech.digital.workshops.lab3.entity.Book;
import pragmatech.digital.workshops.lab3.repository.BookRepository;
import pragmatech.digital.workshops.lab3.service.BookMetadataService;

import java.util.List;

@SpringBootApplication
public class Lab3Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }
    
    @Bean
    public CommandLineRunner initializeBookMetadata(BookMetadataService bookMetadataService, BookRepository bookRepository) {
        return args -> {
            // Sample ISBNs for well-known programming books
            List<String> sampleIsbns = List.of(
                "9780132350884",  // Clean Code by Robert C. Martin
                "9780201633610",  // Design Patterns by Gang of Four
                "9780134757599"   // Effective Java by Joshua Bloch
            );
            
            System.out.println("\n===== Fetching OpenLibrary Book Metadata During Startup =====");
            
            for (String isbn : sampleIsbns) {
                BookMetadataDTO metadata = bookMetadataService.getBookMetadata(isbn);
                
                if (metadata != null) {
                    System.out.println("\nISBN: " + metadata.getMainIsbn());
                    System.out.println("Title: " + metadata.title());
                    System.out.println("Publisher: " + metadata.getPublisher());
                    System.out.println("Publish Date: " + metadata.publishDate());
                    
                    // For the first book (Clean Code), enrich and save to database if it doesn't exist
                    if (isbn.equals("9780132350884") && !bookRepository.existsById(isbn)) {
                        Book book = new Book();
                        book.setIsbn(isbn);
                        Book enrichedBook = bookMetadataService.enrichBookWithMetadata(book);
                        bookRepository.save(enrichedBook);
                        System.out.println("* Saved to database *");
                    }
                } else {
                    System.out.println("\nCould not fetch book metadata for ISBN: " + isbn);
                }
            }
            
            System.out.println("\n=== OpenLibrary API Integration Ready ===");
        };
    }
}