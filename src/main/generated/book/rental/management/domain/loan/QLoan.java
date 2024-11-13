package book.rental.management.domain.loan;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLoan is a Querydsl query type for Loan
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLoan extends EntityPathBase<Loan> {

    private static final long serialVersionUID = -1797284790L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLoan loan = new QLoan("loan");

    public final book.rental.management.domain.QBase _super = new book.rental.management.domain.QBase(this);

    public final book.rental.management.domain.book.QBook book;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> loanDate = createDateTime("loanDate", java.time.LocalDateTime.class);

    public final EnumPath<LoanStatus> loanStatus = createEnum("loanStatus", LoanStatus.class);

    public final book.rental.management.domain.member.QMember member;

    public final DateTimePath<java.time.LocalDateTime> returnDate = createDateTime("returnDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QLoan(String variable) {
        this(Loan.class, forVariable(variable), INITS);
    }

    public QLoan(Path<? extends Loan> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLoan(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLoan(PathMetadata metadata, PathInits inits) {
        this(Loan.class, metadata, inits);
    }

    public QLoan(Class<? extends Loan> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new book.rental.management.domain.book.QBook(forProperty("book")) : null;
        this.member = inits.isInitialized("member") ? new book.rental.management.domain.member.QMember(forProperty("member")) : null;
    }

}

