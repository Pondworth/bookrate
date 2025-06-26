package de.pondworth.bookrate.repository;

import de.pondworth.bookrate.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    // 🔍 Suche nach Titel oder Autor (case-insensitive)
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchByTitleOrAuthor(@Param("query") String query);

    // ⭐ Filter nach Bewertung
    List<Book> findByRating(int rating);
}
