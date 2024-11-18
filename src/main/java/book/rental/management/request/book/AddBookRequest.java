package book.rental.management.request.book;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddBookRequest {
    @NotEmpty(message = "제목은 필수 입력사항입니다.")
    private String title;
    @NotEmpty(message = "저자는 필수 입력사항입니다.")
    private String author;
    @NotEmpty(message = "출판사는 필수 입력사항입니다.")
    private String publisher;

    @Builder
    public AddBookRequest(String title, String author, String publisher) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }
}
