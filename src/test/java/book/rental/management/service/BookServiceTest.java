package book.rental.management.service;

import book.rental.management.domain.book.Book;
import book.rental.management.request.book.AddBookRequest;
import book.rental.management.dto.BookCondition;
import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.response.book.AddBookResponse;
import book.rental.management.response.book.BookResponse;
import book.rental.management.response.book.RankBookResponse;
import book.rental.management.support.IntegrationTestSupport;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookServiceTest extends IntegrationTestSupport {

    @Nested
    @DisplayName("등록 성공")
    class successAddBookTests {
        @Test
        @DisplayName("[성공]: 책이 정상적으로 등록")
        void addBook() {
            // given
            AddBookRequest addBookRequest = createAddBookRequest("test-book", "test-author", "test-publisher");

            // when
            AddBookResponse result = bookService.addBook(addBookRequest);

            // then
            Book findBook = bookRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            assertThat(findBook.getTitle()).isEqualTo("test-book");
            assertThat(findBook.getAuthor()).isEqualTo("test-author");
            assertThat(findBook.getPublisher()).isEqualTo("test-publisher");
        }

        @Test
        @DisplayName("[성공]: 출판사가 다른 같은 책 등록")
        void addBook_withDifferentPublisher() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("test-book", "test-author", "test-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("test-book", "test-author", "new-publisher");

            // when
            AddBookResponse result = bookService.addBook(addBookRequest2);

            // then
            Book findBook = bookRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );
            assertThat(findBook.getTitle()).isEqualTo("test-book");
            assertThat(findBook.getAuthor()).isEqualTo("test-author");
            assertThat(findBook.getPublisher()).isEqualTo("new-publisher");
        }

        @Test
        @DisplayName("[성공]: 저자가 다른 같은 책 등록")
        void addBook_withDifferentAuthor() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("test-book", "test-author", "test-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("test-book", "new-author", "test-publisher");

            // when
            AddBookResponse result = bookService.addBook(addBookRequest2);

            // then
            Book findBook = bookRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );
            assertThat(findBook.getTitle()).isEqualTo("test-book");
            assertThat(findBook.getAuthor()).isEqualTo("new-author");
            assertThat(findBook.getPublisher()).isEqualTo("test-publisher");
        }

        @Test
        @DisplayName("[성공]: 제목이 다른 같은 책 등록")
        void addBook_withDifferentTitle() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("test-book", "test-author", "test-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("new-book", "test-author", "test-publisher");

            // when
            AddBookResponse result = bookService.addBook(addBookRequest2);

            // then
            Book findBook = bookRepository.findById(result.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );
            assertThat(findBook.getTitle()).isEqualTo("new-book");
            assertThat(findBook.getAuthor()).isEqualTo("test-author");
            assertThat(findBook.getPublisher()).isEqualTo("test-publisher");
        }
    }

    @Nested
    @DisplayName("등록 실패")
    class failAddBookTests {
        @Test
        @DisplayName("[실패]: 중복 책 등록")
        void addBook_withDuplicateBook() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("test-book", "test-author", "test-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("test-book", "test-author", "test-publisher");


            // when & then
            Exception ex = Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.addBook(addBookRequest2));
            assertThat(ex.getMessage()).isEqualTo("이미 등록된 책입니다.");
        }
    }

    @Nested
    @DisplayName("조회")
    class getBooksTests {
        @Test
        @DisplayName("[성공]: 책 전체 조회 - 사전순")
        void getBooks() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookRequest addBookRequest2 = createAddBookRequest("a", "a-author", "a-publisher");
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest1);
            bookService.addBook(addBookRequest2);
            bookService.addBook(addBookRequest3);

            // when
            List<BookResponse> findBooks = bookService.getBooks();

            // then
            assertThat(findBooks).hasSize(3)
                    .extracting("title", "author", "publisher")
                    .containsExactly(
                            Tuple.tuple("a", "a-author", "a-publisher"),
                            Tuple.tuple("b", "b-author", "b-publisher"),
                            Tuple.tuple("c", "c-author", "c-publisher")
                    );
        }

        @Test
        @DisplayName("[성공]: 책 제목으로 조회 - 사전순")
        void getBooksByConditionWithTitle() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookRequest addBookRequest2 = createAddBookRequest("a", "a-author", "a-publisher");
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest1);
            bookService.addBook(addBookRequest2);
            bookService.addBook(addBookRequest3);
            BookCondition condition = new BookCondition();
            condition.setTitle("a");

            // when
            List<BookResponse> findBooks = bookService.getBookByCondition(condition);

            // then
            assertThat(findBooks).hasSize(1)
                    .extracting("title", "author", "publisher")
                    .containsExactly(
                            Tuple.tuple("a", "a-author", "a-publisher")
                    );
        }

        @Test
        @DisplayName("[성공]: 제목, 저자, 출판사로 조회 - 사전순")
        void getBooksByConditionWithAll() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookRequest addBookRequest2 = createAddBookRequest("a", "a-author", "a-publisher");
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest1);
            bookService.addBook(addBookRequest2);
            bookService.addBook(addBookRequest3);
            BookCondition condition = new BookCondition("a", "a-author", "a-publisher");

            // when
            List<BookResponse> findBooks = bookService.getBookByCondition(condition);

            // then
            assertThat(findBooks).hasSize(1)
                    .extracting("title", "author", "publisher")
                    .containsExactly(
                            Tuple.tuple("a", "a-author", "a-publisher")
                    );
        }

        @Test
        @DisplayName("[성공]: 책 저자로 조회 - 사전순")
        void getBooksByConditionWithAuthor() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookRequest addBookRequest2 = createAddBookRequest("a", "a-author", "a-publisher");
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest1);
            bookService.addBook(addBookRequest2);
            bookService.addBook(addBookRequest3);
            BookCondition condition = new BookCondition();
            condition.setAuthor("a-author");

            // when
            List<BookResponse> findBooks = bookService.getBookByCondition(condition);

            // then
            assertThat(findBooks).hasSize(1)
                    .extracting("title", "author", "publisher")
                    .containsExactly(
                            Tuple.tuple("a", "a-author", "a-publisher")
                    );
        }

        @Test
        @DisplayName("[성공]: 책 출판사로 조회 - 사전순")
        void getBooksByConditionWithPublisher() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookRequest addBookRequest2 = createAddBookRequest("a", "a-author", "a-publisher");
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest1);
            bookService.addBook(addBookRequest2);
            bookService.addBook(addBookRequest3);
            BookCondition condition = new BookCondition();
            condition.setPublisher("a-publisher");

            // when
            List<BookResponse> findBooks = bookService.getBookByCondition(condition);

            // then
            assertThat(findBooks).hasSize(1)
                    .extracting("title", "author", "publisher")
                    .containsExactly(
                            Tuple.tuple("a", "a-author", "a-publisher")
                    );
        }
    }

    @Nested
    @DisplayName("대여 랭킹 - 조회")
    class rankTests {
        @Test
        @DisplayName("[성공]: 대여 횟수가 같은 경우 이름 사전순 조회")
        void rankBook_sort_name() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("a", "a-author", "a-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookResponse response = bookService.addBook(addBookRequest2);
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest3);

            Book book = bookRepository.findById(response.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            book.increaseRentalCount();

            // when
            List<RankBookResponse> result = bookService.rankBook(PageRequest.of(0,100));

            // then
            assertThat(result.get(0).getRentCount()).isEqualTo(1);
            assertThat(result.get(0).getRank()).isEqualTo(1);
            assertThat(result.get(0).getTitle()).isEqualTo("b");
            assertThat(result.get(0).getAuthor()).isEqualTo("b-author");
            assertThat(result.get(0).getPublisher()).isEqualTo("b-publisher");
            assertThat(result.get(1).getRentCount()).isEqualTo(0);
            assertThat(result.get(1).getRank()).isEqualTo(2);
            assertThat(result.get(1).getTitle()).isEqualTo("a");
            assertThat(result.get(1).getAuthor()).isEqualTo("a-author");
            assertThat(result.get(1).getPublisher()).isEqualTo("a-publisher");
            assertThat(result.get(2).getRentCount()).isEqualTo(0);
            assertThat(result.get(2).getRank()).isEqualTo(3);
            assertThat(result.get(2).getTitle()).isEqualTo("c");
            assertThat(result.get(2).getAuthor()).isEqualTo("c-author");
            assertThat(result.get(2).getPublisher()).isEqualTo("c-publisher");
        }

        @Test
        @DisplayName("[성공]: 대여 횟수와 제목이 같은 경우 저자 사전순 조회")
        void rankBook_sort_author() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("c", "a-author", "a-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookResponse response = bookService.addBook(addBookRequest2);
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest3);

            Book book = bookRepository.findById(response.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            book.increaseRentalCount();

            // when
            List<RankBookResponse> result = bookService.rankBook(PageRequest.of(0,100));

            // then
            assertThat(result.get(0).getRentCount()).isEqualTo(1);
            assertThat(result.get(0).getRank()).isEqualTo(1);
            assertThat(result.get(0).getTitle()).isEqualTo("b");
            assertThat(result.get(0).getAuthor()).isEqualTo("b-author");
            assertThat(result.get(0).getPublisher()).isEqualTo("b-publisher");
            assertThat(result.get(1).getRentCount()).isEqualTo(0);
            assertThat(result.get(1).getRank()).isEqualTo(2);
            assertThat(result.get(1).getTitle()).isEqualTo("c");
            assertThat(result.get(1).getAuthor()).isEqualTo("a-author");
            assertThat(result.get(1).getPublisher()).isEqualTo("a-publisher");
            assertThat(result.get(2).getRentCount()).isEqualTo(0);
            assertThat(result.get(2).getRank()).isEqualTo(3);
            assertThat(result.get(2).getTitle()).isEqualTo("c");
            assertThat(result.get(2).getAuthor()).isEqualTo("c-author");
            assertThat(result.get(2).getPublisher()).isEqualTo("c-publisher");
        }

        @Test
        @DisplayName("[성공]: 대여 횟수와 제목, 저자가 같은 경우 출판사 사전순 조회")
        void rankBook_sort_publisher() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("c", "c-author", "a-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookResponse response = bookService.addBook(addBookRequest2);
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest3);

            Book book = bookRepository.findById(response.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            book.increaseRentalCount();

            // when
            List<RankBookResponse> result = bookService.rankBook(PageRequest.of(0,100));

            // then
            assertThat(result.get(0).getRentCount()).isEqualTo(1);
            assertThat(result.get(0).getRank()).isEqualTo(1);
            assertThat(result.get(0).getTitle()).isEqualTo("b");
            assertThat(result.get(0).getAuthor()).isEqualTo("b-author");
            assertThat(result.get(0).getPublisher()).isEqualTo("b-publisher");
            assertThat(result.get(1).getRentCount()).isEqualTo(0);
            assertThat(result.get(1).getRank()).isEqualTo(2);
            assertThat(result.get(1).getTitle()).isEqualTo("c");
            assertThat(result.get(1).getAuthor()).isEqualTo("c-author");
            assertThat(result.get(1).getPublisher()).isEqualTo("a-publisher");
            assertThat(result.get(2).getRentCount()).isEqualTo(0);
            assertThat(result.get(2).getRank()).isEqualTo(3);
            assertThat(result.get(2).getTitle()).isEqualTo("c");
            assertThat(result.get(2).getAuthor()).isEqualTo("c-author");
            assertThat(result.get(2).getPublisher()).isEqualTo("c-publisher");
        }
    }

    @Nested
    @DisplayName("페이징 처리")
    class pagingTests {
        @Test
        @DisplayName("[성공]: 책 대여 횟수 랭킹 조회 페이징 처리 [첫 페이지에 2개씩 나오도록]")
        void rankBook_paging_0_2() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("c", "c-author", "a-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookResponse response = bookService.addBook(addBookRequest2);
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest3);

            Book book = bookRepository.findById(response.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            book.increaseRentalCount();

            // when
            List<RankBookResponse> result = bookService.rankBook(PageRequest.of(0,2));

            // then
            assertThat(result.get(0).getRentCount()).isEqualTo(1);
            assertThat(result.get(0).getRank()).isEqualTo(1);
            assertThat(result.get(0).getTitle()).isEqualTo("b");
            assertThat(result.get(0).getAuthor()).isEqualTo("b-author");
            assertThat(result.get(0).getPublisher()).isEqualTo("b-publisher");
            assertThat(result.get(1).getRentCount()).isEqualTo(0);
            assertThat(result.get(1).getRank()).isEqualTo(2);
            assertThat(result.get(1).getTitle()).isEqualTo("c");
            assertThat(result.get(1).getAuthor()).isEqualTo("c-author");
            assertThat(result.get(1).getPublisher()).isEqualTo("a-publisher");
        }

        @Test
        @DisplayName("[성공]: 책 대여 횟수 랭킹 조회 페이징 처리 [두번째 페이지에 2개씩 나오지만 1개만 있을때]")
        void rankBook_paging_1_2_1() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("c", "c-author", "a-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookResponse response = bookService.addBook(addBookRequest2);
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest3);

            Book book = bookRepository.findById(response.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            book.increaseRentalCount();

            // when
            List<RankBookResponse> result = bookService.rankBook(PageRequest.of(1,2));

            // then
            assertThat(result.get(0).getRentCount()).isEqualTo(0);
            assertThat(result.get(0).getTitle()).isEqualTo("c");
            assertThat(result.get(0).getAuthor()).isEqualTo("c-author");
            assertThat(result.get(0).getRank()).isEqualTo(3);
            assertThat(result.get(0).getPublisher()).isEqualTo("c-publisher");
        }

        @Test
        @DisplayName("[성공]: 책 대여 횟수 랭킹 조회 페이징 처리 [세번째 페이지에 2개씩 나오지만 없을때]")
        void rankBook_paging_2_2_0() {
            // given
            AddBookRequest addBookRequest1 = createAddBookRequest("c", "c-author", "a-publisher");
            bookService.addBook(addBookRequest1);
            AddBookRequest addBookRequest2 = createAddBookRequest("b", "b-author", "b-publisher");
            AddBookResponse response = bookService.addBook(addBookRequest2);
            AddBookRequest addBookRequest3 = createAddBookRequest("c", "c-author", "c-publisher");
            bookService.addBook(addBookRequest3);

            Book book = bookRepository.findById(response.getId()).orElseThrow(
                    () -> new IllegalArgumentException("책을 찾을 수 없습니다.")
            );

            book.increaseRentalCount();

            // when
            List<RankBookResponse> result = bookService.rankBook(PageRequest.of(2,2));

            // then
            assertThat(result).hasSize(0);
        }

    }


    private AddBookRequest createAddBookRequest(String title, String author, String publisher) {
        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setTitle(title);
        addBookRequest.setAuthor(author);
        addBookRequest.setPublisher(publisher);
        return addBookRequest;
    }
}