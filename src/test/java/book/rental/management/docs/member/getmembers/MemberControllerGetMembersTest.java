package book.rental.management.docs.member.getmembers;

import book.rental.management.response.member.MemberResponse;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class MemberControllerGetMembersTest extends RestDocSupport {

    @Test
    @DisplayName("[DOCS] 전체 회원을 조회한다.")
    void getMembers() throws Exception {
        // given
        MemberResponse response1 = new MemberResponse("James Arthur Gosling", "java@gmail.com", "010-1111-1111");
        MemberResponse response2 = new MemberResponse("Jeff Dean", "google@gmail.com", "010-2222-2222");

        when(memberService.getMembers()).thenReturn(List.of(response1, response2));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("get-members",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 메시지"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING)
                                        .description("이메일").optional(),
                                fieldWithPath("data[].phoneNumber").type(JsonFieldType.STRING)
                                        .description("전화번호").optional()
                        )
                        )
                );
    }
}
