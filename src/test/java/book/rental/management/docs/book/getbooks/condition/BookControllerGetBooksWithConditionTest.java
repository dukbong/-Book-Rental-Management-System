package book.rental.management.docs.book.getbooks.condition;

import book.rental.management.request.book.BookCondition;
import book.rental.management.response.book.BookResponse;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class BookControllerGetBooksWithConditionTest extends RestDocSupport {

    @Test
    @DisplayName("[DOCS] 조건에 따른 책을 조회할 수 있다.")
    void getBooksWithCondition() throws Exception {
        // given
        BookResponse response1 = new BookResponse("JPA", "kim", "인프런");
        BookResponse response2 = new BookResponse("Spring", "Park", "유튜브");
        BookResponse response3 = new BookResponse("QueryDSL", "Kim", "인프런");

        BookCondition bookCondition = new BookCondition("", "Kim",null);

        when(bookService.getBookByCondition(bookCondition)).thenReturn(List.of(response1, response3));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/books/search")
                .param("title", "")
                .param("author", "Kim")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("get-books-with-condition",
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
                                        .description("출판사")
                        )
                        )
                );
    }
}
