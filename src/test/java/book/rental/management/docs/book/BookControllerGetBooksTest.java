package book.rental.management.docs.book;

import book.rental.management.docs.RestDocSupport;
import book.rental.management.response.book.BookResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class BookControllerGetBooksTest extends RestDocSupport {

    @Test
    void getBooks() throws Exception {
        // given
        BookResponse bookResponse1 = new BookResponse("JPA", "김영한", "인프런");
        BookResponse bookResponse2 = new BookResponse("Spring", "남궁성", "유튜브");

        when(bookService.getBooks()).thenReturn(List.of(bookResponse1, bookResponse2));

        // when & then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("get-books",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(),
                        responseFields()
                        )
                );

    }
}
