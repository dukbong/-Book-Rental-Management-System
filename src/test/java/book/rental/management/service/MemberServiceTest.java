package book.rental.management.service;

import book.rental.management.domain.book.Book;
import book.rental.management.domain.loan.Loan;
import book.rental.management.domain.loan.LoanStatus;
import book.rental.management.domain.member.Member;
import book.rental.management.repository.loan.LoanRepository;
import book.rental.management.repository.member.MemberRepository;
import book.rental.management.request.book.AddBookRequest;
import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.request.member.MemberCondition;
import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.book.AddBookResponse;
import book.rental.management.response.book.BookLoanResponse;
import book.rental.management.response.member.AddMemberResponse;
import book.rental.management.response.member.MemberResponse;
import book.rental.management.support.IntegrationTestSupport;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private LoanRepository loanRepository;

    @Nested
    @DisplayName("등록 성공")
    class successAddMemberTests {
        @Test
        @DisplayName("[성공]: 이름, 이메일, 전화번호 모두 입력한 사용자의 등록")
        void addMember_fullRequest() {
            // given
            JoinMemberRequest joinMemberRequest = createJoinMemberRequestAll("user1", "asdf@gmail.com", "010-1234-5678");

            // when
            AddMemberResponse result = memberService.addMember(joinMemberRequest);

            // then
            Member findMember = memberRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
            );
            assertThat(findMember.getName()).isEqualTo("user1");
            assertThat(findMember.getEmail()).isEqualTo("asdf@gmail.com");
            assertThat(findMember.getPhoneNumber()).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("[성공]: 이름, 전화번호를 입력한 사용자의 등록")
        void addMember_emptyEmail() {
            // given
            JoinMemberRequest joinMemberRequest = createJoinMemberRequestEmptyEmail("user1", "010-1234-5678");

            // when
            AddMemberResponse result = memberService.addMember(joinMemberRequest);

            // then
            Member findMember = memberRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
            );
            assertThat(findMember.getName()).isEqualTo("user1");
            assertThat(findMember.getPhoneNumber()).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("[성공]: 이름, 이메일을 입력한 사용자의 등록")
        void addMember_emptyPhoneNumber() {
            // given
            JoinMemberRequest joinMemberRequest = createJoinMemberRequestEmptyPhoneNumber("user1", "asdf@gmail.com");

            // when
            AddMemberResponse result = memberService.addMember(joinMemberRequest);

            // then
            Member findMember = memberRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
            );
            assertThat(findMember.getName()).isEqualTo("user1");
            assertThat(findMember.getEmail()).isEqualTo("asdf@gmail.com");
        }

        @Test
        @DisplayName("[성공]: 이름만 입력한 사용자의 등록")
        void addMember_emptyEmailAndPhoneNumber() {
            // given
            JoinMemberRequest joinMemberRequest = createJoinMemberRequestEmptyEmailAndPhoneNumber("user1");

            // when
            AddMemberResponse result = memberService.addMember(joinMemberRequest);

            // then
            Member findMember = memberRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
            );
            assertThat(findMember.getName()).isEqualTo("user1");
        }
    }

    @Nested
    @DisplayName("등록 실패")
    class failAddMemberTests {
        @Test
        @DisplayName("[실패]: 이미 존재하는 사용자 이름으로 등록")
        void addMember_duplicationName() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("user1", "010-1234-5678", "asdf@gmail.com");
            memberService.addMember(joinMemberRequest1);
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("user1", "010-9789-4567", "zxcv@gmail.com");

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> memberService.addMember(joinMemberRequest2)
            );
            assertThat(ex.getMessage()).isEqualTo("이미 존재하는 사용자 이름입니다.");
        }
    }

    @Nested
    @DisplayName("조회")
    class getMembersTests {
        @Test
        @DisplayName("[성공] 회원 모두를 조회 - 사전순")
        void getMembers() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("a", "bbbb@gmail.com", "010-2222-2222");
            JoinMemberRequest joinMemberRequest3 = createJoinMemberRequestAll("c", "cccc@gmail.com", "010-3333-3333");
            memberService.addMember(joinMemberRequest1);
            memberService.addMember(joinMemberRequest2);
            memberService.addMember(joinMemberRequest3);

            // when
            List<MemberResponse> findMembers = memberService.getMembers();

            // then
            assertThat(findMembers).hasSize(3)
                    .extracting("name", "email", "phoneNumber")
                    .containsExactly(
                            Tuple.tuple("a", "bbbb@gmail.com", "010-2222-2222"),
                            Tuple.tuple("b", "aaaa@gmail.com", "010-1111-1111"),
                            Tuple.tuple("c", "cccc@gmail.com", "010-3333-3333")
                    );
        }

        @Test
        @DisplayName("[성공]: 사용자 이름으로 조회 - 사전순")
        void getMembersByConditionWithName() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("Rod Johnson", "aaaa@gmail.com", "010-1111-1111");
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222");
            JoinMemberRequest joinMemberRequest3 = createJoinMemberRequestAll("Dave Syer", "cccc@gmail.com", "010-3333-3333");
            memberService.addMember(joinMemberRequest1);
            memberService.addMember(joinMemberRequest2);
            memberService.addMember(joinMemberRequest3);
            MemberCondition condition = new MemberCondition();
            condition.setName("Juergen");

            // when
            List<MemberResponse> findMember = memberService.getMemberByCondition(condition);

            // then
            assertThat(findMember).hasSize(1)
                    .extracting("name", "email", "phoneNumber")
                    .containsExactlyInAnyOrder(
                            Tuple.tuple("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222")
                    );
        }

        @Test
        @DisplayName("[성공]: 이름, 이메일, 전화번호로 조회 - 사전순")
        void getMembersByConditionWithAll() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("Rod Johnson", "aaaa@gmail.com", "010-1111-1111");
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222");
            JoinMemberRequest joinMemberRequest3 = createJoinMemberRequestAll("Dave Syer", "cccc@gmail.com", "010-3333-3333");
            memberService.addMember(joinMemberRequest1);
            memberService.addMember(joinMemberRequest2);
            memberService.addMember(joinMemberRequest3);
            MemberCondition condition = new MemberCondition("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222");

            // when
            List<MemberResponse> findMember = memberService.getMemberByCondition(condition);

            // then
            assertThat(findMember).hasSize(1)
                    .extracting("name", "email", "phoneNumber")
                    .containsExactlyInAnyOrder(
                            Tuple.tuple("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222")
                    );
        }

        @Test
        @DisplayName("[성공]: 사용자 이메일로 조회 - 사전순")
        void getMembersByConditionWithEmail() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("Rod Johnson", "aaaa@gmail.com", "010-1111-1111");
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222");
            JoinMemberRequest joinMemberRequest3 = createJoinMemberRequestAll("Dave Syer", "cccc@gmail.com", "010-3333-3333");
            memberService.addMember(joinMemberRequest1);
            memberService.addMember(joinMemberRequest2);
            memberService.addMember(joinMemberRequest3);
            MemberCondition condition = new MemberCondition();
            condition.setEmail("bbbb@gmail.com");

            // when
            List<MemberResponse> findMember = memberService.getMemberByCondition(condition);

            // then
            assertThat(findMember).hasSize(1)
                    .extracting("name", "email", "phoneNumber")
                    .containsExactlyInAnyOrder(
                            Tuple.tuple("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222")
                    );
        }

        @Test
        @DisplayName("[성공]: 사용자 전화번호로 조회 - 사전순")
        void getMembersByConditionWithPhoneNumber() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("Rod Johnson", "aaaa@gmail.com", "010-1111-1111");
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("Juergen Hoeller", "bbbb@gmail.com", "010-2222-2222");
            JoinMemberRequest joinMemberRequest3 = createJoinMemberRequestAll("Dave Syer", "cccc@gmail.com", "010-3333-3333");
            memberService.addMember(joinMemberRequest1);
            memberService.addMember(joinMemberRequest2);
            memberService.addMember(joinMemberRequest3);
            MemberCondition condition = new MemberCondition();
            condition.setPhoneNumber("010-3333-3333");

            // when
            List<MemberResponse> findMember = memberService.getMemberByCondition(condition);

            // then
            assertThat(findMember).hasSize(1)
                    .extracting("name", "email", "phoneNumber")
                    .containsExactlyInAnyOrder(
                            Tuple.tuple("Dave Syer", "cccc@gmail.com", "010-3333-3333")
                    );
        }

        @Test
        @DisplayName("[성공]: 입력 사항 없이 조회 시 전체 조회 - 사전순")
        void getMembersByConditionWithEmpty() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("a", "bbbb@gmail.com", "010-2222-2222");
            JoinMemberRequest joinMemberRequest3 = createJoinMemberRequestAll("c", "cccc@gmail.com", "010-3333-3333");
            memberService.addMember(joinMemberRequest1);
            memberService.addMember(joinMemberRequest2);
            memberService.addMember(joinMemberRequest3);
            MemberCondition condition = new MemberCondition();

            // when
            List<MemberResponse> findMember = memberService.getMemberByCondition(condition);

            // then
            assertThat(findMember).hasSize(3)
                    .extracting("name", "email", "phoneNumber")
                    .containsExactly(
                            Tuple.tuple("a", "bbbb@gmail.com", "010-2222-2222"),
                            Tuple.tuple("b", "aaaa@gmail.com", "010-1111-1111"),
                            Tuple.tuple("c", "cccc@gmail.com", "010-3333-3333")
                    );
        }
    }

    @Nested
    @DisplayName("대여 성공")
    class rentalBookTests {
        @Test
        @DisplayName("[성공]: 책을 대여할 수 있다.")
        void rentalBook() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(memberResult.getId());

            // when
            Long rentBookId = memberService.rentalBookV2(rentBookRequest);

            // then
            Book rentBook = bookRepository.findById(rentBookId).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            assertThat(rentBook.getLoans()).hasSize(1);
            for(int i = 0; i < rentBook.getLoans().size(); i++) {
                assertThat(rentBook.getLoans().get(i).getBook().getTitle()).isEqualTo("JPA");
                assertThat(rentBook.getLoans().get(i).getBook().getAuthor()).isEqualTo("b-author");
                assertThat(rentBook.getLoans().get(i).getBook().getPublisher()).isEqualTo("b-publisher");
                assertThat(rentBook.getLoans().get(i).getMember().getName()).isEqualTo("b");
                assertThat(rentBook.getLoans().get(i).getMember().getEmail()).isEqualTo("aaaa@gmail.com");
            }
        }

        @Test
        @DisplayName("[성공]: 반납된 책을 대여할 수 있다.")
        void rentalBook2() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse result1 = memberService.addMember(joinMemberRequest1);
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("bb", "bbbb@gmail.com", "010-2222-2222");
            AddMemberResponse result2 = memberService.addMember(joinMemberRequest2);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(result1.getId());
            memberService.rentalBookV2(rentBookRequest);

            Loan loan = loanRepository.findAll().get(0);
            loan.updateLoanStatus(LoanStatus.RETURNED);

            RentBookRequest rentBookRequest2 = new RentBookRequest();
            rentBookRequest2.setBookId(result.getId());
            rentBookRequest2.setMemberId(result2.getId());

            // when
            Long rentBookId = memberService.rentalBookV2(rentBookRequest2);
            // then
            Book rentBook = bookRepository.findById(rentBookId).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            assertThat(rentBook.getLoans()).hasSize(2);
            assertThat(rentBook.getLoans().get(1).getBook().getTitle()).isEqualTo("JPA");
            assertThat(rentBook.getLoans().get(1).getBook().getAuthor()).isEqualTo("b-author");
            assertThat(rentBook.getLoans().get(1).getBook().getPublisher()).isEqualTo("b-publisher");
            assertThat(rentBook.getLoans().get(1).getMember().getName()).isEqualTo("bb");
            assertThat(rentBook.getLoans().get(1).getMember().getEmail()).isEqualTo("bbbb@gmail.com");
            assertThat(rentBook.getLoans().get(0).getMember().getName()).isEqualTo("b");
            assertThat(rentBook.getLoans().get(0).getMember().getEmail()).isEqualTo("aaaa@gmail.com");
        }
    }

    @Nested
    @DisplayName("대여 실패")
    class failRentalBookTests {
        @Test
        @DisplayName("[실패]: 책 대여할 사용자를 찾을 수 없습니다.")
        void rentalBook_notFoundMember() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(memberResult.getId() + 1L);

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.rentalBookV2(rentBookRequest)
            );
            assertThat(ex.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("[실패]: 대여할 책을 찾을 수 없습니다.")
        void rentalBook_notFoundBook() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId() + 1L);
            rentBookRequest.setMemberId(memberResult.getId());

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.rentalBookV2(rentBookRequest)
            );
            assertThat(ex.getMessage()).isEqualTo("책을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("[실패]: 대여 중인 책은 대여할 수 없습니다.")
        void rentalBook_duplication() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult1 = memberService.addMember(joinMemberRequest1);
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("z", "zzzz@gmail.com", "010-9999-999");
            AddMemberResponse memberResult2 = memberService.addMember(joinMemberRequest2);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest1 = new RentBookRequest();
            rentBookRequest1.setBookId(result.getId());
            rentBookRequest1.setMemberId(memberResult1.getId());
            memberService.rentalBookV2(rentBookRequest1);

            RentBookRequest rentBookRequest2 = new RentBookRequest();
            rentBookRequest2.setBookId(result.getId());
            rentBookRequest2.setMemberId(memberResult2.getId());

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.rentalBookV2(rentBookRequest2)
            );

            assertThat(ex.getMessage()).isEqualTo("이미 대여 중인 책입니다.");
        }

        @Test
        @DisplayName("[실패]: 연체 중인 책은 대여할 수 없습니다.")
        void rentalBook_duplication2() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest1 = new RentBookRequest();
            rentBookRequest1.setBookId(result.getId());
            rentBookRequest1.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest1);

            Loan loan = loanRepository.findAll().get(0);
            loan.updateLoanStatus(LoanStatus.OVERDUE);

            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.rentalBookV2(rentBookRequest1)
            );

            assertThat(ex.getMessage()).isEqualTo("이미 대여 중인 책입니다.");
        }
    }

    @Nested
    @DisplayName("반납 성공")
    class returnBookTests {
        @Test
        @DisplayName("[성공]: 대여한 책을 반납")
        void returnBook() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest);

            // when
            Long findBookId = memberService.returnBookV2(rentBookRequest);

            // then
            Book book = bookRepository.findById(findBookId).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );
            assertThat(book.getLoans()).hasSize(1)
                    .extracting("loanStatus")
                    .containsExactlyInAnyOrder(
                            LoanStatus.RETURNED
                    );
        }

        @Test
        @DisplayName("[성공]: 연체된 책을 반납")
        void returnTo_overdueBook() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest1 = new RentBookRequest();
            rentBookRequest1.setBookId(result.getId());
            rentBookRequest1.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest1);

            Loan loan = loanRepository.findAll().get(0);
            loan.updateLoanStatus(LoanStatus.OVERDUE);

            // when
            Long returnBookId = memberService.returnBookV2(rentBookRequest1);
            Book book = bookRepository.findById(returnBookId).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            assertThat(book.getLoans()).hasSize(1)
                    .extracting("loanStatus")
                    .containsExactlyInAnyOrder(
                            LoanStatus.RETURNED
                    );
        }
    }
    
    @Nested
    @DisplayName("반납 실패")
    class failReturnBookTests {
        @Test
        @DisplayName("[실패]: 책 반납할 사용자를 찾을 수 없습니다.")
        void returnBook_notFoundMember() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest);
            rentBookRequest.setMemberId(memberResult.getId() + 1L);
            // when
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.returnBookV2(rentBookRequest)
            );
            assertThat(ex.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("[실패]: 책 반납할 책을 찾을 수 없습니다.")
        void returnBook_notFoundBook() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest);
            rentBookRequest.setBookId(result.getId() + 1L);
            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.returnBookV2(rentBookRequest)
            );
            assertThat(ex.getMessage()).isEqualTo("책을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("[실패]: 현재 사용자는 해당 책을 대여 하지 않은 상태입니다.")
        void returnTo_notRentBook() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult1 = memberService.addMember(joinMemberRequest1);
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("z", "zzzz@gmail.com", "010-9999-999");
            AddMemberResponse memberResult2 = memberService.addMember(joinMemberRequest2);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest1 = new RentBookRequest();
            rentBookRequest1.setBookId(result.getId());
            rentBookRequest1.setMemberId(memberResult1.getId());
            memberService.rentalBookV2(rentBookRequest1);

            RentBookRequest rentBookRequest2 = new RentBookRequest();
            rentBookRequest2.setBookId(result.getId());
            rentBookRequest2.setMemberId(memberResult2.getId());

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.returnBookV2(rentBookRequest2)
            );

            assertThat(ex.getMessage()).isEqualTo("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.");
        }

        @Test
        @DisplayName("[실패]: 현재 사용자는 해당 책을 대여 하지 않은 상태입니다. [연체 된 책]")
        void returnTo_notRentBook2() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult1 = memberService.addMember(joinMemberRequest1);
            JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("z", "zzzz@gmail.com", "010-9999-999");
            AddMemberResponse memberResult2 = memberService.addMember(joinMemberRequest2);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest1 = new RentBookRequest();
            rentBookRequest1.setBookId(result.getId());
            rentBookRequest1.setMemberId(memberResult1.getId());
            memberService.rentalBookV2(rentBookRequest1);

            Loan loan = loanRepository.findAll().get(0);
            loan.updateLoanStatus(LoanStatus.OVERDUE);

            RentBookRequest rentBookRequest2 = new RentBookRequest();
            rentBookRequest2.setBookId(result.getId());
            rentBookRequest2.setMemberId(memberResult2.getId());

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.returnBookV2(rentBookRequest2)
            );

            assertThat(ex.getMessage()).isEqualTo("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.");
        }

        @Test
        @DisplayName("[실패]: 현재 사용자는 해당 책을 대여 하지 않은 상태입니다. [반납된 책]")
        void returnTo_noneBook() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest1 = new RentBookRequest();
            rentBookRequest1.setBookId(result.getId());
            rentBookRequest1.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest1);

            Loan loan = loanRepository.findAll().get(0);
            loan.updateLoanStatus(LoanStatus.RETURNED);

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.returnBookV2(rentBookRequest1)
            );

            assertThat(ex.getMessage()).isEqualTo("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.");
        }

        @Test
        @DisplayName("[실패]: 대여하지 않은 책 반납 [대여 항목의 책 ID 비교]")
        void returnTo_noneBook2() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result1 = bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("Spring", "c-author", "c-publisher");
            AddBookResponse result2 = bookService.addBook(addBookRequest2);

            RentBookRequest rentBookRequest1 = new RentBookRequest();
            rentBookRequest1.setBookId(result1.getId());
            rentBookRequest1.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest1);

            RentBookRequest rentBookRequest2 = new RentBookRequest();
            rentBookRequest2.setBookId(result2.getId());
            rentBookRequest2.setMemberId(memberResult.getId());

            Loan loan = loanRepository.findAll().get(0);
            loan.updateLoanStatus(LoanStatus.RETURNED);

            // when & then
            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    memberService.returnBookV2(rentBookRequest2)
            );

            assertThat(ex.getMessage()).isEqualTo("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.");
        }
    }

    @Nested
    @DisplayName("대여 후 대여 현황 조회")
    class LoanListAfterRentalTests {
        @Test
        @DisplayName("[성공]: 대여 후 대여 현황 조회")
        void loanList() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest);

            // when
            List<BookLoanResponse> list = bookService.loanList(memberResult.getId());

            // then
            assertThat(list.size()).isEqualTo(1);
            for (BookLoanResponse bookLoanResponse : list) {
                assertThat(bookLoanResponse.getTitle()).isEqualTo("JPA");
                assertThat(bookLoanResponse.getAuthor()).isEqualTo("b-author");
                assertThat(bookLoanResponse.getPublisher()).isEqualTo("b-publisher");
                assertThat(bookLoanResponse.getStatus()).isEqualTo(LoanStatus.ON_TIME);
            }
        }
    }
    
    @Nested
    @DisplayName("대여 후 대여 현황 조회 실패")
    class failLoanListAfterRentalTests {
        @Test
        @DisplayName("[실패]: 대여 후 대여 현황 조회시 사용자 아이디가 없는 경우")
        void loanList_notFoundMember() {
            // given
            JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
            AddMemberResponse memberResult = memberService.addMember(joinMemberRequest1);
            AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
            AddBookResponse result = bookService.addBook(addBookRequest1);

            RentBookRequest rentBookRequest = new RentBookRequest();
            rentBookRequest.setBookId(result.getId());
            rentBookRequest.setMemberId(memberResult.getId());
            memberService.rentalBookV2(rentBookRequest);

            // when & then
            Exception ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    bookService.loanList(memberResult.getId() + 1L)
            );
            assertThat(ex.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
        }
    }

    private AddBookRequest createAddBookRequest(String title, String author, String publisher) {
        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setTitle(title);
        addBookRequest.setAuthor(author);
        addBookRequest.setPublisher(publisher);
        return addBookRequest;
    }

    private JoinMemberRequest createJoinMemberRequestAll(String name, String email, String phoneNumber) {
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName(name);
        joinMemberRequest.setEmail(email);
        joinMemberRequest.setPhoneNumber(phoneNumber);
        return joinMemberRequest;
    }

    private JoinMemberRequest createJoinMemberRequestEmptyPhoneNumber(String name, String email) {
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName(name);
        joinMemberRequest.setEmail(email);
        return joinMemberRequest;
    }

    private JoinMemberRequest createJoinMemberRequestEmptyEmail(String name, String phoneNumber) {
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName(name);
        joinMemberRequest.setPhoneNumber(phoneNumber);
        return joinMemberRequest;
    }

    private JoinMemberRequest createJoinMemberRequestEmptyEmailAndPhoneNumber(String name) {
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName(name);
        return joinMemberRequest;
    }

}