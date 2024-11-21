package book.rental.management.docs.member.getmembers.condition;

import book.rental.management.dto.MemberCondition;
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

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class MemberControllerGetMembersWithConditionWithEmptyTest extends RestDocSupport {
    @Test
    @DisplayName("[DOCS] 조건에 따른 사용자를 조회할 수 있다.")
    void getMembersWithCondition() throws Exception {
        // given
        MemberResponse response1 = new MemberResponse("James Arthur Gosling", "java@gmail.com", "010-1111-1111");
        MemberResponse response2 = new MemberResponse("Jeff Dean", "google@gmail.com", "010-2222-2222");

        MemberCondition condition = new MemberCondition(null, null, null);

        when(memberService.getMemberByCondition(condition)).thenReturn(List.of(response1, response2));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/members/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("get-members-with-condition-empty",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),

                                responseFields(
                                    fieldWithPath("data").type(JsonFieldType.ARRAY)
                                            .description("응답 데이터"),
                                    fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                            .description("사용자 이름"),
                                    fieldWithPath("data[].email").type(JsonFieldType.STRING)
                                            .description("이메일"),
                                    fieldWithPath("data[].phoneNumber").type(JsonFieldType.STRING)
                                            .description("전화번호")
                                )
                        )
                );
    }
}
