package book.rental.management.domain.book;

import book.rental.management.domain.Base;
import book.rental.management.domain.loan.Loan;
import book.rental.management.domain.member.Member;
import book.rental.management.domain.reservation.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        // 3가지 모두 동일한 책은 입력할 수 없다.
                        columnNames = {"title", "author", "publisher"}
                )
        }
)
public class Book extends Base {
    @Id @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private String publisher;

    private int rentalCount;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @Builder
    private Book(String title, String author, String publisher) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

    public boolean isAvailableForLoan() {
        return this.loans.stream().noneMatch(Loan::isActive);
    }

    public boolean isReservation(Member member) {
        boolean isCancelled = this.reservations.stream()
                .anyMatch(reservation -> reservation.cancelIfAvailable(member));
        if (isCancelled) {
            return false;
        }
        return this.reservations.stream().anyMatch(Reservation::isReservation);
    }

    public boolean isBookAlreadyRentedByMember(Member member) {
        return this.loans.stream().anyMatch(loan -> loan.isRentedByMember(member));
    }

    public boolean isBookAlreadyReservationByMember(Member member) {
        return this.reservations.stream().anyMatch(reservation -> reservation.isReservationByMember(member));
    }

    public void addLoan(Loan loan) {
        this.loans.add(loan);
        increaseRentalCount();
    }

    public void increaseRentalCount() {
        this.rentalCount++;
    }

    public void addReservations(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public Integer getReservationPriority() {
        if(this.reservations.isEmpty()) {
            return 1;
        }
        return this.reservations.get(this.reservations.size() -1).getPriority() + 1;
    }



}
