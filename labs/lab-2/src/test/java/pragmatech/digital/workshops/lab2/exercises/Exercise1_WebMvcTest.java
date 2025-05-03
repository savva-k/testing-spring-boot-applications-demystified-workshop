package pragmatech.digital.workshops.lab2.exercises;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pragmatech.digital.workshops.lab2.controller.BookController;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exercise 1: Testing Web Controllers with @WebMvcTest
 *
 * In this exercise, you will learn how to test Spring MVC controllers using @WebMvcTest,
 * which loads only the web layer components and provides utilities like MockMvc for testing HTTP requests.
 *
 * Tasks:
 * 1. Set up a proper test class that uses @WebMvcTest to test the BookController.
 * 2. Mock the BookRepository dependency used by the controller.
 * 3. Write tests for the following endpoints:
 *    a. GET /api/books - should return a list of books
 *    b. GET /api/books/{isbn} - should return a specific book or 404
 *    c. POST /api/books - should create a new book
 *    d. PUT /api/books/{isbn} - should update an existing book
 * 4. Test both successful requests and error scenarios.
 * 5. Include security configuration to test authorized and unauthorized access.
 */
@WebMvcTest(BookController.class)
public class Exercise1_WebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookRepository bookRepository;
    
    // Example test to show how to structure your tests
    @Test
    @WithMockUser(roles = "ADMIN")
    void exampleTest() {
        // This test is intentionally empty but will pass
        // It demonstrates how to use @WithMockUser annotation for authorization
    }

    // TODO: Write test for GET /api/books endpoint
    
    // TODO: Write test for GET /api/books/{isbn} endpoint (found case)
    
    // TODO: Write test for GET /api/books/{isbn} endpoint (not found case)
    
    // TODO: Write test for POST /api/books endpoint (success case)
    
    // TODO: Write test for POST /api/books endpoint (book already exists case)
    
    // TODO: Write test for PUT /api/books/{isbn} endpoint (success case)
    
    // TODO: Write test for PUT /api/books/{isbn} endpoint (not found case)
    
    // TODO: Write test for unauthorized access to POST /api/books
}