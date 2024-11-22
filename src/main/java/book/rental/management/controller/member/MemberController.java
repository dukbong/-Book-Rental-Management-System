package book.rental.management.controller.member;

import book.rental.management.request.member.JoinMemberRequest;
import book.rental.management.dto.MemberCondition;
import book.rental.management.request.member.RentBookRequest;
import book.rental.management.response.member.*;
import book.rental.management.response.reservation.ReservationResponse;
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
    public ResponseEntity<MemberApiResponse<List<MemberResponse>>> getMembersWithCondition(
                                                        @RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "email", required = false) String email,
                                                        @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
        MemberCondition condition = new MemberCondition(name, email, phoneNumber);
        List<MemberResponse> result = memberService.getMemberByCondition(condition);
        return ResponseEntity.ok().body(new MemberApiResponse<>(result));
    }

    @PostMapping("/api/v1/member")
    public ResponseEntity<MemberApiResponse<AddMemberResponse>> addMember(@RequestBody @Valid JoinMemberRequest request) {
        AddMemberResponse result = memberService.addMember(request);
        return ResponseEntity.ok().body(new MemberApiResponse<>(result));
    }

    @PostMapping("/api/v1/member/rental")
    public ResponseEntity<MemberApiResponse<RentalBookResponse>> rentalBook(@RequestBody @Valid RentBookRequest request) {
        RentalBookResponse result = memberService.rentalBookV2(request);
        return ResponseEntity.ok().body(new MemberApiResponse<>(result));
    }

    @PostMapping("/api/v1/member/return")
    public ResponseEntity<MemberApiResponse<ReturnBookResponse>> returnBook(@RequestBody @Valid RentBookRequest request) {
        ReturnBookResponse result = memberService.returnBookV2(request);
        return ResponseEntity.ok().body(new MemberApiResponse<>(result));
    }

    @PostMapping("/api/v1/member/reservation")
    public ResponseEntity<MemberApiResponse<ReservationResponse>> reservationBook(@RequestBody @Valid RentBookRequest request) {
        ReservationResponse result = memberService.reservationBook(request);
        return ResponseEntity.ok().body(new MemberApiResponse<>(result));
    }

}
