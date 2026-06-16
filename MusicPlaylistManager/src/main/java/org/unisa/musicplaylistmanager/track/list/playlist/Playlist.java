package org.unisa.musicplaylistmanager.track.list.playlist;

/**
 * Rappresenta una playlist musicale, ovvero una collezione ordinata di tracce.
 * Questa classe fornisce metodi per gestire l'aggiunta, la rimozione e la modifica
 * di tracce all'interno della playlist.
 *
 * @author gruppo10
 */
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.unisa.musicplaylistmanager.observer.BaseObserver;
import org.unisa.musicplaylistmanager.service.statistics.MostPlayed;
import org.unisa.musicplaylistmanager.track.Track;
import javafx.beans.property.IntegerProperty;
import org.unisa.musicplaylistmanager.track.list.TrackCollection;

public class Playlist extends TrackCollection implements BaseObserver, MostPlayed {

    //ATTRIBUTI
    // indica il numero di volte che la playlist è stata ascoltata
    private final IntegerProperty numOfPlay = new SimpleIntegerProperty(0);

    /**
     * Costruisce una nuova playlist con il nome specificato.
     * 
     * @param name il nome della playlist
     *
     * @throws IllegalArgumentException Quando il nome della playlist è null
     */
    public Playlist(String name) {
        super(name);
    }


    /**
     * Imposta o modifica il nome della playlist.
     *
     * @param name il nuovo nome
     */
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void update(Track track) {
        this.removeTrack(track);
    }

    /**
     *
     * Restituisce il numero di volte che la playlist è stata ascoltata.
     *
     * @return numOfPlay Numero di volte che la playlist è stata ascoltata.
     *
     */
    @Override
    public int getNumOfPlay(){ return numOfPlay.get();}

    /**
     *
     * Incrementa di 1 il numero di ascolti della playlist
     *
     */
    @Override
    public void incrementNumOfPlay(){ numOfPlay.set(numOfPlay.get() + 1);}

    @Override
    public ReadOnlyIntegerProperty playCountProperty() {
        return numOfPlay;
    }

}