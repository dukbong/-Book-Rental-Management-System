package book.rental.management.domain.loan;

import lombok.Getter;

@Getter
public enum LoanStatus {
    ON_TIME, // 대여 중
    OVERDUE, // 연체 중
    RETURNED // 반납 완료
}
