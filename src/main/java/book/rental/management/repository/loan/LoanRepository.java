package book.rental.management.repository.loan;

import book.rental.management.domain.loan.Loan;
import book.rental.management.domain.loan.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("select l from Loan l where l.loanStatus = :status and l.returnDate < :currentDate")
    List<Loan> findByLoanStatusAndReturnDateBefore(@Param("status") LoanStatus status, @Param("currentDate") LocalDateTime currentDate);
}
