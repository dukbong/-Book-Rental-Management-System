package book.rental.management.controller.book;

import book.rental.management.support.ControllerTestSupport;
import book.rental.management.domain.loan.LoanStatus;
import book.rental.management.request.book.AddBookRequest;
import book.rental.management.request.book.BookCondition;
import book.rental.management.response.book.BookLoanResponse;
import book.rental.management.response.book.BookResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class BookControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("신규 책을 등록한다.")
    void addBook() throws Exception {
        // given
        AddBookRequest request = AddBookRequest.builder()
                .title("JPA")
                .author("JPA-Author")
                .publisher("JPA-Publisher")
                .build();

        Long expectedBookId = 1L;
        // when
        when(bookService.addBook(request)).thenReturn(expectedBookId);
        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data").value(expectedBookId)); // 응답의 "data" 필드 값 확인
    }

    @Test
    @DisplayName("신규 책을 등록한다. - 유효성 검사(제목)")
    void addBookWithValidation_title() throws Exception {
        // given
        AddBookRequest request = AddBookRequest.builder()
                .title("")
                .author("JPA-Author")
                .publisher("JPA-Publisher")
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("title"))
                .andExpect(jsonPath("$.data[0].message").value("제목은 필수 입력사항입니다."));
    }

    @Test
    @DisplayName("신규 책을 등록한다. - 유효성 검사(저자)")
    void addBookWithValidation_author() throws Exception {
        // given
        AddBookRequest request = AddBookRequest.builder()
                .title("JPA")
                .author("")
                .publisher("JPA-Publisher")
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("author"))
                .andExpect(jsonPath("$.data[0].message").value("저자는 필수 입력사항입니다."));
    }

    @Test
    @DisplayName("신규 책을 등록한다. - 유효성 검사(출판사)")
    void addBookWithValidation_publisher() throws Exception {
        // given
        AddBookRequest request = AddBookRequest.builder()
                .title("JPA")
                .author("JPA-Author")
                .publisher("")
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("publisher"))
                .andExpect(jsonPath("$.data[0].message").value("출판사는 필수 입력사항입니다."));
    }

    @Test
    @DisplayName("신규 책을 등록한다. - 유효성 검사(제목, 저자, 출판사)")
    void addBookWithValidation_All() throws Exception {
        // given
        AddBookRequest request = AddBookRequest.builder()
                .title("")
                .author("")
                .publisher("")
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data", Matchers.hasItem(
                        Matchers.allOf(
                                Matchers.hasEntry("field", "title"),
                                Matchers.hasEntry("message", "제목은 필수 입력사항입니다.")
                        )
                )))
                .andExpect(jsonPath("$.data", Matchers.hasItem(
                        Matchers.allOf(
                                Matchers.hasEntry("field", "author"),
                                Matchers.hasEntry("message", "저자는 필수 입력사항입니다.")
                        )
                )))
                .andExpect(jsonPath("$.data", Matchers.hasItem(
                        Matchers.allOf(
                                Matchers.hasEntry("field", "publisher"),
                                Matchers.hasEntry("message", "출판사는 필수 입력사항입니다.")
                        )
                )))
        ;
    }

    @Test
    @DisplayName("책을 전체 조회한다.")
    void getBooks() throws Exception {
        // given
        BookResponse response1 = new BookResponse("JPA", "김영한", "인프런");
        BookResponse response2 = new BookResponse("Spring", "남궁성", "유튜브");
        
        // when
        when(bookService.getBooks()).thenReturn(List.of(response1, response2));
        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].title").value(response1.getTitle()))
                .andExpect(jsonPath("$.data[0].author").value(response1.getAuthor()))
                .andExpect(jsonPath("$.data[0].publisher").value(response1.getPublisher()))
                .andExpect(jsonPath("$.data[1].title").value(response2.getTitle()))
                .andExpect(jsonPath("$.data[1].author").value(response2.getAuthor()))
                .andExpect(jsonPath("$.data[1].publisher").value(response2.getPublisher()));
    }

    @Test
    @DisplayName("책에 조건부로 검색이 가능하다.")
    void getBooksWithCondition() throws Exception {
        // given
        BookResponse response1 = new BookResponse("JPA", "김영한", "인프런");
        BookCondition condition = new BookCondition("JPA", "김영한", "인프런");

        // when
        when(bookService.getBookByCondition(condition)).thenReturn(List.of(response1));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/books/search")
                        .param("title", "JPA")
                        .param("author", "김영한")
                        .param("publisher", "인프런")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].title").value(response1.getTitle()))
                .andExpect(jsonPath("$.data[0].author").value(response1.getAuthor()))
                .andExpect(jsonPath("$.data[0].publisher").value(response1.getPublisher()))
        ;
    }

    @Test
    @DisplayName("사용자 별 대여 현황 조회가 가능하다.")
    void loanList() throws Exception {
        // given
        Long memberId = 1L;
        BookLoanResponse bookLoanResponse1 = new BookLoanResponse("JPA",
                "김영한",
                "인프런",
                LocalDateTime.of(2024,11,15,1,30,15),
                LocalDateTime.of(2024,11,23,11,30,20),
                LoanStatus.ON_TIME);
        BookLoanResponse bookLoanResponse2 = new BookLoanResponse("Spring",
                "남궁성",
                "유튜브",
                LocalDateTime.of(2024,11,15,1,30,25),
                LocalDateTime.of(2024,11,17,11,30,30),
                LoanStatus.OVERDUE);

        // when
        when(bookService.loanList(memberId)).thenReturn(List.of(bookLoanResponse1, bookLoanResponse2));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/book/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].title").value(bookLoanResponse1.getTitle()))
                .andExpect(jsonPath("$.data[0].author").value(bookLoanResponse1.getAuthor()))
                .andExpect(jsonPath("$.data[0].publisher").value(bookLoanResponse1.getPublisher()))
                .andExpect(jsonPath("$.data[0].loanDate").value(bookLoanResponse1.getLoanDate().toString()))
                .andExpect(jsonPath("$.data[0].returnDate").value(bookLoanResponse1.getReturnDate().toString()))
                .andExpect(jsonPath("$.data[0].status").value(bookLoanResponse1.getStatus().toString()));
    }

}