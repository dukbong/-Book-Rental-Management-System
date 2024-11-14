package book.rental.management.domain.loan;

import book.rental.management.domain.Base;
import book.rental.management.domain.book.Book;
import book.rental.management.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Loan extends Base {
    @Id @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private LocalDateTime loanDate;
    @Column(nullable = false)
    private LocalDateTime returnDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Builder
    private Loan(LocalDateTime loanDate, LocalDateTime returnDate, LoanStatus loanStatus) {
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.loanStatus = loanStatus;
    }

    // DDD 방식 으로 변경 위한 테스트
    public static Loan createLoan(LocalDateTime now, LocalDateTime returnDate, LoanStatus loanStatus, Member member, Book book) {
        Loan loan = Loan.builder()
                .loanDate(now)
                .returnDate(returnDate)
                .loanStatus(loanStatus)
                .build();
        loan.settingMember(member);
        loan.settingBook(book);
        return loan;
    }

    private void settingBook(Book book) {
        this.book = book;
    }

    private void settingMember(Member member) {
        this.member = member;
    }

    public void updateLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public boolean isActive() {
        return this.loanStatus == LoanStatus.ON_TIME || this.loanStatus == LoanStatus.OVERDUE;
    }
}
