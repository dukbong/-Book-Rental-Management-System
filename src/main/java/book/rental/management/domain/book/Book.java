package book.rental.management.domain.book;

import book.rental.management.domain.Base;
import book.rental.management.domain.loan.Loan;
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

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();

    @Builder
    private Book(String title, String author, String publisher) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

//    public void rental(Loan loan) {
//        this.loans.add(loan);
//        loan.settingBook(this);
//    }

    public boolean isAvailableForLoan() {
        return this.loans.stream().noneMatch(Loan::isActive);
    }

    public void addLoan(Loan loan) {
        this.loans.add(loan);
    }
}
