package book.rental.management.service;

import book.rental.management.domain.book.Book;
import book.rental.management.domain.member.Member;
import book.rental.management.repository.book.BookRepository;
import book.rental.management.repository.member.MemberRepository;
import book.rental.management.request.book.AddBookRequest;
import book.rental.management.dto.BookCondition;
import book.rental.management.response.book.AddBookResponse;
import book.rental.management.response.book.BookLoanResponse;
import book.rental.management.response.book.BookResponse;
import book.rental.management.response.book.RankBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    // Book 등록
    @Transactional
    public AddBookResponse addBook(AddBookRequest request) {
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
        return new AddBookResponse(book.getId());
    }

    // 전체 book 조회
    public List<BookResponse> getBooks() {
        List<Book> books = bookRepository.findAllByOrderByTitleAsc();
        return convertResponse(books);
    }

    // 특정 조건 ( 제목, 저자, 출판사 ) Book 조회 - 동적 쿼리
    public List<BookResponse> getBookByCondition(BookCondition condition) {
        List<Book> findBooks = bookRepository.getBookByCondition(condition);
        return convertResponse(findBooks);
    }

    // batch_size를 사용하여 단건에서 IN 절로 변경함으로써 N -> 1로 성능 최적화
    public List<BookLoanResponse> loanList(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );
        return member.getActiveLoanSortedByReturnDate();
    }

    // 페이징 처리
    public List<RankBookResponse> rankBook(Pageable pageable) {
        List<Book> ranking = bookRepository.getBookByRank(pageable);
        int startRank = (int) pageable.getOffset() + 1;
        return IntStream.range(0, ranking.size())
                .mapToObj(i -> new RankBookResponse(
                        ranking.get(i).getTitle(),
                        ranking.get(i).getAuthor(),
                        ranking.get(i).getPublisher(),
                        startRank + i,
                        ranking.get(i).getRentalCount()
                )).toList();
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
