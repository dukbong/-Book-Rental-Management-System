package book.rental.management.domain.reservation;

import book.rental.management.domain.Base;
import book.rental.management.domain.book.Book;
import book.rental.management.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends Base {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private Integer priority;

    private LocalDateTime alertTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    private Reservation(Integer priority, ReservationStatus reservationStatus) {
        this.priority = priority;
        this.reservationStatus = reservationStatus;
    }

    public static Reservation createReservation(Member member, Book book) {
        Integer priority = book.getReservationPriority();
        Reservation reservation = new Reservation(priority, ReservationStatus.READY);
        reservation.settingMember(member);
        reservation.settingBook(book);
        return reservation;
    }

    private void settingMember(Member member) {
        this.member = member;
    }

    private void settingBook(Book book) {
        this.book = book;
    }

    public boolean isReservation() {
        return this.reservationStatus == ReservationStatus.READY;
    }

    public boolean isAvailableByMember(Member member) {
        return this.member.getId().equals(member.getId()) && this.reservationStatus == ReservationStatus.AVAILABLE;
    }

    public boolean isReservationByMember(Member member) {
        return this.member.getId().equals(member.getId());
    }

    private void cancelReservation() {
        this.reservationStatus = ReservationStatus.CANCEL;
    }

    public boolean cancelIfAvailable(Member member) {
        if (this.isAvailableByMember(member)) {
            this.cancelReservation();
            return true;
        }
        return false;
    }
}
