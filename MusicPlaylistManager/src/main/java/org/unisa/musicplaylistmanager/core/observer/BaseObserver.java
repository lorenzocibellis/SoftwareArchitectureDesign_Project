package org.unisa.musicplaylistmanager.core.observer;

import org.unisa.musicplaylistmanager.track.model.Track;

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
