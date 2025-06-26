package de.pondworth.bookrate;

import de.pondworth.bookrate.controller.BookController;
import de.pondworth.bookrate.model.Book;
import de.pondworth.bookrate.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Test
    void shouldReturnListOfBooks() throws Exception {
        // Mock-Daten vorbereiten
        Book book = new Book("Testbuch", "Testautor", 5, "Sehr gut!", "Roman", "Will noch lesen");
        Mockito.when(bookRepository.findAll()).thenReturn(List.of(book));

        // Test ausführen
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Testbuch"))
                .andExpect(jsonPath("$[0].author").value("Testautor"))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Sehr gut!"));
    }

    @Test
    void shouldFilterBooksByGenre() throws Exception {
        Book book = new Book("Testbuch", "Autor", 5, "Kommentar", "Roman", "Gelesen");
        Mockito.when(bookRepository.findByGenre("Roman")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books").param("genre", "Roman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Testbuch"))
                .andExpect(jsonPath("$[0].genre").value("Roman"));
    }
}
