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
    //Costruttore
    public BaseObserverPlaylist(Playlist playlist){
        this.playlist = playlist;
    }

    //metodo per aggiornare la playlist osservatrice riguardo un evento (eliminazione traccia)
    public void update(Track track){
        playlist.removeTrack(track);
    }
}
