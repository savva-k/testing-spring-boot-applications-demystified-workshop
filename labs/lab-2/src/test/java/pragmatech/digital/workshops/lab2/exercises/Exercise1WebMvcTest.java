package pragmatech.digital.workshops.lab2.exercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pragmatech.digital.workshops.lab2.config.SecurityConfig;
import pragmatech.digital.workshops.lab2.controller.BookController;
import pragmatech.digital.workshops.lab2.service.BookService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercise 1: WebMvc Test
 * <p>
 * This exercise demonstrates testing a Spring MVC controller with security using @WebMvcTest
 * <p>
 * Tasks:
 * 1. Test that only admins can delete books
 * 2. Test that regular users or unauthenticated users can't delete books
 * 3. Test that books can be created successfully with proper JSON data
 */
@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
class Exercise1WebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @Nested
  @DisplayName("DELETE /api/books/{id} endpoint tests")
  class DeleteBookTests {

    @Test
    @DisplayName("Should return 401 Unauthorized when no authentication is provided")
    void shouldReturnUnauthorizedWhenNoAuthenticationIsProvided() throws Exception {
      mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/books/123"))
        .andExpect(status().isUnauthorized());

      verify(bookService, times(0)).deleteBook(anyLong());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 Forbidden when authenticated with insufficient privileges")
    void shouldReturnForbiddenWhenAuthenticatedWithInsufficientPrivileges() throws Exception {
      mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/books/123"))
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 204 No Content when authenticated as admin and book exists")
    void shouldReturnNoContentWhenAuthenticatedAsAdminAndBookExists() throws Exception {
      when(bookService.deleteBook(anyLong())).thenReturn(true);
      mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/books/123"))
        .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 Not Found when authenticated as admin but book doesn't exist")
    void shouldReturnNotFoundWhenAuthenticatedAsAdminButBookDoesntExist() throws Exception {
      when(bookService.deleteBook(anyLong())).thenReturn(false);
      mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/books/123"))
        .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /api/books endpoint tests")
  class CreateBookTests {

    @Test
    @WithMockUser
    @DisplayName("Should return 201 Created when valid book data is provided")
    void shouldReturnCreatedWhenValidBookDataIsProvided() throws Exception {
      String validBookJson = """
        {
            "isbn": "123-1234567890",
            "title": "Test Book",
            "author": "Test Author",
            "publishedDate": "2023-01-01"
        }
        """;

      when(bookService.createBook(any())).thenReturn(43L);
      mockMvc
        .perform(
          MockMvcRequestBuilders
            .post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validBookJson)
        )
        .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 Bad Request when invalid book data is provided")
    void shouldReturnBadRequestWhenInvalidBookDataIsProvided() throws Exception {
      String validBookJson = """
        {
            "isbn": "AAAAA",
            "title": "Test Book",
            "author": "Test Author",
            "publishedDate": "1010101010"
        }
        """;
      mockMvc.perform(MockMvcRequestBuilders.post("/api/books"))
        .andExpect(status().isBadRequest());
    }
  }
}
