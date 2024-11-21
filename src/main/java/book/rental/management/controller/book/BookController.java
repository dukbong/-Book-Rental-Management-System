package book.rental.management.controller.book;

import book.rental.management.request.book.AddBookRequest;
import book.rental.management.dto.BookCondition;
import book.rental.management.response.book.*;
import book.rental.management.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<BookApiResponse<List<BookResponse>>> getBooksWithCondition(
                                                    @RequestParam(value = "title", required = false) String title,
                                                    @RequestParam(value = "author", required = false) String author,
                                                    @RequestParam(value = "publisher", required = false) String publisher) {
        BookCondition condition = new BookCondition(title, author, publisher);
        List<BookResponse> result = bookService.getBookByCondition(condition);
        return ResponseEntity.ok().body(new BookApiResponse<>(result));
    }

    @PostMapping("/api/v1/book")
    public ResponseEntity<BookApiResponse<AddBookResponse>> addBook(@RequestBody @Valid AddBookRequest request) {
        AddBookResponse result = bookService.addBook(request);
        return ResponseEntity.ok().body(new BookApiResponse<>(result));
    }

    @GetMapping("/api/v1/book/{memberId}")
    public ResponseEntity<BookApiResponse<List<BookLoanResponse>>> loanList(@PathVariable("memberId") Long memberId) {
        List<BookLoanResponse> result = bookService.loanList(memberId);
        return ResponseEntity.ok().body(new BookApiResponse<>(result));
    }

    @GetMapping("/api/v1/book/rank")
    public ResponseEntity<BookApiResponse<List<RankBookResponse>>> rankBook(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        Pageable pageable = PageRequest.of(offset <= 0 ? 0 : offset, limit <= 0 ? 100 : limit);
        List<RankBookResponse> result = bookService.rankBook(pageable);
        return ResponseEntity.ok().body(new BookApiResponse<>(result));
    }
}
