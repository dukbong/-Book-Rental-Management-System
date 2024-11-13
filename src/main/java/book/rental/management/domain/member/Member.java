package book.rental.management.domain.member;

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
public class Member extends Base {
    @Id @GeneratedValue
    private Long id;
    @Column(nullable = false, length = 20, unique = true)
    private String name;
    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();

    @Builder
    public Member(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void rental(Loan loan) {
        this.loans.add(loan);
        loan.settingMember(this);
    }
}