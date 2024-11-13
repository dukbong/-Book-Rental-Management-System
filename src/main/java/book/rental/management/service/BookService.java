package book.rental.management.service;

import book.rental.management.domain.book.Book;
import book.rental.management.repository.book.BookQueryDslRepository;
import book.rental.management.repository.book.BookRepository;
import book.rental.management.request.book.AddBookRequest;
import book.rental.management.request.book.BookCondition;
import book.rental.management.response.book.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookQueryDslRepository bookQueryDslRepository;

    // Book 등록
    @Transactional
    public Long addBook(AddBookRequest request) {
        boolean bookCheck = bookRepository.existsByTitleAndAuthorAndPublisher(request.getTitle(), request.getAuthor(), request.getPublisher());
        if (bookCheck) {
            throw new IllegalArgumentException("이미 등록된 책입니다.");
        }
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .build();
        bookRepository.save(book);
        return book.getId();
    }

    // 전체 book 조회
    public List<BookResponse> getBooks() {
        List<Book> books = bookRepository.findAllByOrderByTitleAsc();
        return convertResponse(books);
    }

    // 특정 조건 ( 제목, 저자, 출판사 ) Book 조회 - 동적 쿼리
    public List<BookResponse> getBookByCondition(BookCondition condition) {
        List<Book> findBooks = bookQueryDslRepository.getBookByCondition(condition);
        return convertResponse(findBooks);
    }

    private List<BookResponse> convertResponse(List<Book> books) {
        return books.stream()
                .map(book -> new BookResponse(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher()
                )).toList();
    }
}
