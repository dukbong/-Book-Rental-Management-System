package book.rental.management.docs.member.addmember;

import book.rental.management.request.member.JoinMemberRequest;
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

public class MemberControllerAddMemberWithEmailExceptionTest extends RestDocSupport {
    @Test
    @DisplayName("[DOCS] 사용자 등록 시 이메일은 형식을 지켜야 합니다.")
    void addMemberWithNameException() throws Exception {
        // given
        JoinMemberRequest request = new JoinMemberRequest("James Arthur Gosling", "java-Spring", "010-1111-1111");

        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/member")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(document("add-member-with-email-exception",
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
