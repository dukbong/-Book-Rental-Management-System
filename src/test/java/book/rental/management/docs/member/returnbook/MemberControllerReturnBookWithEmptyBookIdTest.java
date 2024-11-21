package book.rental.management.docs.member.returnbook;

import book.rental.management.request.member.RentBookRequest;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class MemberControllerReturnBookWithEmptyBookIdTest extends RestDocSupport {

    @Test
    void rentalBookTest() throws Exception {
        // given
        RentBookRequest request = new RentBookRequest(1L, null);

        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/member/return")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(document("return-book-fail-empty-book-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                                        .description("반납할 사용자 ID"),
                                fieldWithPath("bookId").type(JsonFieldType.NULL)
                                        .description("반납할 책 ID")
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
