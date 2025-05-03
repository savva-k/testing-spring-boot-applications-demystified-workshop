package pragmatech.digital.workshops.lab4.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pragmatech.digital.workshops.lab4.controller.BookController;
import pragmatech.digital.workshops.lab4.entity.Book;
import pragmatech.digital.workshops.lab4.service.BookService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This test class demonstrates how to avoid common Spring Boot testing pitfalls
 */
@WebMvcTest(BookController.class)  // Use @WebMvcTest instead of @SpringBootTest for controller tests
public class AvoidCommonPitfallsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    // Use field setup for shared test data
    private List<Book> testBooks;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Set up common test data
        testBook = new Book("123", "Test Book", "Test Author");
        testBooks = Arrays.asList(
                testBook,
                new Book("456", "Another Book", "Another Author")
        );

        // Reset mocks to ensure isolation between tests
        reset(bookService);
    }

    @Nested
    class GetBookTests {
        @Test
        void getBookByIsbn_returnsBook_whenFound() throws Exception {
            // Given
            when(bookService.findByIsbn("123")).thenReturn(Optional.of(testBook));

            // When & Then
            mockMvc.perform(get("/api/books/123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isbn", is("123")))
                    .andExpect(jsonPath("$.title", is("Test Book")));
        }

        @Test
        void getBookByIsbn_returnsNotFound_whenMissing() throws Exception {
            // Given
            when(bookService.findByIsbn("999")).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/books/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetAllBooksTests {
        @Test
        void getAllBooks_returnsAllBooks() throws Exception {
            // Given
            when(bookService.findAllAvailableBooks()).thenReturn(testBooks);

            // When & Then
            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].isbn", is("123")))
                    .andExpect(jsonPath("$[1].isbn", is("456")));
        }
    }

    @Nested
    class CreateBookTests {
        @Test
        void createBook_returnsCreatedBook_whenSuccessful() throws Exception {
            // Given
            when(bookService.createBook(any(Book.class))).thenReturn(testBook);

            // When & Then
            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"isbn\":\"123\",\"title\":\"Test Book\",\"author\":\"Test Author\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isbn", is("123")));
        }

        @Test
        void createBook_returnsBadRequest_whenDuplicate() throws Exception {
            // Given
            when(bookService.createBook(any(Book.class))).thenThrow(new IllegalStateException("Duplicate book"));

            // When & Then
            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"isbn\":\"123\",\"title\":\"Test Book\",\"author\":\"Test Author\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // Clean up any shared state if needed
    @AfterEach
    void tearDown() {
        // No cleanup needed here as we're using MockBeans
        // For database tests, we would clear the database here
    }
}