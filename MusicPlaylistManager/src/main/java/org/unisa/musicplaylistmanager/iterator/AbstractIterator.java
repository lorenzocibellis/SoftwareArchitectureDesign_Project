package org.unisa.musicplaylistmanager.iterator;

import org.unisa.musicplaylistmanager.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.track.Track;

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