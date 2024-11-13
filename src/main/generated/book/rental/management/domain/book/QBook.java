package book.rental.management.domain.book;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBook is a Querydsl query type for Book
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBook extends EntityPathBase<Book> {

    private static final long serialVersionUID = -1448644630L;

    public static final QBook book = new QBook("book");

    public final book.rental.management.domain.QBase _super = new book.rental.management.domain.QBase(this);

    public final StringPath author = createString("author");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<book.rental.management.domain.loan.Loan, book.rental.management.domain.loan.QLoan> loans = this.<book.rental.management.domain.loan.Loan, book.rental.management.domain.loan.QLoan>createList("loans", book.rental.management.domain.loan.Loan.class, book.rental.management.domain.loan.QLoan.class, PathInits.DIRECT2);

    public final StringPath publisher = createString("publisher");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QBook(String variable) {
        super(Book.class, forVariable(variable));
    }

    public QBook(Path<? extends Book> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBook(PathMetadata metadata) {
        super(Book.class, metadata);
    }

}

