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

public class Playlist extends TrackCollection implements BaseObserver {

    /**
     * Costruisce una nuova playlist con il nome specificato.
     * 
     * @param name il nome della playlist
     *
     * @throws IllegalArgumentException Quando il nome della playlist è null
     */
    public Playlist(String name) {
        super(name);
        if(name == null || name.equals(super.TRACKLIST_NAME)) {
            throw new IllegalArgumentException("Non è possibile creare una playlist con questo nome!");
        }
        TrackList.getTrackListPointer().attach(this);
    }


    @Override
    public void update(Track track) {
        this.removeTrack(track);
    }
}