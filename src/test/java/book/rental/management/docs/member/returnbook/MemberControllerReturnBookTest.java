package book.rental.management.docs.member.returnbook;

import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.member.ReturnBookResponse;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class MemberControllerReturnBookTest extends RestDocSupport {

    @Test
    void rentalBookTest() throws Exception {
        // given
        RentBookRequest request = new RentBookRequest(1L, 1L);
        ReturnBookResponse response = new ReturnBookResponse(1L);

        when(memberService.returnBookV2(request)).thenReturn(response);

        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/member/return")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("return-book-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                                        .description("반납할 사용자 ID"),
                                fieldWithPath("bookId").type(JsonFieldType.NUMBER)
                                        .description("반납할 책 ID")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("반납 성공한 책 ID")
                        )
                        )
                );
    }
}
