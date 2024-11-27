package book.rental.management.repository.book;

import book.rental.management.domain.book.Book;
import book.rental.management.dto.BookCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookQueryDslRepositoryTest {

    @Autowired
    BookQueryDslRepository bookQueryDslRepository;
    @Autowired
    BookRepository bookRepository;

    @Test
    void containsTest() {
        // given
        Book book1 = Book.builder()
                .title("test1")
                .author("test1-a")
                .publisher("test1-p")
                .build();
        Book book2 = Book.builder()
                .title("apple")
                .author("apple-a")
                .publisher("apple-p")
                .build();
        bookRepository.save(book1);
        bookRepository.save(book2);

        BookCondition condition = new BookCondition("test1",null, null);

        // when
        List<Book> result = bookQueryDslRepository.getBookByCondition(null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("test1");
    }

}