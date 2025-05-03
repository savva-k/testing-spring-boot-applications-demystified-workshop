package pragmatech.digital.workshops.lab4.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pragmatech.digital.workshops.lab4.entity.Book;
import reactor.core.publisher.Mono;

@Service
public class BookInfoService {
    
    private final WebClient webClient;
    
    @Autowired
    public BookInfoService(WebClient.Builder webClientBuilder, 
                           @Value("${book.info.service.url:http://localhost:8080}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }
    
    public Mono<Book> getBookDetails(String isbn) {
        return webClient.get()
                .uri("/api/books/{isbn}", isbn)
                .retrieve()
                .bodyToMono(Book.class);
    }
    
    public Mono<Book> enrichBookWithDetails(Book book) {
        return getBookDetails(book.getIsbn())
                .map(detailedBook -> {
                    // Only update certain fields if they're empty in our book
                    if (book.getPublishedDate() == null) {
                        book.setPublishedDate(detailedBook.getPublishedDate());
                    }
                    return book;
                })
                .onErrorReturn(book); // If external service fails, return original book
    }
}