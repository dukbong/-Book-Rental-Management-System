package book.rental.management.response.book;

import book.rental.management.domain.loan.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookLoanResponse {
    private String title;
    private String author;
    private String publisher;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private LoanStatus status;
}
