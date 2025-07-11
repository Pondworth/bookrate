package de.pondworth.bookrate;

import de.pondworth.bookrate.model.Book;
import de.pondworth.bookrate.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        Book book = new Book("Testbuch", "Autor", 5, "Kommentar", "Roman", "Gelesen");
        bookRepository.save(book);
    }

    @Test
    void shouldReturnBooksFilteredByGenre() throws Exception {
        mockMvc.perform(get("/api/books").param("genre", "Roman"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Testbuch 1"))
                .andExpect(jsonPath("$[0].genre").value("Roman"));
    }
}