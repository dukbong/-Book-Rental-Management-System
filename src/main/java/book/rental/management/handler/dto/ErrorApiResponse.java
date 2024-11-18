package book.rental.management.handler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ErrorApiResponse<T> {
    T data;

    public ErrorApiResponse(T data) {
        this.data = data;
    }
}
