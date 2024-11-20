package book.rental.management.request.member;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinMemberRequest {
    @NotEmpty(message = "사용자 이름은 필수 사항입니다.")
    private String name;
    // email의 경우 없을 수도 있고 asdf@gmail.com 이런식으로 작성
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "유효한 이메일 주소를 입력해 주세요.")
    private String email;
    // phoneNumber의 경우 없을 수도 있고 010-1234-1234, 010-2345-2345 이런식으로 작성
    @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$",
            message = "유효한 전화번호 형식(예: 010-1234-1234)을 입력해 주세요.")
    private String phoneNumber;
}
