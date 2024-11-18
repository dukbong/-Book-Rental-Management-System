package book.rental.management.controller.member;

import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.request.member.MemberCondition;
import book.rental.management.request.member.RentBookRequest;
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
    public ResponseEntity<MemberApiResponse<Long>> addMember(@RequestBody @Valid JoinMemberRequest request) {
        Long memberId = memberService.addMember(request);
        return ResponseEntity.ok().body(new MemberApiResponse<>(memberId));
    }

    @PostMapping("/api/v1/member/rental")
    public ResponseEntity<MemberApiResponse<Long>> rentalBook(@RequestBody @Valid RentBookRequest request) {
        Long bookId = memberService.rentalBookV2(request);
        return ResponseEntity.ok().body(new MemberApiResponse<>(bookId));
    }

    @PostMapping("/api/v1/member/retrun")
    public ResponseEntity<MemberApiResponse<Long>> returnBook(@RequestBody @Valid RentBookRequest request) {
        Long bookId = memberService.returnBookV2(request);
        return ResponseEntity.ok().body(new MemberApiResponse<>(bookId));
    }

}
