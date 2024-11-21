package book.rental.management.domain.member;

import book.rental.management.domain.Base;
import book.rental.management.domain.book.Book;
import book.rental.management.domain.loan.Loan;
import book.rental.management.domain.loan.LoanStatus;
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

    @Builder
    private Member(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

//    public void rental(Loan loan) {
//        this.loans.add(loan);
//        loan.settingMember(this);
//    }

    public Long rentTo(Book book) {
        if (!book.isAvailableForLoan()) {
            throw new IllegalArgumentException("이미 대여 중인 책입니다.");
        }
        Loan loan = Loan.createLoan(LocalDateTime.now(), LocalDateTime.now().plusDays(7), LoanStatus.ON_TIME, this, book);
        this.loans.add(loan);
        book.addLoan(loan);
        book.increaseRentalCount();
        return book.getId();
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