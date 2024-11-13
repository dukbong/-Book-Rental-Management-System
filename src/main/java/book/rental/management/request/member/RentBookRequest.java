package book.rental.management.request.member;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentBookRequest {
    @NotNull(message = "사용자 정보는 필수 입니다.")
    private Long memberId;
    @NotNull(message = "책 정보는 필수 입니다.")
    private Long bookId;
}
