package book.rental.management.controller.member;

import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.request.member.MemberCondition;
import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.book.BookApiResponse;
import book.rental.management.response.book.BookLoanResponse;
import book.rental.management.response.member.MemberApiResponse;
import book.rental.management.response.member.MemberResponse;
import book.rental.management.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public ResponseEntity<MemberApiResponse<List<MemberResponse>>> getMembers() {
        List<MemberResponse> result = memberService.getMembers();
        return ResponseEntity.ok().body(new MemberApiResponse<>(result));
    }

    @GetMapping("/api/v1/members/search")
    public ResponseEntity<MemberApiResponse<List<MemberResponse>>> getMembersWithCondition(@RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "email", required = false) String email,
                                                        @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
        MemberCondition condition = new MemberCondition(name, email, phoneNumber);
        List<MemberResponse> result = memberService.getMemberByCondition(condition);
        return ResponseEntity.ok().body(new MemberApiResponse<>(result));
    }

    @PostMapping("/api/v1/member")
    public Long addMember(@RequestBody @Valid JoinMemberRequest request) {
        return memberService.addMember(request);
    }

    @PostMapping("/api/v1/member/rental")
    public Long rentalBook(@RequestBody @Valid RentBookRequest request) {
        return memberService.rentalBookV2(request);
    }

    @PostMapping("/api/v1/member/retrun")
    public Long returnBook(@RequestBody @Valid RentBookRequest request) {
        return memberService.returnBookV2(request);
    }

}
