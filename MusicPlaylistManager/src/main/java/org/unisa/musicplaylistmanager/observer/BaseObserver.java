package org.unisa.musicplaylistmanager.observer;

import org.unisa.musicplaylistmanager.track.Track;

/**
 * @author gruppo10
 */

public interface BaseObserver {
    //METODI
    /**
     *
     * @param track Traccia eliminata da propagare nella playlist.
     *
     */
    public void update(Track track);
}
