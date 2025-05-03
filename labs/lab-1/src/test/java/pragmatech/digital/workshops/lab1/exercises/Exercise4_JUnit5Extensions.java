package pragmatech.digital.workshops.lab1.exercises;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Exercise 4: Creating a JUnit 5 Extension
 *
 * In this exercise, you'll learn how to create custom JUnit 5 extensions
 * to enhance your tests and reduce boilerplate code.
 *
 * Tasks:
 * 1. Implement a BookParameterResolver extension that automatically provides Book objects to tests.
 * 2. Implement a RandomUserExtension that creates a random LibraryUser for each test.
 * 3. Write tests that demonstrate the use of these extensions.
 */
public class Exercise4_JUnit5Extensions {

    /**
     * TODO: Implement a BookParameterResolver that resolves parameters of type Book
     * with randomly generated book data. 
     * 
     * This extension should:
     * - Check if the parameter is of type Book
     * - Generate a Book with random but valid data (ISBN, title, author, published date)
     * - Return the generated Book
     */
    public static class BookParameterResolver implements ParameterResolver {
        
        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) 
                throws ParameterResolutionException {
            // TODO: Implement this method
            return false;
        }
        
        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) 
                throws ParameterResolutionException {
            // TODO: Implement this method
            return null;
        }
    }
    
    /**
     * TODO: Implement a RandomUserExtension that:
     * - Creates a random LibraryUser before each test
     * - Stores it in the ExtensionContext store
     * - Can be injected as a parameter in tests
     * - Is cleaned up after each test
     */
    
    // TODO: Write tests that demonstrate the use of these extensions
}