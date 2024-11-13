package book.rental.management.repository.book;

import book.rental.management.domain.book.Book;
import book.rental.management.domain.book.QBook;
import book.rental.management.domain.loan.QLoan;
import book.rental.management.request.book.BookCondition;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class BookQueryDslRepository {

    private final JPAQueryFactory query;

    public BookQueryDslRepository(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    public List<Book> getBookByCondition(BookCondition condition) {
        QBook book = QBook.book;
        QLoan loan = QLoan.loan;

        return query
                .select(book)
                .from(book)
                .leftJoin(book.loans, loan)
                .where(
                        titleLike(condition.getTitle()),
                        authorLike(condition.getAuthor()),
                        publisherLike(condition.getPublisher())
                )
                .orderBy(book.title.asc())
                .fetch();
    }

    private Predicate titleLike(String title) {
        if(!StringUtils.hasText(title)) {
            return null;
        }
        return QBook.book.title.like("%" + title + "%");
    }

    private Predicate authorLike(String author) {
        if(!StringUtils.hasText(author)) {
            return null;
        }
        return QBook.book.author.like("%" + author + "%");
    }

    private Predicate publisherLike(String publisher) {
        if(!StringUtils.hasText(publisher)) {
            return null;
        }
        return QBook.book.publisher.like("%" + publisher + "%");
    }


    public List<Book> findByIdWithLoan(Long bookId) {
        QBook book = QBook.book;
        QLoan loan = QLoan.loan;

        return query
                .select(book)
                .from(book)
                .leftJoin(book.loans, loan)
                .where(
                        book.id.eq(bookId)
                )
                .fetch();
    }
}
