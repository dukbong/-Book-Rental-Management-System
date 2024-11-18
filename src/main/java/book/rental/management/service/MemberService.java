package book.rental.management.service;

import book.rental.management.domain.book.Book;
import book.rental.management.domain.member.Member;
import book.rental.management.repository.book.BookRepository;
import book.rental.management.repository.member.MemberQueryDslRepository;
import book.rental.management.repository.member.MemberRepository;
import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.request.member.MemberCondition;
import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.member.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryDslRepository memberQueryDslRepository;
    private final BookRepository bookRepository;

    // 전체 Member 조회
    public List<MemberResponse> getMembers() {
        List<Member> members = memberRepository.findAllByOrderByNameAsc();
        return convertResponse(members);
    }

    // 특정 조건 ( 이름, 이메일, 전화번호 ) Member 조회 - 동적 쿼리
    public List<MemberResponse> getMemberByCondition(MemberCondition condition) {
        List<Member> findMembers = memberQueryDslRepository.getMemberByCondition(condition);
        return convertResponse(findMembers);
    }

    // Member 등록
    @Transactional
    public Long addMember(JoinMemberRequest request) {
        Optional<Member> findMember = memberRepository.findByName(request.getName());
        if(findMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();
        memberRepository.save(member);
        return member.getId();
    }

    // Member -> Book 대여
//    V1 ==> 우선 기능적 구현
//    @Transactional
//    public Long rentalBook(RentBookRequest request) {
//        Member findMember = memberRepository.findById(request.getMemberId()).orElseThrow(
//                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
//        );
//        Book findBook = bookRepository.findById(request.getBookId()).orElseThrow(
//                () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
//        );
//        boolean loanCheck = findBook.getLoans().stream()
//                .anyMatch(loan -> loan.getLoanStatus() == LoanStatus.ON_TIME || loan.getLoanStatus() == LoanStatus.OVERDUE);
//        if(loanCheck) {
//            throw new IllegalArgumentException("이미 대여 중인 책입니다.");
//        }
//        Loan loan = Loan.builder()
//                .loanDate(LocalDateTime.now())
//                .returnDate(LocalDateTime.now().plusDays(7))
//                .loanStatus(LoanStatus.ON_TIME)
//                .build();
//        findMember.rental(loan);
//        findBook.rental(loan);
//
//        return findBook.getId();
//    }

//  V2 ==> DDD 설계 방식으로 변경
    @Transactional
    public Long rentalBookV2(RentBookRequest request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(
                () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
        );
        return member.rentTo(book);
    }

    // Member -> Book 반납
//    V1 ==> 기능 구현
//    @Transactional
//    public Long returnBook(RentBookRequest request) {
//        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(
//                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
//        );
//        Book book = bookRepository.findById(request.getBookId()).orElseThrow(
//                () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
//        );
//        member.returnTo(book);
//        Optional<Loan> findLoan = member.getLoans()
//                .stream()
//                .filter(loan -> loan.getBook().getId().equals(book.getId()) &&
//                loan.getLoanStatus() == LoanStatus.ON_TIME ||
//                loan.getLoanStatus() == LoanStatus.OVERDUE).findFirst();
//        if(findLoan.isEmpty()) {
//            throw new IllegalArgumentException("현재 사용자는 해당 책을 대여 중이지 않습니다.");
//        }
//        findLoan.get().updateLoanStatus(LoanStatus.RETURNED);
//        return book.getId();
//    }

//  V2 ==> DDD 설계 방식으로 변경
    @Transactional
    public Long returnBookV2(RentBookRequest request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(
                () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
        );
        return member.returnTo(book);
    }

    private List<MemberResponse> convertResponse(List<Member> members) {
        return members.stream()
                .map(member -> new MemberResponse(
                        member.getName(),
                        member.getEmail(),
                        member.getPhoneNumber())
                ).toList();
    }


}
