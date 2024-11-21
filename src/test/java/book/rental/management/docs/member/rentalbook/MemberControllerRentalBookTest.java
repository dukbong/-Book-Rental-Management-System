package book.rental.management.docs.member.rentalbook;

import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.member.RentalBookResponse;
import book.rental.management.response.member.ReturnBookResponse;
import book.rental.management.support.RestDocSupport;
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

public class MemberControllerRentalBookTest extends RestDocSupport {

    @Test
    void rentalBookTest() throws Exception {
        // given
        RentBookRequest request = new RentBookRequest(1L, 1L);

        RentalBookResponse response = new RentalBookResponse(1L);

        when(memberService.rentalBookV2(request)).thenReturn(response);

        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/member/rental")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("rental-book-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                                        .description("대여할 사용자 ID"),
                                fieldWithPath("bookId").type(JsonFieldType.NUMBER)
                                        .description("대여할 책 ID")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("대여 성공한 책 ID")
                        )
                        )
                );
    }
}
