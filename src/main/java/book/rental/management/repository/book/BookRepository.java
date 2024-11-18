package book.rental.management.repository.book;

import book.rental.management.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByTitleAndAuthorAndPublisher(String title, String author, String publisher);

    List<Book> findAllByOrderByTitleAsc();

}
