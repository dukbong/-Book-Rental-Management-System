package book.rental.management.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -2049127542L;

    public static final QMember member = new QMember("member1");

    public final book.rental.management.domain.QBase _super = new book.rental.management.domain.QBase(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<book.rental.management.domain.loan.Loan, book.rental.management.domain.loan.QLoan> loans = this.<book.rental.management.domain.loan.Loan, book.rental.management.domain.loan.QLoan>createList("loans", book.rental.management.domain.loan.Loan.class, book.rental.management.domain.loan.QLoan.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final ListPath<book.rental.management.domain.reservation.Reservation, book.rental.management.domain.reservation.QReservation> reservations = this.<book.rental.management.domain.reservation.Reservation, book.rental.management.domain.reservation.QReservation>createList("reservations", book.rental.management.domain.reservation.Reservation.class, book.rental.management.domain.reservation.QReservation.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

