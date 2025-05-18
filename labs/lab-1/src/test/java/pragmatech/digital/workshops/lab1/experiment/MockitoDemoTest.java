package pragmatech.digital.workshops.lab1.experiment;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pragmatech.digital.workshops.lab1.domain.Book;
import pragmatech.digital.workshops.lab1.service.BookService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockitoDemoTest {

  @Mock
  private BookService bookService;

  @Test
  void shouldReturnBookWhenFound() {
    // Arrange
    Book expectedTestBook = new Book("1234", "Test Book", "Author", LocalDate.now());
    when(bookService.findByIsbn("1234")).thenReturn(Optional.of(expectedTestBook));

    // Act
    Optional<Book> result = bookService.findByIsbn("1234");

    // Assert
    assertTrue(result.isPresent());
    assertEquals(expectedTestBook, result.get());
    verify(bookService).findByIsbn("1234");
  }
}
