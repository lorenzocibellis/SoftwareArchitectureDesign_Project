package org.unisa.musicplaylistmanager.observer;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

/**
 * @author gruppo10
 */

public abstract class BaseObserverPlaylist {
    //Definizione attributi
    private Playlist playlist;

    //METODI
    /**
     * Costruttore
     *
     * @param playlist Playlist che osserverà la lista di tracce.
     *
     */
    public BaseObserverPlaylist(Playlist playlist){
        this.playlist = playlist;
    }

    /**
     *
     * @param track Traccia eliminata da propagare nella playlist.
     *
     */
    public void update(Track track){
        playlist.removeTrack(track);
    }
}
