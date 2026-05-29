package org.unisa.musicplaylistmanager;

import java.time.Year;

public class Track {

    private String title;
    private String author;
    private String genre;
    private Year year;
    private int seconds;
    private boolean favourite;
    private boolean explicit;
    private boolean newRelease;

    public Track(String title, String author, Year year, String genre, int duration,
                 boolean favourite, boolean explicit, boolean newRelease) {

        validate(title, author, genre, year);

        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.seconds = duration;
        this.favourite = favourite;
        this.explicit = explicit;
        this.newRelease = newRelease;
    }
    // funzione di validazione interna
    private static void validate(String title, String author, String genre, Year year) {
        if (isBlank(title) || isBlank(author) || isBlank(genre) || year == null) {
            throw new IllegalArgumentException("I campi non possono essere vuoti.");
        }
        if (year.getValue() > 2026) {
            throw new IllegalArgumentException("L'anno non può essere superiore al 2026.");
        }
    }
    
    // funzione per verificare se i campi inseriti dall'utente sono vuoti
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Getters
    public String getTitle()      { return title; }
    public String getAuthor()     { return author; }
    public String getGenre()      { return genre; }
    public Year getYear()         { return year; }
    public int getSeconds()       { return seconds; }
    public boolean isFavourite()  { return favourite; }
    public boolean isExplicit()   { return explicit; }
    public boolean isNewRelease() { return newRelease; }

    // Setters
    public void setTitle(String title) {
        if (isBlank(title)) throw new IllegalArgumentException("Il titolo non può essere vuoto.");
        this.title = title;
    }
    public void setAuthor(String author) {
        if (isBlank(author)) throw new IllegalArgumentException("L'autore non può essere vuoto.");
        this.author = author;
    }
    public void setGenre(String genre) {
        if (isBlank(genre)) throw new IllegalArgumentException("Il genere non può essere vuoto.");
        this.genre = genre;
    }
    public void setYear(Year year) {
        if (year == null) throw new IllegalArgumentException("L'anno non può essere nullo.");
        if (year.getValue() > 2026) throw new IllegalArgumentException("L'anno non può essere superiore al 2026.");
        this.year = year;
    }
    public void setSeconds(int seconds)         { this.seconds = seconds; }
    public void setFavourite(boolean favourite)  { this.favourite = favourite; }
    public void setExplicit(boolean explicit)    { this.explicit = explicit; }
    public void setNewRelease(boolean newRelease){ this.newRelease = newRelease; }
}