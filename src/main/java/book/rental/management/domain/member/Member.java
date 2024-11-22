package book.rental.management.domain.member;

import book.rental.management.domain.Base;
import book.rental.management.domain.book.Book;
import book.rental.management.domain.loan.Loan;
import book.rental.management.domain.loan.LoanStatus;
import book.rental.management.domain.reservation.Reservation;
import book.rental.management.response.book.BookLoanResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Base {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, length = 20, unique = true)
    private String name;
    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @Builder
    private Member(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long rentTo(Book book) {
        if (!book.isAvailableForLoan()) {
            throw new IllegalArgumentException("이미 대여 중인 책입니다.");
        }
        if (book.isReservation(this)) {
            throw new IllegalArgumentException("해당 책은 예약 대기 중이며 현재 대여 불가능합니다.");
        }
        Loan loan = Loan.createLoan(LocalDateTime.now(), LocalDateTime.now().plusDays(7), LoanStatus.ON_TIME, this, book);
        this.loans.add(loan);
        book.addLoan(loan);
        return book.getId();
    }

    public Integer reservationTo(Book book) {
        if(book.isAvailableForLoan()) {
            throw new IllegalArgumentException("예약이 필요 없는 책입니다.");
        }
        if(book.isBookAlreadyRentedByMember(this)) {
            throw new IllegalArgumentException("이미 대여 중인 책은 예약할 수 없습니다.");
        }
        if(book.isBookAlreadyReservationByMember(this)) {
            throw new IllegalArgumentException("중복 예약을 할 수 없습니다.");
        }
        Reservation reservation = Reservation.createReservation(this, book);
        this.reservations.add(reservation);
        book.addReservations(reservation);
        return reservation.getPriority();
    }

    public Long returnTo(Book book) {
        Loan loan = this.loans.stream()
                .filter(
                        l -> l.getBook().getId().equals(book.getId()) &&
                                l.getLoanStatus() == LoanStatus.ON_TIME || l.getLoanStatus() == LoanStatus.OVERDUE
                ).findFirst().orElseThrow(
                        () -> new IllegalArgumentException("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.")
                );
        loan.updateLoanStatus(LoanStatus.RETURNED);
        // 예약자에게 메일 전송
        return book.getId();
    }

    public List<BookLoanResponse> getActiveLoanSortedByReturnDate() {
        return this.loans.stream()
                .filter(Loan::isActive)
                .sorted(Comparator.comparing(Loan::getReturnDate))
                .map(l -> new BookLoanResponse(
                        l.getBook().getTitle(),
                        l.getBook().getAuthor(),
                        l.getBook().getPublisher(),
                        l.getLoanDate(),
                        l.getReturnDate(),
                        l.getLoanStatus()
                )).toList();
    }


}