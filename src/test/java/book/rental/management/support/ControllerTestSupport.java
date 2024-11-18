package book.rental.management.support;

import book.rental.management.controller.book.BookController;
import book.rental.management.controller.member.MemberController;
import book.rental.management.service.BookService;
import book.rental.management.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        BookController.class,
        MemberController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected BookService bookService;

    @MockBean
    protected MemberService memberService;

}
