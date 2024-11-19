package book.rental.management.support;

import book.rental.management.repository.book.BookRepository;
import book.rental.management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    protected BookService bookService;
    @Autowired
    protected BookRepository bookRepository;
}
