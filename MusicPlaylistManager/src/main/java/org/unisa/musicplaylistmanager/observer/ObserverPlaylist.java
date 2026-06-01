package org.unisa.musicplaylistmanager.observer;

import org.unisa.musicplaylistmanager.playlist.Playlist;

/**
 * @author gruppo10
 */

public class ObserverPlaylist extends BaseObserverPlaylist{

    //METODI
    /**
     * Costruttore
     *
     * @param playlist Playlist che fungerà da osservatore.
     *
     */
    public ObserverPlaylist(Playlist playlist) {
        super(playlist);
    }
}
