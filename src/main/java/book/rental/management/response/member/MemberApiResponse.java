package book.rental.management.response.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberApiResponse<T> {
    private T data;
}
