package book.rental.management.repository.book.querydsl;

import book.rental.management.domain.book.Book;
import book.rental.management.domain.book.QBook;
import book.rental.management.domain.loan.QLoan;
import book.rental.management.dto.BookCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class BookQueryDslRepositoryImpl implements BookQueryDslRepository {

    private final JPAQueryFactory query;

    public BookQueryDslRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    @Override
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

    @Override
    public List<Book> getBookByRank(Pageable pageable) {
        QBook book = QBook.book;

        OrderSpecifier<?> rentalCountOrder = book.rentalCount.desc();
        OrderSpecifier<?> nameOrder = book.title.asc();
        OrderSpecifier<?> authorOrder = book.author.asc();
        OrderSpecifier<?> publisherOrder = book.publisher.asc();

        return query
                .select(book)
                .from(book)
                .orderBy(rentalCountOrder, nameOrder, authorOrder, publisherOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private Predicate titleLike(String title) {
        if(!StringUtils.hasText(title)) {
            return null;
        }
        return QBook.book.title.contains(title);
    }

    private Predicate authorLike(String author) {
        if(!StringUtils.hasText(author)) {
            return null;
        }
        return QBook.book.author.contains(author);
    }

    private Predicate publisherLike(String publisher) {
        if(!StringUtils.hasText(publisher)) {
            return null;
        }
        return QBook.book.publisher.contains(publisher);
    }


}
