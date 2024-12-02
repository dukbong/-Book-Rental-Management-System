package book.rental.management.repository.book.querydsl;

import book.rental.management.domain.book.Book;
import book.rental.management.dto.BookCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookQueryDslRepository {
    List<Book> getBookByCondition(BookCondition condition);
    List<Book> getBookByRank(Pageable pageable);

}
