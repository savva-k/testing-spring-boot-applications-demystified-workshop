package pragmatech.digital.workshops.lab3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pragmatech.digital.workshops.lab3.dto.BookMetadataDTO;
import pragmatech.digital.workshops.lab3.entity.Book;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

@Service
public class BookMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(BookMetadataService.class);
    
    private final WebClient webClient;
    
    @Autowired
    public BookMetadataService(WebClient openLibraryWebClient) {
        this.webClient = openLibraryWebClient;
    }
    
    public BookMetadataDTO getBookMetadata(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }
        
        String cleanIsbn = isbn.replaceAll("[^0-9X]", ""); // Remove any non-digit characters except X
        
        try {
            return webClient.get()
                    .uri("/isbn/{isbn}.json", cleanIsbn)
                    .retrieve()
                    .bodyToMono(BookMetadataDTO.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                        logger.warn("Book not found for ISBN: {}", cleanIsbn);
                        return Mono.empty();
                    })
                    .onErrorResume(Exception.class, ex -> {
                        logger.error("Error fetching book metadata for ISBN: {}", cleanIsbn, ex);
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Error fetching book metadata for ISBN: {}", cleanIsbn, e);
            return null;
        }
    }
    
    public Book enrichBookWithMetadata(Book book) {
        if (book == null || book.getIsbn() == null) {
            return book;
        }
        
        BookMetadataDTO metadata = getBookMetadata(book.getIsbn());
        
        if (metadata == null) {
            return book;
        }
        
        // Only update fields that are not already set
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            book.setTitle(metadata.title());
        }
        
        if (metadata.description() != null && (book.getDescription() == null || book.getDescription().isEmpty())) {
            book.setDescription(metadata.description());
        }
        
        if (metadata.getPublisher() != null && (book.getPublisher() == null || book.getPublisher().isEmpty())) {
            book.setPublisher(metadata.getPublisher());
        }
        
        if (book.getPublishedDate() == null && metadata.publishDate() != null) {
            book.setPublishedDate(parsePublishDate(metadata.publishDate()));
        }
        
        if (metadata.getCoverUrl() != null && (book.getThumbnailUrl() == null || book.getThumbnailUrl().isEmpty())) {
            book.setThumbnailUrl(metadata.getCoverUrl());
        }
        
        if (metadata.numberOfPages() != null && book.getPageCount() == null) {
            book.setPageCount(metadata.numberOfPages());
        }
        
        // Extract author from author references if available
        if (metadata.authorRefs() != null && !metadata.authorRefs().isEmpty() 
                && (book.getAuthor() == null || book.getAuthor().isEmpty())) {
            // For now, just use the first author reference's name if available
            // In a real app, we'd make another call to fetch author details
            var authorRef = metadata.authorRefs().get(0);
            if (authorRef.containsKey("name")) {
                book.setAuthor(authorRef.get("name"));
            }
        }
        
        return book;
    }
    
    private LocalDate parsePublishDate(String publishDateStr) {
        if (publishDateStr == null || publishDateStr.isEmpty()) {
            return null;
        }
        
        try {
            // Try parsing with common date formats
            String[] patterns = {
                "MMMM d, yyyy", 
                "MMM d, yyyy",
                "MMMM yyyy", 
                "MMM yyyy", 
                "yyyy",
                "MMMM d yyyy",
                "MMM d yyyy"
            };
            
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
                    return LocalDate.parse(publishDateStr, formatter);
                } catch (DateTimeParseException e) {
                    // Try the next pattern
                }
            }
            
            // If we can't parse the date, extract the year at least
            String yearStr = publishDateStr.replaceAll("[^0-9]", " ")
                    .trim()
                    .replaceAll("\\s+", " ");
            String[] parts = yearStr.split(" ");
            if (parts.length > 0) {
                // Assume the largest 4-digit number is the year
                for (String part : parts) {
                    if (part.length() == 4) {
                        return LocalDate.of(Integer.parseInt(part), 1, 1);
                    }
                }
            }
            
            // Default fallback
            return null;
        } catch (Exception e) {
            logger.warn("Failed to parse publish date: {}", publishDateStr, e);
            return null;
        }
    }
}