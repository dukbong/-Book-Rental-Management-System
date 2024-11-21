package book.rental.management.response.book;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankBookResponse {
    private String title;
    private String author;
    private String publisher;
    private int rank;
    private int rentCount;
}
