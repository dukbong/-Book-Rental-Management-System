package book.rental.management.docs.book.loanlist;

import book.rental.management.domain.loan.LoanStatus;
import book.rental.management.response.book.BookLoanResponse;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class BookControllerLoanListTest extends RestDocSupport {

    @Test
    @DisplayName("[DOCS] 대여 현황을 조회할 수 있다.")
    void loanList() throws Exception {
        // given
        BookLoanResponse bookLoanResponse1 = new BookLoanResponse(
                "JPA",
                "김영한",
                "인프런",
                LocalDateTime.of(2024,11,15,1,30),
                LocalDateTime.of(2024,11,22,1,30),
                LoanStatus.ON_TIME);
        BookLoanResponse bookLoanResponse2 = new BookLoanResponse(
                "QueryDSL",
                "김영한",
                "인프런",
                LocalDateTime.of(2024,11,15,1,30),
                LocalDateTime.of(2024,11,22,1,30),
                LoanStatus.ON_TIME);

        Long memberId = 1L;

        when(bookService.loanList(memberId)).thenReturn(List.of(bookLoanResponse1, bookLoanResponse2));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/book/{memberId}", memberId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("loan-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 데이터"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data[].author").type(JsonFieldType.STRING)
                                        .description("저자"),
                                fieldWithPath("data[].publisher").type(JsonFieldType.STRING)
                                        .description("출판사"),
                                fieldWithPath("data[].loanDate").type(JsonFieldType.STRING)
                                        .description("대여 날짜"),
                                fieldWithPath("data[].returnDate").type(JsonFieldType.STRING)
                                        .description("반납 날짜"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING)
                                        .description("대여 상태")
                        )
                        )
                );
    }
}
