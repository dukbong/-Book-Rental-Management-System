package book.rental.management.docs.member.rentalbook;

import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.member.RentalBookResponse;
import book.rental.management.support.RestDocSupport;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class MemberControllerRentalBookWithEmptyMemberIdTest extends RestDocSupport {

    @Test
    void rentalBookTest() throws Exception {
        // given
        RentBookRequest request = new RentBookRequest(null, 1L);

        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/member/rental")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(document("rental-book-fail-empty-member-id",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),

                                requestFields(
                                        fieldWithPath("memberId").type(JsonFieldType.NULL)
                                                .description("대여할 사용자 ID"),
                                        fieldWithPath("bookId").type(JsonFieldType.NUMBER)
                                                .description("대여할 책 ID")
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
