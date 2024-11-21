package book.rental.management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookCondition {
    private String title;
    private String author;
    private String publisher;

    public BookCondition(String title, String author, String publisher) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }
}
