package pragmatech.digital.workshops.lab2.solutions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import pragmatech.digital.workshops.lab2.controller.BookController;
import pragmatech.digital.workshops.lab2.entity.Book;
import pragmatech.digital.workshops.lab2.entity.BookStatus;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Solution for Exercise 1: Testing Web Controllers with @WebMvcTest
 */
@WebMvcTest(BookController.class)
public class Solution1_WebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BookRepository bookRepository;
    
    @Test
    @DisplayName("GET /api/books should return all books")
    @WithMockUser
    void getAllBooksReturnsListOfBooks() throws Exception {
        // Arrange
        Book book1 = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        Book book2 = new Book("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24));
        List<Book> books = Arrays.asList(book1, book2);
        
        when(bookRepository.findAll()).thenReturn(books);
        
        // Act & Assert
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isbn", is("978-1-11111-111-1")))
                .andExpect(jsonPath("$[0].title", is("Clean Code")))
                .andExpect(jsonPath("$[1].isbn", is("978-2-22222-222-2")))
                .andExpect(jsonPath("$[1].title", is("Effective Java")));
        
        verify(bookRepository).findAll();
    }
    
    @Test
    @DisplayName("GET /api/books/{isbn} should return book when found")
    void getBookByIsbnReturnsBookWhenFound() throws Exception {
        // Arrange
        String isbn = "978-1-11111-111-1";
        Book book = new Book(isbn, "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(book));
        
        // Act & Assert
        mockMvc.perform(get("/api/books/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isbn", is(isbn)))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert C. Martin")))
                .andExpect(jsonPath("$.publishedDate", is("2008-08-01")))
                .andExpect(jsonPath("$.status", is("AVAILABLE")));
        
        verify(bookRepository).findById(isbn);
    }
    
    @Test
    @DisplayName("GET /api/books/{isbn} should return 404 when book not found")
    void getBookByIsbnReturns404WhenNotFound() throws Exception {
        // Arrange
        String isbn = "non-existent-isbn";
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/books/{isbn}", isbn))
                .andExpect(status().isNotFound());
        
        verify(bookRepository).findById(isbn);
    }
    
    @Test
    @DisplayName("POST /api/books should create a new book when authorized as admin")
    @WithMockUser(roles = "ADMIN")
    void createBookCreatesNewBookWhenAuthorizedAsAdmin() throws Exception {
        // Arrange
        Book book = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        when(bookRepository.existsById(book.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        // Act & Assert
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn", is(book.getIsbn())))
                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.author", is(book.getAuthor())));
        
        verify(bookRepository).existsById(book.getIsbn());
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    @DisplayName("POST /api/books should return 400 when book already exists")
    @WithMockUser(roles = "ADMIN")
    void createBookReturns400WhenBookAlreadyExists() throws Exception {
        // Arrange
        Book book = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        when(bookRepository.existsById(book.getIsbn())).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest());
        
        verify(bookRepository).existsById(book.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    @DisplayName("PUT /api/books/{isbn} should update book when found")
    @WithMockUser(roles = "ADMIN")
    void updateBookUpdatesBookWhenFound() throws Exception {
        // Arrange
        String isbn = "978-1-11111-111-1";
        Book existingBook = new Book(isbn, "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        Book updatedBook = new Book(isbn, "Clean Code (Updated)", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        updatedBook.setStatus(BookStatus.BORROWED);
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        
        // Act & Assert
        mockMvc.perform(put("/api/books/{isbn}", isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", is(isbn)))
                .andExpect(jsonPath("$.title", is(updatedBook.getTitle())))
                .andExpect(jsonPath("$.status", is("BORROWED")));
        
        verify(bookRepository).findById(isbn);
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    @DisplayName("PUT /api/books/{isbn} should return 404 when book not found")
    @WithMockUser(roles = "ADMIN")
    void updateBookReturns404WhenNotFound() throws Exception {
        // Arrange
        String isbn = "non-existent-isbn";
        Book book = new Book(isbn, "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        when(bookRepository.findById(isbn)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(put("/api/books/{isbn}", isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound());
        
        verify(bookRepository).findById(isbn);
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    @DisplayName("POST /api/books should return 403 when not authorized as admin")
    @WithMockUser(roles = "USER")
    void createBookReturns403WhenNotAuthorizedAsAdmin() throws Exception {
        // Arrange
        Book book = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        // Act & Assert
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isForbidden());
        
        verify(bookRepository, never()).existsById(anyString());
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    @DisplayName("POST /api/books should return 401 when not authenticated")
    void createBookReturns401WhenNotAuthenticated() throws Exception {
        // Arrange
        Book book = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        
        // Act & Assert
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isUnauthorized());
        
        verify(bookRepository, never()).existsById(anyString());
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    @DisplayName("GET /api/books/available should return available books")
    void getAvailableBooksReturnsBooksByStatus() throws Exception {
        // Arrange
        Book book1 = new Book("978-1-11111-111-1", "Clean Code", "Robert C. Martin", LocalDate.of(2008, 8, 1));
        Book book2 = new Book("978-2-22222-222-2", "Effective Java", "Joshua Bloch", LocalDate.of(2017, 10, 24));
        List<Book> availableBooks = Arrays.asList(book1, book2);
        
        when(bookRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(availableBooks);
        
        // Act & Assert
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isbn", is("978-1-11111-111-1")))
                .andExpect(jsonPath("$[1].isbn", is("978-2-22222-222-2")));
        
        verify(bookRepository).findByStatus(BookStatus.AVAILABLE);
    }
}