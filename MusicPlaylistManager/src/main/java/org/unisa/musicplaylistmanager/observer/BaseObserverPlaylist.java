package org.unisa.musicplaylistmanager.observer;

import org.unisa.musicplaylistmanager.playlist.Playlist;

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

    public void update(){
        throw new UnsupportedOperationException();
    }
}
