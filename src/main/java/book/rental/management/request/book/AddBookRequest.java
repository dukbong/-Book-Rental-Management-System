package book.rental.management.request.book;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddBookRequest {
    @NotEmpty(message = "제목은 필수 입력사항입니다.")
    private String title;
    @NotEmpty(message = "저자는 필수 입력사항입니다.")
    private String author;
    @NotEmpty(message = "출판사는 필수 입력사항입니다.")
    private String publisher;
}
