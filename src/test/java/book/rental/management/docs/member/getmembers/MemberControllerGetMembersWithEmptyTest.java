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

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class MemberControllerGetMembersWithEmptyTest extends RestDocSupport {

    @Test
    @DisplayName("[DOCS] 회원이 없이도 전체 회원을 조회한다.")
    void getMembers() throws Exception {
        // given
        when(memberService.getMembers()).thenReturn(List.of());

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("get-members-empty",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 메시지")
                        )
                        )
                );
    }
}
