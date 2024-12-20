package book.rental.management.docs.book.getbooks.paging;

import book.rental.management.response.book.RankBookResponse;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class BookControllerRankBookWithPageOffsetEmptyTest extends RestDocSupport {

    @Test
    @DisplayName("[페이징 - offset: null] 대여한 횟수로 책의 순위를 매길 수 있다.")
    void rankBook() throws Exception {
        // given
        RankBookResponse response1 = new RankBookResponse("A", "A-author", "A-publisher", 3, 3);
        RankBookResponse response2 = new RankBookResponse("B", "B-author", "B-publisher", 1, 5);
        RankBookResponse response3 = new RankBookResponse("C", "C-author", "C-publisher", 2, 4);

        Pageable pageable = PageRequest.of(0,3);

        when(bookService.rankBook(pageable)).thenReturn(List.of(response2, response3, response1));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/book/rank")
                .param("limit", "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("rank-book-paging-param-empty-offset",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 데이터 [max: 3]"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data[].author").type(JsonFieldType.STRING)
                                        .description("저자"),
                                fieldWithPath("data[].publisher").type(JsonFieldType.STRING)
                                        .description("출판사"),
                                fieldWithPath("data[].rank").type(JsonFieldType.NUMBER)
                                        .description("순위"),
                                fieldWithPath("data[].rentCount").type(JsonFieldType.NUMBER)
                                        .description("대여 횟수")
                        )
                        )
                );
    }
}
