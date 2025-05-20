package pragmatech.digital.workshops.lab1.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import pragmatech.digital.workshops.lab1.domain.Book;

@Repository
public interface BookRepository {

  Book save(Book book);
  Optional<Book> findByIsbn(String isbn);
}
