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
import book.rental.management.response.book.BookLoanResponse;
import book.rental.management.response.member.MemberResponse;
import book.rental.management.support.IntegrationTestSupport;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("[성공]: 이름, 이메일, 전화번호 모두 입력한 사용자의 등록")
    void addMember_fullRequest() {
        // given
        JoinMemberRequest joinMemberRequest = createJoinMemberRequestAll("user1", "asdf@gmail.com", "010-1234-5678");

        // when
        Long memberId = memberService.addMember(joinMemberRequest);

        // then
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );
        assertThat(findMember.getName()).isEqualTo("user1");
        assertThat(findMember.getEmail()).isEqualTo("asdf@gmail.com");
        assertThat(findMember.getPhoneNumber()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("[성공]: 이름, 전화번호 모두 입력한 사용자의 등록")
    void addMember_emptyEmail() {
        // given
        JoinMemberRequest joinMemberRequest = createJoinMemberRequestEmptyEmail("user1", "010-1234-5678");

        // when
        Long memberId = memberService.addMember(joinMemberRequest);

        // then
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );
        assertThat(findMember.getName()).isEqualTo("user1");
        assertThat(findMember.getPhoneNumber()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("[성공]: 이름, 이메일 모두 입력한 사용자의 등록")
    void addMember_emptyPhoneNumber() {
        // given
        JoinMemberRequest joinMemberRequest = createJoinMemberRequestEmptyPhoneNumber("user1", "asdf@gmail.com");

        // when
        Long memberId = memberService.addMember(joinMemberRequest);

        // then
        Member findMember = memberRepository.findById(memberId).orElseThrow(
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
        Long memberId = memberService.addMember(joinMemberRequest);

        // then
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );
        assertThat(findMember.getName()).isEqualTo("user1");
    }

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

    @Test
    void rentalBook() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1);

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
    void rentalBook2() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("bb", "bbbb@gmail.com", "010-2222-2222");
        Long memberId2 = memberService.addMember(joinMemberRequest2);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest);

        Loan loan = loanRepository.findAll().get(0);
        loan.updateLoanStatus(LoanStatus.RETURNED);

        RentBookRequest rentBookRequest2 = new RentBookRequest();
        rentBookRequest2.setBookId(bookId);
        rentBookRequest2.setMemberId(memberId2);

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

    @Test
    void loanList() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest);

        // when
        List<BookLoanResponse> list = bookService.loanList(memberId1);

        // then
        assertThat(list.size()).isEqualTo(1);
        for (BookLoanResponse bookLoanResponse : list) {
            assertThat(bookLoanResponse.getTitle()).isEqualTo("JPA");
            assertThat(bookLoanResponse.getAuthor()).isEqualTo("b-author");
            assertThat(bookLoanResponse.getPublisher()).isEqualTo("b-publisher");
            assertThat(bookLoanResponse.getStatus()).isEqualTo(LoanStatus.ON_TIME);
        }
    }

    @Test
    void loanList_notFoundMember() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest);

        // when & then
        Exception ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
            bookService.loanList(memberId1 + 1L)
        );
        assertThat(ex.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("[실패]: 책 대여할 사용자를 찾을 수 없습니다.")
    void rentalBook_notFoundMember() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1 + 1L);

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
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId + 1L);
        rentBookRequest.setMemberId(memberId1);

        // when & then
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.rentalBookV2(rentBookRequest)
        );
        assertThat(ex.getMessage()).isEqualTo("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("[실패]: 책 반납할 사용자를 찾을 수 없습니다.")
    void returnBook_notFoundMember() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest);
        rentBookRequest.setMemberId(memberId1 + 1L);
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
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest);
        rentBookRequest.setBookId(bookId + 1L);
        // when & then
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.returnBookV2(rentBookRequest)
        );
        assertThat(ex.getMessage()).isEqualTo("책을 찾을 수 없습니다.");
    }
    @Test
    @DisplayName("[성공]: 대여한 책을 반납")
    void returnBook() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setBookId(bookId);
        rentBookRequest.setMemberId(memberId1);
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
    @DisplayName("[실패]: 이미 대여중인 책입니다.")
    void rentalBook_duplication() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("z", "zzzz@gmail.com", "010-9999-999");
        Long memberId2 = memberService.addMember(joinMemberRequest2);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest1 = new RentBookRequest();
        rentBookRequest1.setBookId(bookId);
        rentBookRequest1.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest1);

        RentBookRequest rentBookRequest2 = new RentBookRequest();
        rentBookRequest2.setBookId(bookId);
        rentBookRequest2.setMemberId(memberId2);

        // when & then
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.rentalBookV2(rentBookRequest2)
        );

        assertThat(ex.getMessage()).isEqualTo("이미 대여 중인 책입니다.");
    }

    @Test
    @DisplayName("[실패]: 2이미 대여중인 책입니다.")
    void rentalBook_duplication2() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest1 = new RentBookRequest();
        rentBookRequest1.setBookId(bookId);
        rentBookRequest1.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest1);

        Loan loan = loanRepository.findAll().get(0);
        loan.updateLoanStatus(LoanStatus.OVERDUE);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.rentalBookV2(rentBookRequest1)
        );

        assertThat(ex.getMessage()).isEqualTo("이미 대여 중인 책입니다.");
    }

    @Test
    @DisplayName("[실패]: 현재 사용자는 해당 책을 대여 하지 않은 상태입니다.")
    void returnTo_notRentBook() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("z", "zzzz@gmail.com", "010-9999-999");
        Long memberId2 = memberService.addMember(joinMemberRequest2);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest1 = new RentBookRequest();
        rentBookRequest1.setBookId(bookId);
        rentBookRequest1.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest1);

        RentBookRequest rentBookRequest2 = new RentBookRequest();
        rentBookRequest2.setBookId(bookId);
        rentBookRequest2.setMemberId(memberId2);

        // when & then
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.returnBookV2(rentBookRequest2)
        );

        assertThat(ex.getMessage()).isEqualTo("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.");
    }

    @Test
    @DisplayName("[실패]: 2현재 사용자는 해당 책을 대여 하지 않은 상태입니다.")
    void returnTo_notRentBook2() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        JoinMemberRequest joinMemberRequest2 = createJoinMemberRequestAll("z", "zzzz@gmail.com", "010-9999-999");
        Long memberId2 = memberService.addMember(joinMemberRequest2);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest1 = new RentBookRequest();
        rentBookRequest1.setBookId(bookId);
        rentBookRequest1.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest1);

        Loan loan = loanRepository.findAll().get(0);
        loan.updateLoanStatus(LoanStatus.OVERDUE);

        RentBookRequest rentBookRequest2 = new RentBookRequest();
        rentBookRequest2.setBookId(bookId);
        rentBookRequest2.setMemberId(memberId2);

        // when & then
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.returnBookV2(rentBookRequest2)
        );

        assertThat(ex.getMessage()).isEqualTo("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.");
    }

    @Test
    @DisplayName("[성공]: 연체된 책 반납")
    void returnTo_overdueBook() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest1 = new RentBookRequest();
        rentBookRequest1.setBookId(bookId);
        rentBookRequest1.setMemberId(memberId1);
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

    @Test
    @DisplayName("[실패]: 대여하지 않은 책 반납")
    void returnTo_noneBook() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);

        RentBookRequest rentBookRequest1 = new RentBookRequest();
        rentBookRequest1.setBookId(bookId);
        rentBookRequest1.setMemberId(memberId1);
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
    @DisplayName("[실패]: 대여하지 않은 책 반납")
    void returnTo_noneBook2() {
        // given
        JoinMemberRequest joinMemberRequest1 = createJoinMemberRequestAll("b", "aaaa@gmail.com", "010-1111-1111");
        Long memberId1 = memberService.addMember(joinMemberRequest1);
        AddBookRequest addBookRequest1 = createAddBookRequest("JPA", "b-author", "b-publisher");
        Long bookId = bookService.addBook(addBookRequest1);
        AddBookRequest addBookRequest2 = createAddBookRequest("Spring", "c-author", "c-publisher");
        Long bookId2 = bookService.addBook(addBookRequest2);

        RentBookRequest rentBookRequest1 = new RentBookRequest();
        rentBookRequest1.setBookId(bookId);
        rentBookRequest1.setMemberId(memberId1);
        memberService.rentalBookV2(rentBookRequest1);

        RentBookRequest rentBookRequest2 = new RentBookRequest();
        rentBookRequest2.setBookId(bookId2);
        rentBookRequest2.setMemberId(memberId1);

        Loan loan = loanRepository.findAll().get(0);
        loan.updateLoanStatus(LoanStatus.RETURNED);

        // when & then
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.returnBookV2(rentBookRequest2)
        );

        assertThat(ex.getMessage()).isEqualTo("현재 사용자는 해당 책을 대여 하지 않은 상태입니다.");
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