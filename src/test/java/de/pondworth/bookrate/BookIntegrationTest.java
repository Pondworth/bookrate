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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        // bookRepository.deleteAll();
        bookRepository.save(new Book("Testbuch 1", "Autor A", 4, "Gut", "Roman", "Gelesen"));
        bookRepository.save(new Book("Testbuch 2", "Autor B", 3, "Okay", "Fantasy", "Will ich lesen"));
        bookRepository.save(new Book("Testbuch 3", "Autor C", 5, "Top", "Roman", "Lese ich"));
    }

    @Test
    void shouldReturnBooksFilteredByGenre() throws Exception {
        mockMvc.perform(get("/api/books").param("genre", "Roman")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].genre").value("Roman"));
    }
}
