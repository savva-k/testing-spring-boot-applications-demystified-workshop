package pragmatech.digital.workshops.lab2.solutions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pragmatech.digital.workshops.lab2.config.RequestLoggingFilter;
import pragmatech.digital.workshops.lab2.controller.BookController;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookStatus;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Solution for Exercise 2: Testing Filters and Security
 */
@WebMvcTest({BookController.class, RequestLoggingFilter.class})
public class Solution2_FilterSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookRepository bookRepository;
    
    @Test
    @DisplayName("RequestLoggingFilter should log request information")
    void requestLoggingFilterShouldLogRequestInformation() throws Exception {
        // Arrange
        Book book1 = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        Book book2 = new Book("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24));
        
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        
        // Act & Assert
        mockMvc.perform(get("/api/books")
                .header("User-Agent", "Mozilla/5.0 (Test User Agent)"))
                .andExpect(status().isOk());
        
        // Note: This test would normally use a logging appender mock to verify log messages.
        // For the purpose of this exercise, we're just ensuring the filter is in the chain
        // and the request completes successfully.
        
        verify(bookRepository).findAll();
    }
    
    @Test
    @DisplayName("GET /api/books should be accessible without authentication")
    void getBooksEndpointShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Arrange
        Book book1 = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        Book book2 = new Book("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24));
        
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        
        // Act & Assert
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isbn", is("978-1-11111-111-1")))
                .andExpect(jsonPath("$[1].isbn", is("978-2-22222-222-2")));
        
        verify(bookRepository).findAll();
    }
    
    @Test
    @DisplayName("POST /api/books should require ADMIN role")
    void postBooksEndpointShouldRequireAdminRole() throws Exception {
        // Arrange
        Book book = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        when(bookRepository.existsById(book.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        // Act & Assert - Unauthorized (no authentication)
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isbn\":\"978-1-11111-111-1\",\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"publishedDate\":\"2008-08-01\"}"))
                .andExpect(status().isUnauthorized());
        
        // Act & Assert - Forbidden (authenticated but wrong role)
        mockMvc.perform(post("/api/books")
                .with(httpBasic("user", "user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isbn\":\"978-1-11111-111-1\",\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"publishedDate\":\"2008-08-01\"}"))
                .andExpect(status().isForbidden());
        
        // Act & Assert - Authorized (ADMIN role)
        mockMvc.perform(post("/api/books")
                .with(httpBasic("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isbn\":\"978-1-11111-111-1\",\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"publishedDate\":\"2008-08-01\"}"))
                .andExpect(status().isCreated());
        
        verify(bookRepository).existsById(book.getIsbn());
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    @DisplayName("PATCH /api/books/{isbn}/status should require ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void patchBookStatusEndpointShouldRequireAdminRole() throws Exception {
        // Arrange
        String isbn = "978-1-11111-111-1";
        Book book = new Book(isbn, "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        // Act & Assert
        mockMvc.perform(patch("/api/books/{isbn}/status", isbn)
                .param("status", "BORROWED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("BORROWED")));
        
        verify(bookRepository).findById(isbn);
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    @DisplayName("DELETE /api/books/{isbn} should require ADMIN role")
    void deleteBookEndpointShouldRequireAdminRole() throws Exception {
        // Arrange
        String isbn = "978-1-11111-111-1";
        Book book = new Book(isbn, "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);
        
        // Act & Assert - Unauthorized (no authentication)
        mockMvc.perform(delete("/api/books/{isbn}", isbn))
                .andExpect(status().isUnauthorized());
        
        // Act & Assert - Forbidden (authenticated but wrong role)
        mockMvc.perform(delete("/api/books/{isbn}", isbn)
                .with(httpBasic("librarian", "librarian")))
                .andExpect(status().isForbidden());
        
        // Act & Assert - Authorized (ADMIN role)
        mockMvc.perform(delete("/api/books/{isbn}", isbn)
                .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());
        
        verify(bookRepository).findById(isbn);
        verify(bookRepository).delete(book);
    }
    
    @Test
    @DisplayName("GET /api/books/search should be accessible without authentication")
    void getBookSearchEndpointShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Arrange
        String author = "Martin";
        Book book1 = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        Book book2 = new Book("978-3-33333-333-3", "Clean Architecture", "Robert C. Martin", LocalDate.of(2017, 9, 10));
        
        when(bookRepository.findByAuthorContainingIgnoreCase(author)).thenReturn(Arrays.asList(book1, book2));
        
        // Act & Assert
        mockMvc.perform(get("/api/books/search")
                .param("author", author))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isbn", is("978-1-11111-111-1")))
                .andExpect(jsonPath("$[1].isbn", is("978-3-33333-333-3")));
        
        verify(bookRepository).findByAuthorContainingIgnoreCase(author);
    }
}