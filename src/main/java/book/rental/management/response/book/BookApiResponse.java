package book.rental.management.response.book;

import lombok.Data;

/***
 * response 확장성
 */
@Data
public class BookApiResponse<T> {
    private T data;

    public BookApiResponse(T data) {
        this.data = data;
    }
}
