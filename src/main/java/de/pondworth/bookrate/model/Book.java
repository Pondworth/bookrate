package de.pondworth.bookrate.model;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private int rating; // 1 bis 5 Sterne
    private String comment; //kleiner Bewertungskommentar
    private String genre = "Unbekannt"; // 🟡 Standardwert gesetzt
    private String status = "Unbekannt"; // 🟡 Standardwert gesetzt

    // Leerer Konstruktor für JPA
    public Book() {}

    // Konstruktor mit allen Feldern
    public Book(String title, String author, int rating, String comment, String genre, String status) {
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.comment = comment;
        this.genre = genre;
        this.status = status;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
