package org.unisa.musicplaylistmanager.track;

/**
 * @author gruppo10
 */

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.Year;
import java.util.Objects;

public class Track {

    //ATTRIBUTI
    private StringProperty title;
    private StringProperty author;
    private String genre;
    private Year year;
    private int duration;
    private boolean favourite;
    private boolean explicit;
    private boolean newRelease;

    //METODI

    /**
     * Costruttore
     *
     * @param title Titolo della traccia.
     * @param author L'autore della traccia.
     * @param year Anno di pubblicazione.
     * @param genre Genere della traccia.
     * @param duration Durata della traccia
     * @param favourite Flag indicante che la traccia è una tra le preferita
     * @param explicit Flag indicante la presenza di contenuto esplicito
     * @param newRelease Flag indicante che la traccia è una nuova uscita
     *
     */
    public Track(String title, String author, Year year, String genre, int duration,
                 boolean favourite, boolean explicit, boolean newRelease) {

        //Controllo validità dati di input
        validate(title, author, genre, year, duration);

        //setttaggio valori di input come dati della traccia
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.year = year;
        this.genre = genre;
        this.duration = duration;
        this.favourite = favourite;
        this.explicit = explicit;
        this.newRelease = newRelease;
    }


    /**
     * 
     *
     * @param title Titolo della traccia.
     * @param author L'autore della traccia.
     * @param year Anno di pubblicazione.
     * @param genre Genere della traccia.
     * @param duration Durata della traccia
     *
     */
    private static void validate(String title, String author, String genre, Year year, int duration) {
        if (isBlank(title) || isBlank(author) || isBlank(genre) || year == null) {
            throw new IllegalArgumentException("I campi non possono essere vuoti.");
        }
        if (!validateYear(year)) {
            throw new IllegalArgumentException("L'anno non può essere superiore all'anno attuale.");
        }
        // Validazione aggiuntiva per la durata positiva
        if (!validateDuration(duration)) {
            throw new IllegalArgumentException("La durata non può essere negativa.");
        }
    }

    //verifica se l'anno è stato inserito e se è valido
    private static boolean validateYear(Year y){
        return !(y != null && y.getValue() > Year.now().getValue());
    }

    //verifica che la durata sia >0
    private static boolean validateDuration(int duration){ return duration >= 0;}

    //funzione per verificare se i campi inseriti dall'utente sono vuoti
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Getters
    public String getTitle()      { return title.get(); }
    public String getAuthor()     { return author.get(); }
    public String getGenre()      { return genre; }
    public Year getYear()         { return year; }
    public int getDuration()      { return duration; }
    public boolean isFavourite()    { return favourite; }
    public boolean isExplicitContent() { return explicit; }
    public boolean isNewRelease()   { return newRelease; }

    // Setters
    public void setTitle(String title) {
        if (isBlank(title)) throw new IllegalArgumentException("Il titolo non può essere vuoto.");
        this.title.set(title);
    }
    public void setAuthor(String author) {
        if (isBlank(author)) throw new IllegalArgumentException("L'autore non può essere vuoto.");
        this.author.set(author);
    }
    public void setGenre(String genre) {
        if (isBlank(genre)) throw new IllegalArgumentException("Il genere non può essere vuoto.");
        this.genre = genre;
    }
    public void setYear(Year year) {
        if (validateYear(year)) throw new IllegalArgumentException("L'anno è obbligatorio e non può essere superiore all'anno corrente.");
        this.year = year;
    }
    public void setDuration(int duration) {
        if (duration < 0) throw new IllegalArgumentException("La durata non può essere negativa.");
        this.duration = duration;
    }
    public void setFavourite(boolean favourite)  { this.favourite = favourite; }
    public void setExplicit(boolean explicit)    { this.explicit = explicit; }
    public void setNewRelease(boolean newRelease){ this.newRelease = newRelease; }

    // --- Metodi Property (Nuovi, necessari per implementare il pattern Observer tramite Binding) ---
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }

    //Override metodo per uguaglianza tra tracce
    @Override
    public boolean equals(Object t){
        if (this == t) return true;
        if ((t == null) || this.getClass() != t.getClass()) return false;
        Track track = (Track) t;
        return track.getTitle().equals(this.getTitle()) && track.getAuthor().equals(this.getAuthor()) && track.getYear().equals(this.getYear());
    }

    //Override metodo di hashCode
    @Override
    public int hashCode(){
        return (getTitle() + getAuthor() + getYear()).hashCode();
    }

    //Override metodo per print di dati della traccia
    @Override
    public String toString(){
        return this.getTitle() + " | " + this.getAuthor() + " | " + this.getYear();
    }
}