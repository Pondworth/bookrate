package de.pondworth.bookrate.service;

import de.pondworth.bookrate.model.Book;
import de.pondworth.bookrate.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // Constructor Injection (Best Practice)
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ==================== CRUD Operationen ====================

    /**
     * Gibt alle Bücher zurück oder filtert nach Rating und/oder Genre
     */
    public List<Book> getAllBooks(Integer rating, String genre) {
        // Validierung
        if (rating != null) {
            validateRating(rating);
        }

        // Filterlogik
        if (rating != null && genre != null) {
            return bookRepository.findByRatingAndGenre(rating, genre);
        } else if (rating != null) {
            return bookRepository.findByRating(rating);
        } else if (genre != null) {
            return bookRepository.findByGenre(genre);
        } else {
            return bookRepository.findAll();
        }
    }

    /**
     * Gibt ein Buch anhand der ID zurück
     */
    public Optional<Book> getBookById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID muss eine positive Zahl sein!");
        }
        return bookRepository.findById(id);
    }

    /**
     * Erstellt ein neues Buch
     */
    public Book createBook(Book book) {
        // Validierung
        validateBook(book);

        // Geschäftslogik: Standardwerte setzen falls nicht vorhanden
        if (book.getGenre() == null || book.getGenre().isEmpty()) {
            book.setGenre("Unbekannt");
        }
        if (book.getStatus() == null || book.getStatus().isEmpty()) {
            book.setStatus("Unbekannt");
        }

        // Speichern
        return bookRepository.save(book);
    }

    /**
     * Aktualisiert ein bestehendes Buch
     */
    public Book updateBook(Long id, Book updatedBook) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID muss eine positive Zahl sein!");
        }

        // Validierung des aktualisierten Buchs
        validateBook(updatedBook);

        // Buch suchen
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buch mit ID " + id + " nicht gefunden!"));

        // Felder aktualisieren
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setRating(updatedBook.getRating());
        existingBook.setComment(updatedBook.getComment());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setStatus(updatedBook.getStatus());

        return bookRepository.save(existingBook);
    }

    /**
     * Löscht ein Buch
     */
    public void deleteBook(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID muss eine positive Zahl sein!");
        }

        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Buch mit ID " + id + " nicht gefunden!");
        }

        bookRepository.deleteById(id);
    }

    // ==================== Such- und Filterfunktionen ====================

    /**
     * Sucht Bücher nach Titel oder Autor
     */
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Suchbegriff darf nicht leer sein!");
        }
        return bookRepository.searchByTitleOrAuthor(query.trim());
    }

    /**
     * Filtert Bücher nach Genre und/oder Status
     */
    public List<Book> filterBooks(String genre, String status) {
        if (genre != null && status != null) {
            return bookRepository.findByGenreAndStatus(genre, status);
        } else if (genre != null) {
            return bookRepository.findByGenre(genre);
        } else if (status != null) {
            return bookRepository.findByStatus(status);
        } else {
            return bookRepository.findAll();
        }
    }

    // ==================== Validierung ====================

    /**
     * Validiert ein Buch-Objekt
     */
    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Buch darf nicht null sein!");
        }

        // Titel validieren
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Titel darf nicht leer sein!");
        }

        // Autor validieren
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Autor darf nicht leer sein!");
        }

        // Rating validieren
        validateRating(book.getRating());
    }

    /**
     * Validiert ein Rating (muss zwischen 1 und 5 sein)
     */
    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating muss zwischen 1 und 5 liegen! Aktuell: " + rating);
        }
    }
}