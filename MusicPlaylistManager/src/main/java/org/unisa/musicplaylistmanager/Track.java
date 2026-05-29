package org.unisa.musicplaylistmanager;

import java.time.Year;

public class Track {

    //definizione attributi privati
    private String title;
    private String author;
    private String genre;
    private Year year;
    private int seconds;
    private boolean favourite;
    private boolean explicit;
    private boolean newRelease;

    //METODI
    //Costruttore
    public Track(String title, String author, Year year, String genre, int duration, boolean favourite,
                 boolean explicit, boolean newRelease){
        this.title = title;
        this.author = author;
        this.seconds = duration;
        this.year = year;
        this.genre = genre;
        this.favourite = favourite;
        this.newRelease = newRelease;
        this.explicit = explicit;
    }

    //Metodi getter
    public String getTitle() {
        return title;
    }

    public Year getYear() {
        return year;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public boolean isNewRelease() {
        return newRelease;
    }

    //Metodi setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public void setNewRelease(boolean newRelease) {
        this.newRelease = newRelease;
    }
}
