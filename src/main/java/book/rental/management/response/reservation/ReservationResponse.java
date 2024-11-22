package book.rental.management.response.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationResponse {
    private Long bookId;
    private Integer priority;
}
