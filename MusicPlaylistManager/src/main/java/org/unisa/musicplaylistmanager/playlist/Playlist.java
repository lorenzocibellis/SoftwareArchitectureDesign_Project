package org.unisa.musicplaylistmanager.playlist;

/**
 * Rappresenta una playlist musicale, ovvero una collezione ordinata di tracce.
 * Questa classe fornisce metodi per gestire l'aggiunta, la rimozione e la modifica
 * di tracce all'interno della playlist.
 *
 * @author gruppo10
 */
import org.unisa.musicplaylistmanager.observer.BaseObserver;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;

public class Playlist extends TrackCollection implements BaseObserver {

    /**
     * Costruisce una nuova playlist con il nome specificato.
     * 
     * @param name il nome della playlist
     */
    public Playlist(String name) {
        super(name);
        TrackList.getTrackListPointer().attach(this);
    }


    /**
     * Annulla l'ultima operazione (non supportato al momento).
     * 
     * @throws UnsupportedOperationException sempre
     */
    public void undo(){
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Track track) {
        this.removeTrack(track);
    }
}