package pragmatech.digital.workshops.lab1.solutions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.domain.LibraryUser;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Solution for Exercise 4: Creating a JUnit 5 Extension
 */
public class Solution4_JUnit5Extensions {

    /**
     * A ParameterResolver that creates random Book instances for tests.
     */
    public static class BookParameterResolver implements ParameterResolver {
        
        private static final String[] AUTHORS = {
                "Robert C. Martin", "Martin Fowler", "Kent Beck", "Joshua Bloch",
                "Erich Gamma", "Bruce Eckel", "Brian Goetz", "Elisabeth Freeman"
        };
        
        private static final String[] TITLE_PREFIXES = {
                "Clean", "Refactoring", "Effective", "Head First",
                "Design Patterns", "Test-Driven", "Domain-Driven", "The Art of"
        };
        
        private static final String[] TITLE_SUFFIXES = {
                "Code", "Java", "Programming", "Development",
                "Architecture", "Testing", "Practices", "Software"
        };
        
        private final Random random = new Random();
        
        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) 
                throws ParameterResolutionException {
            return parameterContext.getParameter().getType() == Book.class;
        }
        
        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) 
                throws ParameterResolutionException {
            // Generate random book data
            String isbn = generateRandomIsbn();
            String title = generateRandomTitle();
            String author = AUTHORS[random.nextInt(AUTHORS.length)];
            LocalDate publishedDate = generateRandomPublishedDate();
            
            return new Book(isbn, title, author, publishedDate);
        }
        
        private String generateRandomIsbn() {
            return "978-" + (1 + random.nextInt(9)) + "-" 
                    + (10000 + random.nextInt(90000)) + "-" 
                    + (10 + random.nextInt(90)) + "-"
                    + random.nextInt(10);
        }
        
        private String generateRandomTitle() {
            return TITLE_PREFIXES[random.nextInt(TITLE_PREFIXES.length)] + " " 
                    + TITLE_SUFFIXES[random.nextInt(TITLE_SUFFIXES.length)];
        }
        
        private LocalDate generateRandomPublishedDate() {
            int year = 1990 + random.nextInt(33); // Between 1990 and 2022
            int month = 1 + random.nextInt(12);
            int day = 1 + random.nextInt(28); // Avoid issues with month lengths
            return LocalDate.of(year, month, day);
        }
    }
    
    /**
     * An extension that creates a random LibraryUser before each test.
     */
    public static class RandomUserExtension implements BeforeEachCallback, ParameterResolver, AfterEachCallback {
        
        private static final String[] FIRST_NAMES = {
                "John", "Jane", "Robert", "Emily", "Michael", "Sarah", "David", "Lisa"
        };
        
        private static final String[] LAST_NAMES = {
                "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Wilson"
        };
        
        private static final String KEY = "randomUser";
        private final Random random = new Random();
        
        @Override
        public void beforeEach(ExtensionContext context) {
            // Create a random user
            LibraryUser user = createRandomUser();
            
            // Store it in the context
            getStore(context).put(KEY, user);
        }
        
        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) 
                throws ParameterResolutionException {
            return parameterContext.getParameter().getType() == LibraryUser.class;
        }
        
        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) 
                throws ParameterResolutionException {
            return getStore(extensionContext).get(KEY, LibraryUser.class);
        }
        
        @Override
        public void afterEach(ExtensionContext context) {
            // Clean up
            getStore(context).remove(KEY);
        }
        
        private ExtensionContext.Store getStore(ExtensionContext context) {
            return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
        }
        
        private LibraryUser createRandomUser() {
            String id = UUID.randomUUID().toString();
            String name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " 
                    + LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = name.toLowerCase().replace(" ", ".") + "@example.com";
            String membershipNumber = "LIB-" + (1000 + random.nextInt(9000));
            LocalDate memberSince = LocalDate.now().minusDays(random.nextInt(365 * 3)); // Up to 3 years ago
            
            return new LibraryUser(id, name, email, membershipNumber, memberSince);
        }
    }
    
    // Tests that demonstrate the use of these extensions
    
    @Test
    @DisplayName("BookParameterResolver should provide a random Book")
    @ExtendWith(BookParameterResolver.class)
    void bookParameterResolverShouldProvideRandomBook(Book book) {
        // The Book parameter is automatically provided by our extension
        
        // Assert that the book has valid data
        assertNotNull(book);
        assertNotNull(book.getIsbn());
        assertNotNull(book.getTitle());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getPublishedDate());
        
        // Print the book for demonstration
        System.out.println("Random book: " + book);
    }
    
    @Test
    @DisplayName("RandomUserExtension should provide a random LibraryUser")
    @ExtendWith(RandomUserExtension.class)
    void randomUserExtensionShouldProvideRandomUser(LibraryUser user) {
        // The LibraryUser parameter is automatically provided by our extension
        
        // Assert that the user has valid data
        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getName());
        assertNotNull(user.getEmail());
        assertNotNull(user.getMembershipNumber());
        assertNotNull(user.getMemberSince());
        
        // Print the user for demonstration
        System.out.println("Random user: " + user);
    }
    
    @Test
    @DisplayName("Both extensions can be used together")
    @ExtendWith({BookParameterResolver.class, RandomUserExtension.class})
    void bothExtensionsCanBeUsedTogether(Book book, LibraryUser user) {
        // Both parameters are automatically provided by our extensions
        
        assertNotNull(book);
        assertNotNull(user);
        
        // Print both for demonstration
        System.out.println("Random book: " + book);
        System.out.println("Random user: " + user);
    }
}