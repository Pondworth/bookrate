package de.pondworth.bookrate;

import de.pondworth.bookrate.controller.BookController;
import de.pondworth.bookrate.model.Book;
import de.pondworth.bookrate.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;  // Jetzt mocken wir den Service statt Repository!

    @Test
    void shouldReturnListOfBooks() throws Exception {
        // Mock-Daten vorbereiten
        Book book = new Book("Testbuch", "Testautor", 5, "Sehr gut!", "Roman", "Will noch lesen");
        Mockito.when(bookService.getAllBooks(null, null)).thenReturn(List.of(book));

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
        Mockito.when(bookService.getAllBooks(null, "Roman")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books").param("genre", "Roman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Testbuch"))
                .andExpect(jsonPath("$[0].genre").value("Roman"));
    }

    @Test
    void shouldReturnBookById() throws Exception {
        Book book = new Book("Testbuch", "Autor", 5, "Kommentar", "Roman", "Gelesen");
        book.setId(1L);
        Mockito.when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Testbuch"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        Mockito.when(bookService.getBookById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateBook() throws Exception {
        Book book = new Book("Neues Buch", "Neuer Autor", 4, "Gut!", "Fantasy", "Gelesen");
        book.setId(1L);

        Mockito.when(bookService.createBook(any(Book.class))).thenReturn(book);

        String bookJson = """
                {
                    "title": "Neues Buch",
                    "author": "Neuer Autor",
                    "rating": 4,
                    "comment": "Gut!",
                    "genre": "Fantasy",
                    "status": "Gelesen"
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Neues Buch"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void shouldReturn400WhenCreatingInvalidBook() throws Exception {
        // Service wirft Exception bei ungültigen Daten
        Mockito.when(bookService.createBook(any(Book.class)))
                .thenThrow(new IllegalArgumentException("Rating muss zwischen 1 und 5 liegen!"));

        String invalidBookJson = """
                {
                    "title": "Test",
                    "author": "Autor",
                    "rating": 10,
                    "comment": "Test"
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBookJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Book updatedBook = new Book("Updated Title", "Updated Author", 5, "Updated!", "Krimi", "Gelesen");
        updatedBook.setId(1L);

        Mockito.when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        String bookJson = """
                {
                    "title": "Updated Title",
                    "author": "Updated Author",
                    "rating": 5,
                    "comment": "Updated!",
                    "genre": "Krimi",
                    "status": "Gelesen"
                }
                """;

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        // Service wirft keine Exception = erfolgreich gelöscht
        Mockito.doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentBook() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Buch nicht gefunden"))
                .when(bookService).deleteBook(999L);

        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchBooks() throws Exception {
        Book book = new Book("Harry Potter", "J.K. Rowling", 5, "Magisch!", "Fantasy", "Gelesen");
        Mockito.when(bookService.searchBooks("Harry")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/search").param("query", "Harry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Harry Potter"));
    }
}