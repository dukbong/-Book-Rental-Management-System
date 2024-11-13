package book.rental.management.response.book;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookResponse {
    private String title;
    private String author;
    private String publisher;
}
