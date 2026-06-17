package org.unisa.musicplaylistmanager.core.iterator;

import org.unisa.musicplaylistmanager.player.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.track.model.Track;

/**
 * @author gruppo10
 */

public interface AbstractIterator {
    // dichiarazione dei metodi per lo scorrimento delle tracce
    Track getCurrent();
    Track getNext();
    Track getPrevious();
    void setStrategy(ExecutionStrategy strategy);
    void moveToTrack(Track track);
    String getIdentifier();
}