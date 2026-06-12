package org.unisa.musicplaylistmanager.playlist;

/**
 * Rappresenta una playlist musicale, ovvero una collezione ordinata di tracce.
 * Questa classe fornisce metodi per gestire l'aggiunta, la rimozione e la modifica
 * di tracce all'interno della playlist.
 *
 * @author gruppo10
 */
import javafx.scene.control.Alert;
import org.unisa.musicplaylistmanager.observer.BaseObserver;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;

public class Playlist extends TrackCollection implements BaseObserver, MostPlayed {

    //ATTRIBUTI
    // indica il numero di volte che la playlist è stata ascoltata
    private int numOfPlay;

    /**
     * Costruisce una nuova playlist con il nome specificato.
     * 
     * @param name il nome della playlist
     *
     * @throws IllegalArgumentException Quando il nome della playlist è null
     */
    public Playlist(String name) {
        super(name);
        if(name == null || name.equals(TrackList.TRACKLIST_NAME)) {
            throw new IllegalArgumentException("Non è possibile creare una playlist con questo nome!");
        }
        numOfPlay = 0;
        TrackList.getTrackListPointer().attach(this);
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
    public int getNumOfPlay(){ return numOfPlay;}

    /**
     *
     * Incrementa di 1 il numero di ascolti della playlist
     *
     */
    public void incrementNumOfPlay(){ numOfPlay += 1;}
}