package book.rental.management.service;

import book.rental.management.domain.loan.Loan;
import book.rental.management.domain.loan.LoanStatus;
import book.rental.management.repository.loan.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OverDueServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private OverDueService overDueService;

    @Test
    void overDueServiceTest() {
        // given
        Loan loan = Loan.builder()
                .loanStatus(LoanStatus.ON_TIME)
                .loanDate(LocalDateTime.of(2024,11,11,1,30))
                .returnDate(LocalDateTime.of(2024,11,17,1,30))
                .build();

        when(loanRepository.findByLoanStatusAndReturnDateBefore(
                eq(LoanStatus.ON_TIME),
                any(LocalDateTime.class))
        ).thenReturn(List.of(loan));

        // when
        overDueService.overdueSystem();

        assertEquals(LoanStatus.OVERDUE, loan.getLoanStatus());
        verify(loanRepository, times(1)).findByLoanStatusAndReturnDateBefore(
                eq(LoanStatus.ON_TIME),
                any(LocalDateTime.class)
        );

    }

}