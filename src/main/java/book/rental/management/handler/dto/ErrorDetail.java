package book.rental.management.handler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ErrorDetail {
    String field;
    String message;

    public ErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
