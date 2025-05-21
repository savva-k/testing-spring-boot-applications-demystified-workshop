package pragmatech.digital.workshops.lab1.exercises;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.repository.BookRepository;
import pragmatech.digital.workshops.lab1.service.BookAlreadyExistsException;
import pragmatech.digital.workshops.lab1.service.BookService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Exercise 1: Write unit tests for the BookService class using Mockito
 * <p>
 * Tasks:
 * 1. Complete the test method for checking that an exception is thrown when
 * trying to add a book with an ISBN that already exists
 * 2. Complete the test method for the happy path of creating a new book
 * <p>
 * Guidelines:
 * - Use Mockito to mock the BookRepository
 * - Structure tests using the Arrange-Act-Assert pattern
 * - Use assertThrows for testing exceptions
 */

@ExtendWith(MockitoExtension.class)
class Exercise1UnitTest {

  @Mock
  private BookRepository bookRepository;

  @Test
  void shouldThrowExceptionWhenBookWithIsbnAlreadyExists() {
    when(bookRepository.findByIsbn("123")).thenThrow(BookAlreadyExistsException.class);
    BookService bookService = new BookService(bookRepository);
    assertThrows(BookAlreadyExistsException.class, () -> bookService.create("123", "Title", "Book"));

  }

  @Test
  void shouldCreateBookWhenIsbnDoesNotExist() {
    String actualIsbn = "123";
    long actualBookId = 999;
      when(bookRepository.findByIsbn(actualIsbn)).thenReturn(Optional.empty());
      when(bookRepository.save(any(Book.class))).thenAnswer(a -> {
        Book book = (Book) a.getArguments()[0];
        book.setId(actualBookId);
        return book;
      });
      BookService bookService = new BookService(bookRepository);
      long bookId = bookService.create(actualIsbn, "Title", "Author");
      assertEquals(actualBookId, bookId);
  }
}
