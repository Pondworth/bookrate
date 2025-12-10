package de.pondworth.bookrate;

import de.pondworth.bookrate.model.Book;
import de.pondworth.bookrate.repository.BookRepository;
import de.pondworth.bookrate.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book validBook;

    @BeforeEach
    void setUp() {
        validBook = new Book("Testbuch", "Testautor", 5, "Sehr gut!", "Roman", "Gelesen");
    }

    // ==================== CREATE Tests ====================

    @Test
    void shouldCreateValidBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        Book result = bookService.createBook(validBook);

        assertNotNull(result);
        assertEquals("Testbuch", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void shouldRejectBookWithInvalidRating() {
        Book invalidBook = new Book("Test", "Autor", 10, "Test", "Roman", "Gelesen");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.createBook(invalidBook)
        );

        assertTrue(exception.getMessage().contains("Rating muss zwischen 1 und 5 liegen"));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldRejectBookWithNegativeRating() {
        Book invalidBook = new Book("Test", "Autor", -1, "Test", "Roman", "Gelesen");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.createBook(invalidBook)
        );

        assertTrue(exception.getMessage().contains("Rating muss zwischen 1 und 5 liegen"));
    }

    @Test
    void shouldRejectBookWithEmptyTitle() {
        Book invalidBook = new Book("", "Autor", 5, "Test", "Roman", "Gelesen");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.createBook(invalidBook)
        );

        assertTrue(exception.getMessage().contains("Titel darf nicht leer sein"));
    }

    @Test
    void shouldRejectBookWithNullTitle() {
        Book invalidBook = new Book(null, "Autor", 5, "Test", "Roman", "Gelesen");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.createBook(invalidBook)
        );

        assertTrue(exception.getMessage().contains("Titel darf nicht leer sein"));
    }

    @Test
    void shouldRejectBookWithEmptyAuthor() {
        Book invalidBook = new Book("Titel", "", 5, "Test", "Roman", "Gelesen");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.createBook(invalidBook)
        );

        assertTrue(exception.getMessage().contains("Autor darf nicht leer sein"));
    }

    @Test
    void shouldSetDefaultGenreIfEmpty() {
        Book bookWithoutGenre = new Book("Test", "Autor", 5, "Test", null, "Gelesen");
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.createBook(bookWithoutGenre);

        assertEquals("Unbekannt", result.getGenre());
    }

    @Test
    void shouldSetDefaultStatusIfEmpty() {
        Book bookWithoutStatus = new Book("Test", "Autor", 5, "Test", "Roman", null);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.createBook(bookWithoutStatus);

        assertEquals("Unbekannt", result.getStatus());
    }

    // ==================== READ Tests ====================

    @Test
    void shouldGetAllBooks() {
        List<Book> books = List.of(validBook);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks(null, null);

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldFilterBooksByRating() {
        when(bookRepository.findByRating(5)).thenReturn(List.of(validBook));

        List<Book> result = bookService.getAllBooks(5, null);

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findByRating(5);
    }

    @Test
    void shouldFilterBooksByGenre() {
        when(bookRepository.findByGenre("Roman")).thenReturn(List.of(validBook));

        List<Book> result = bookService.getAllBooks(null, "Roman");

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findByGenre("Roman");
    }

    @Test
    void shouldGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(validBook));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Testbuch", result.get().getTitle());
    }

    @Test
    void shouldRejectInvalidId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.getBookById(-1L)
        );

        assertTrue(exception.getMessage().contains("ID muss eine positive Zahl sein"));
    }

    // ==================== UPDATE Tests ====================

    @Test
    void shouldUpdateBook() {
        Book existingBook = new Book("Old Title", "Old Author", 3, "Old", "Roman", "Gelesen");
        existingBook.setId(1L);
        Book updatedData = new Book("New Title", "New Author", 5, "New", "Krimi", "Will noch lesen");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.updateBook(1L, updatedData);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(5, result.getRating());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBook() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.updateBook(999L, validBook)
        );

        assertTrue(exception.getMessage().contains("nicht gefunden"));
    }

    // ==================== DELETE Tests ====================

    @Test
    void shouldDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentBook() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.deleteBook(999L)
        );

        assertTrue(exception.getMessage().contains("nicht gefunden"));
        verify(bookRepository, never()).deleteById(any());
    }

    // ==================== SEARCH Tests ====================

    @Test
    void shouldSearchBooks() {
        when(bookRepository.searchByTitleOrAuthor("Harry")).thenReturn(List.of(validBook));

        List<Book> result = bookService.searchBooks("Harry");

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).searchByTitleOrAuthor("Harry");
    }

    @Test
    void shouldRejectEmptySearchQuery() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.searchBooks("")
        );

        assertTrue(exception.getMessage().contains("Suchbegriff darf nicht leer sein"));
    }

    @Test
    void shouldTrimSearchQuery() {
        when(bookRepository.searchByTitleOrAuthor("Harry")).thenReturn(List.of(validBook));

        bookService.searchBooks("  Harry  ");

        verify(bookRepository, times(1)).searchByTitleOrAuthor("Harry");
    }

    // ==================== FILTER Tests ====================

    @Test
    void shouldFilterBooksByGenreAndStatus() {
        when(bookRepository.findByGenreAndStatus("Roman", "Gelesen")).thenReturn(List.of(validBook));

        List<Book> result = bookService.filterBooks("Roman", "Gelesen");

        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findByGenreAndStatus("Roman", "Gelesen");
    }
}