package book.rental.management.handler;

import book.rental.management.handler.dto.ErrorApiResponse;
import book.rental.management.handler.dto.ErrorDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApiResponse> handlerValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<ErrorDetail> errors = fieldErrors.stream()
                .map(err -> new ErrorDetail(err.getField(), err.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(new ErrorApiResponse(errors));
    }

}
