package book.rental.management.controller.member;

import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.request.member.MemberCondition;
import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.member.MemberResponse;
import book.rental.management.support.ControllerTestSupport;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class MemberControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("사용자 전체 조회")
    void getMembers() throws Exception {
        // given
        MemberResponse response1 = new MemberResponse("test1", "test1@gmail.com", "010-1111-1111");
        MemberResponse response2 = new MemberResponse("test2", "test2@gmail.com", "010-2222-2222");

        // when
        when(memberService.getMembers()).thenReturn(List.of(response1, response2));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("test1"))
                .andExpect(jsonPath("$.data[0].email").value("test1@gmail.com"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("010-1111-1111"))
                .andExpect(jsonPath("$.data[1].name").value("test2"))
                .andExpect(jsonPath("$.data[1].email").value("test2@gmail.com"))
                .andExpect(jsonPath("$.data[1].phoneNumber").value("010-2222-2222"))
                ;
    }

    @Test
    @DisplayName("사용자 조건부 조회")
    void getMembersWithCondition() throws Exception {
        // given
        MemberCondition condition = new MemberCondition("test1", "test1@gmail.com", "010-1111-1111");

        MemberResponse response1 = new MemberResponse("test1", "test1@gmail.com", "010-1111-1111");

        // when
        when(memberService.getMemberByCondition(condition)).thenReturn(List.of(response1));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members/search")
                        .param("name", "test1")
                        .param("email", "test1@gmail.com")
                        .param("phoneNumber", "010-1111-1111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("test1"))
                .andExpect(jsonPath("$.data[0].email").value("test1@gmail.com"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("010-1111-1111"))
        ;
    }

    @Test
    @DisplayName("신규 사용자 등록")
    void addMember() throws Exception {
        // given
        Long memberId = 1L;

        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName("test1");
        joinMemberRequest.setEmail("test1@gmail.com");
        joinMemberRequest.setPhoneNumber("010-1111-1111");

        // when
        when(memberService.addMember(joinMemberRequest)).thenReturn(memberId);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member")
                        .content(objectMapper.writeValueAsString(joinMemberRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data").value(memberId))
        ;
    }

    @Test
    @DisplayName("신규 사용자 등록 - 유효성 검사(이름)")
    void addMemberWithValidation_name() throws Exception {
        // given
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName("");
        joinMemberRequest.setEmail("test1@gmail.com");
        joinMemberRequest.setPhoneNumber("010-1111-1111");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member")
                        .content(objectMapper.writeValueAsString(joinMemberRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("name"))
                .andExpect(jsonPath("$.data[0].message").value("사용자 이름은 필수 사항입니다."));
    }

    @Test
    @DisplayName("신규 사용자 등록 - 유효성 검사(메일)")
    void addMemberWithValidation_email() throws Exception {
        // given
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName("test1");
        joinMemberRequest.setEmail("test1123123");
        joinMemberRequest.setPhoneNumber("010-1111-1111");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member")
                        .content(objectMapper.writeValueAsString(joinMemberRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("email"))
                .andExpect(jsonPath("$.data[0].message").value("유효한 이메일 주소를 입력해 주세요."));
    }

    @Test
    @DisplayName("신규 사용자 등록 - 유효성 검사(전화번호)")
    void addMemberWithValidation_phoneNumber() throws Exception {
        // given
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName("test1");
        joinMemberRequest.setEmail("test1@gmail.com");
        joinMemberRequest.setPhoneNumber("0101111112431");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member")
                        .content(objectMapper.writeValueAsString(joinMemberRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("phoneNumber"))
                .andExpect(jsonPath("$.data[0].message").value("유효한 전화번호 형식(예: 010-1234-1234)을 입력해 주세요."));
    }

    @Test
    @DisplayName("신규 사용자 등록 - 유효성 검사(이름, 메일, 전화번호)")
    void addMemberWithValidation_All() throws Exception {
        // given
        JoinMemberRequest joinMemberRequest = new JoinMemberRequest();
        joinMemberRequest.setName("");
        joinMemberRequest.setEmail("test1234");
        joinMemberRequest.setPhoneNumber("0101111112431");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member")
                        .content(objectMapper.writeValueAsString(joinMemberRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.data", Matchers.hasItem(
                        Matchers.allOf(
                                Matchers.hasEntry("field", "name"),
                                Matchers.hasEntry("message", "사용자 이름은 필수 사항입니다.")
                        )
                )))
                .andExpect(jsonPath("$.data", Matchers.hasItem(
                        Matchers.allOf(
                                Matchers.hasEntry("field", "email"),
                                Matchers.hasEntry("message", "유효한 이메일 주소를 입력해 주세요.")
                        )
                )))
                .andExpect(jsonPath("$.data", Matchers.hasItem(
                        Matchers.allOf(
                                Matchers.hasEntry("field", "phoneNumber"),
                                Matchers.hasEntry("message", "유효한 전화번호 형식(예: 010-1234-1234)을 입력해 주세요.")
                        )
                )))
        ;
    }

    @Test
    @DisplayName("사용자가 책을 대여한다.")
    void rentalBook() throws Exception {
        // given
        Long bookId = 1L;

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setMemberId(1L);
        rentBookRequest.setBookId(1L);

        // when
        when(memberService.rentalBookV2(rentBookRequest)).thenReturn(bookId);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member/rental")
                        .content(objectMapper.writeValueAsString(rentBookRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data").value(bookId))
        ;
    }

    @Test
    @DisplayName("사용자가 책을 반납한다.")
    void returnBook() throws Exception {
        // given
        Long bookId = 1L;

        RentBookRequest rentBookRequest = new RentBookRequest();
        rentBookRequest.setMemberId(1L);
        rentBookRequest.setBookId(1L);

        // when
        when(memberService.returnBookV2(rentBookRequest)).thenReturn(bookId);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member/retrun")
                        .content(objectMapper.writeValueAsString(rentBookRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data").value(bookId))
        ;
    }
}