package book.rental.management.service;

import book.rental.management.domain.loan.Loan;
import book.rental.management.domain.loan.LoanStatus;
import book.rental.management.repository.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OverDueService {

    private final LoanRepository loanRepository;

    /***
     * 매일 자정에 연체 정보 조회 및 상태 변경
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void overdueSystem() {
        List<Loan> overdueLoans = loanRepository.findByLoanStatusAndReturnDateBefore(LoanStatus.ON_TIME, LocalDateTime.now());
        overdueLoans.forEach(loan -> loan.updateLoanStatus(LoanStatus.OVERDUE));
    }

}
