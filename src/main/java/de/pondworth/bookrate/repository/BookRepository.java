package de.pondworth.bookrate.repository;

import de.pondworth.bookrate.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}