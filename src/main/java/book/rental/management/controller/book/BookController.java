package book.rental.management.controller.book;

import book.rental.management.request.book.AddBookRequest;
import book.rental.management.request.book.BookCondition;
import book.rental.management.response.book.BookApiResponse;
import book.rental.management.response.book.BookLoanResponse;
import book.rental.management.response.book.BookResponse;
import book.rental.management.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/v1/books")
    public ResponseEntity<BookApiResponse<List<BookResponse>>> getBooks() {
        List<BookResponse> result = bookService.getBooks();
        return ResponseEntity.ok().body(new BookApiResponse<>(result));
    }

    @GetMapping("/api/v1/books/search")
    public ResponseEntity<BookApiResponse<List<BookResponse>>> getBooksWithCondition(@RequestParam(value = "title", required = false) String title,
                                                    @RequestParam(value = "author", required = false) String author,
                                                    @RequestParam(value = "publisher", required = false) String publisher) {
        BookCondition condition = new BookCondition(title, author, publisher);
        List<BookResponse> result = bookService.getBookByCondition(condition);
        return ResponseEntity.ok().body(new BookApiResponse<>(result));
    }

    @PostMapping("/api/v1/book")
    public ResponseEntity<BookApiResponse<Long>> addBook(@RequestBody @Valid AddBookRequest request) {
        Long id = bookService.addBook(request);
        return ResponseEntity.ok().body(new BookApiResponse<>(id));
    }

    @GetMapping("/api/v1/book/{memberId}")
    public ResponseEntity<BookApiResponse<List<BookLoanResponse>>> loanList(@PathVariable("memberId") Long memberId) {
        List<BookLoanResponse> result = bookService.loanList(memberId);
        return ResponseEntity.ok().body(new BookApiResponse<>(result));
    }
}
