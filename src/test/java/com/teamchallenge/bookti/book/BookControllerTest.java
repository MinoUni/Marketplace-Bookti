package com.teamchallenge.bookti.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.bookti.user.UserRepository;
import java.time.Year;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {


  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @MockBean private UserRepository userRepository;

  @MockBean private BookService bookService;
  
  @Test
  @WithMockUser
  void test1() throws Exception {
    BookSaveDTO bookPayload = new BookSaveDTO(
            "title", "author", "genre", Year.of(1999), "eng", BookExchangeFormat.GIFT.name(), "description");
    MockMultipartFile mockFile = new MockMultipartFile(
            "bookPayload",
            "book.json",
            APPLICATION_JSON_VALUE,
            mapper.writeValueAsBytes(bookPayload));
    when(bookService.save(any(), any(), any())).thenReturn(BookDetailsDTO.builder().build());
    mockMvc
        .perform(
            multipart("/books")
                .file(mockFile)
                .contentType(MULTIPART_FORM_DATA)
                .param("userId", "1"))
        .andExpectAll(status().isCreated(), content().contentType(APPLICATION_JSON));

    verify(bookService, times(1)).save(any(), any(), any());
  }
}
