package book.rental.management.docs.member.addmember;

import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.response.member.AddMemberResponse;
import book.rental.management.support.RestDocSupport;
import org.junit.jupiter.api.DisplayName;
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

public class MemberControllerAddMemberWithPhoneNumberTest extends RestDocSupport {

    @Test
    @DisplayName("[DOCS] 사용자는 전화번호 없이도 등록할 수 있다.")
    void addMember() throws Exception {
        // given
        JoinMemberRequest request = new JoinMemberRequest("James Arthur Gosling", "java@gmail.com", null);
        AddMemberResponse response = new AddMemberResponse(1L);
        when(memberService.addMember(request)).thenReturn(response);

        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/member")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("add-member-null-phone-number",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일").optional(),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                                        .description("전화번호").optional()
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("사용자 ID")
                        )
                        )
                );
    }
}
