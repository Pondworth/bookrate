package de.pondworth.bookrate.controller;

import de.pondworth.bookrate.model.Book;
import de.pondworth.bookrate.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // 📘 1. Alle Bücher anzeigen oder nach Rating filtern
    @GetMapping
    public List<Book> getAllBooks(@RequestParam(required = false) Integer rating) {
        if (rating != null) {
            return bookRepository.findByRating(rating);
        } else {
            return bookRepository.findAll();
        }
    }

    // 📘 2. Buch nach ID anzeigen
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 📘 3. Neues Buch hinzufügen
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    // 📘 4. Buch aktualisieren
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setRating(updatedBook.getRating());
                    book.setComment(updatedBook.getComment());
                    bookRepository.save(book);
                    return ResponseEntity.ok(book);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 📘 5. Buch löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔍 6. Suche nach Titel oder Autor
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String query) {
        return bookRepository.searchByTitleOrAuthor(query);
    }
}
