package book.rental.management.docs.book.addbook;

import book.rental.management.request.book.AddBookRequest;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class BookControllerAddBookWithPublisherExceptionTest extends RestDocSupport {

    @Test
    @DisplayName("[DOCS] 책 등록 시 '출판사'를 입력하지 않으면 '400' 오류가 발생한다.")
    void addBook_Exception() throws Exception {
        // given
        AddBookRequest request = AddBookRequest.builder()
                .title("JPA")
                .author("김영한")
                .publisher("")
                .build();
        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(document("add-Book-required-with-publisher-exception",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("author").type(JsonFieldType.STRING)
                                        .description("저자"),
                                fieldWithPath("publisher").type(JsonFieldType.STRING)
                                        .description("출판사")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("오류 데이터"),
                                fieldWithPath("data[].field").type(JsonFieldType.STRING)
                                        .description("필드 명"),
                                fieldWithPath("data[].message").type(JsonFieldType.STRING)
                                        .description("에러 메시지")
                        )
                        )
                );
    }

}
