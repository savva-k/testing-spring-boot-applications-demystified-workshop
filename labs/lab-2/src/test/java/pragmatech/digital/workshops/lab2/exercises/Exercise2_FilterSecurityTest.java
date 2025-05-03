package pragmatech.digital.workshops.lab2.exercises;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pragmatech.digital.workshops.lab2.config.RequestLoggingFilter;
import pragmatech.digital.workshops.lab2.controller.BookController;
import pragmatech.digital.workshops.lab2.repository.BookRepository;

/**
 * Exercise 2: Testing Filters and Security
 *
 * In this exercise, you will learn how to test filters and security configuration in a Spring Boot application.
 * You'll test the RequestLoggingFilter to verify that it properly logs request information,
 * and you'll test the security configuration to ensure proper access control.
 *
 * Tasks:
 * 1. Write tests for the RequestLoggingFilter to verify it logs request information.
 * 2. Test the security configuration to verify:
 *    a. Public endpoints are accessible without authentication
 *    b. Protected endpoints require proper authentication and roles
 *    c. Different roles have different levels of access
 * 3. Use appropriate testing annotations and utilities from Spring Security Test.
 */
@WebMvcTest({BookController.class, RequestLoggingFilter.class})
public class Exercise2_FilterSecurityTest {

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
    
    // TODO: Write test to verify RequestLoggingFilter logs request information
    
    // TODO: Write test for public endpoint (GET /api/books) access without authentication
    
    // TODO: Write test for protected endpoint (POST /api/books) requiring ADMIN role
    
    // TODO: Write test for endpoint requiring LIBRARIAN or ADMIN role
    
    // TODO: Write test for endpoint requiring USER role
    
    // TODO: Write test to verify unauthorized access is properly rejected
}