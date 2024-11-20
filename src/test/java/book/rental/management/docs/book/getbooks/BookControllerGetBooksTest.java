package book.rental.management.docs.book.getbooks;

import book.rental.management.support.RestDocSupport;
import book.rental.management.response.book.BookResponse;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class BookControllerGetBooksTest extends RestDocSupport {

    @Test
    @DisplayName("[DOCS] 전체 책을 조회할 수 있다.")
    void getBooks() throws Exception {
        // given
        BookResponse bookResponse1 = new BookResponse("JPA", "김영한", "인프런");
        BookResponse bookResponse2 = new BookResponse("Spring", "남궁성", "유튜브");

        when(bookService.getBooks()).thenReturn(List.of(bookResponse1, bookResponse2));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("get-books",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 데이터 목록"),
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
