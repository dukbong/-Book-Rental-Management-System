package book.rental.management.docs.book;

import book.rental.management.docs.RestDocSupport;
import book.rental.management.request.book.AddBookRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class BookControllerAddBookTest extends RestDocSupport {

    @Test
    @DisplayName("신규 책을 등록할 수 있다.")
    void getBooks() throws Exception {
        // given
        AddBookRequest addBookRequest = AddBookRequest.builder()
                .title("JPA")
                .author("김영한")
                .publisher("인프런")
                .build();

        Long bookId = 1L;

        when(bookService.addBook(addBookRequest)).thenReturn(bookId);

        // when & then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                .content(objectMapper.writeValueAsString(addBookRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("add-book",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("author").type(JsonFieldType.STRING)
                                        .description("저자"),
                                fieldWithPath("publisher").type(JsonFieldType.STRING)
                                        .description("출판사")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.NUMBER)
                                        .description("책 아이디")
                        )
                        )
                );
    }
}
