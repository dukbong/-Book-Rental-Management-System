package book.rental.management.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponse {
    private String name;
    private String email;
    private String phoneNumber;
}
