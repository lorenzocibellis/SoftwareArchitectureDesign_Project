package org.unisa.musicplaylistmanager.track;

/**
 * Rappresenta una traccia musicale all'interno del sistema.
 * 
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
     * Costruisce una nuova traccia musicale verificandone la correttezza dei dati.
     *
     * @param title Titolo della traccia
     * @param author L'autore della traccia
     * @param year Anno di pubblicazione
     * @param genre Genere della traccia
     * @param duration Durata della traccia in secondi
     * @param favourite {@code true} se è tra i preferiti, {@code false} altrimenti
     * @param explicit {@code true} se contiene contenuti espliciti, {@code false} altrimenti
     * @param newRelease {@code true} se è una nuova uscita, {@code false} altrimenti
     * @throws IllegalArgumentException se i parametri non superano la validazione
     */
    public Track(String title, String author, Year year, String genre, int duration,
                 boolean favourite, boolean explicit, boolean newRelease) {

        //Controllo validità dati di input
        validate(title, author, genre, year, duration);

        //settaggio valori di input come dati della traccia
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.year = year;
        this.genre = genre;
        this.duration = duration;
        this.favourite = favourite;
        this.explicit = explicit;
        this.newRelease = newRelease;
    }


    // METODI UTILITARI
    /**
     * Metodo per la validazione interna di dati.
     *
     * @param title Titolo della traccia.
     * @param author L'autore della traccia.
     * @param year Anno di pubblicazione.
     * @param genre Genere della traccia.
     * @param duration Durata della traccia
     *
     * @return true se i dati sono validi, false altrimenti
     */
    private static void validate(String title, String author, String genre, Year year, int duration) {
        if (isBlank(title) || isBlank(author) || isBlank(genre) || year == null) {
            throw new IllegalArgumentException("I campi non possono essere vuoti.");
        }
        if (!validateYear(year)) {
            throw new IllegalArgumentException("L'anno non può essere superiore all'anno attuale.");
        }
        // Validazione aggiuntiva per la durata strettamente positiva
        if (!validateDuration(duration)) {
            throw new IllegalArgumentException("La durata deve essere maggiore di 0.");
        }
    }

    /**
     * Metodo di supporto per la validazione dell'anno.
     *
     * @param y Anno da validare.
     *
     * @return true se l'anno non è nullo e minore o uguale all'anno attuale, false altrimenti
     */
    //verifica se l'anno è stato inserito e se è valido
    private static boolean validateYear(Year y){
        return y != null && y.getValue() <= Year.now().getValue();
    }

    /**
     * Metodo di supporto per la validazione della durata.
     * Controlla che essa sia > 0.
     *
     * @param duration Durata da validare.
     *
     * @return true se la durata è > 0, false altrimenti
     */
    private static boolean validateDuration(int duration){ return duration > 0;}

    /**
     * Metodo di supporto per controllare se una stringa è vuota.
     *
     * @param s Stringa da controllare.
     *
     * @return true se la stringa non è vuota, false altrimenti
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // METODI PUBBLICI

    // Getters

    /** @return Il titolo della traccia */
    public String getTitle()      { return title.get(); }
    /** @return L'autore della traccia */
    public String getAuthor()     { return author.get(); }
    /** @return Il genere musicale della traccia */
    public String getGenre()      { return genre; }
    /** @return L'anno di pubblicazione della traccia */
    public Year getYear()         { return year; }
    /** @return La durata della traccia in secondi */
    public int getDuration()      { return duration; }
    /** @return {@code true} se la traccia è preferita */
    public boolean isFavourite()    { return favourite; }
    /** @return {@code true} se la traccia ha contenuti espliciti */
    public boolean isExplicitContent() { return explicit; }
    /** @return {@code true} se la traccia è una nuova uscita */
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
        if (!validateYear(year)) throw new IllegalArgumentException("L'anno è obbligatorio e non può essere superiore all'anno corrente.");
        this.year = year;
    }
    public void setDuration(int duration) {
        if (duration <= 0) throw new IllegalArgumentException("La durata deve essere maggiore di 0.");
        this.duration = duration;
    }
    public void setFavourite(boolean favourite)  { this.favourite = favourite; }
    public void setExplicit(boolean explicit)    { this.explicit = explicit; }
    public void setNewRelease(boolean newRelease){ this.newRelease = newRelease; }

    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }


    /**
     * Metodo per controllare che l'oggetto chiamante sia uguale all'oggetto passato come parametro.
     * Due tracce sono considerate uguali se hanno lo stesso titolo, lo stesso autore e lo stesso anno.
     *
     * @param o Oggetto da controllare.
     * @return {@code true} se i due oggetti sono uguali, {@code false} altrimenti
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if ((o == null) || this.getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return track.getTitle().equals(this.getTitle()) && track.getAuthor().equals(this.getAuthor()) && track.getYear().equals(this.getYear());
    }

    /**
     * Metodo per la generazione del codice hash dell'oggetto chiamante.
     * Basato su titolo, autore e anno.
     *
     * @return Codice hash dell'oggetto
     */
    @Override
    public int hashCode(){
        return (getTitle() + getAuthor() + getYear()).hashCode();
    }

    /**
     * Metodo per la generazione della stringa descrittiva della traccia.
     *
     * @return stringa formattata con Titolo | Autore | Anno
     */
    @Override
    public String toString(){
        return this.getTitle() + " | " + this.getAuthor() + " | " + this.getYear();
    }
}