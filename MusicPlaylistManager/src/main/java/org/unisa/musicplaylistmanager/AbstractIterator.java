package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

public interface AbstractIterator {
    // dichiarazione dei metodi per lo scorrimento delle tracce
    Track getCurrent();
    Track getNext();
    Track getPrevious();
    void setStrategy(ExecutionStrategy strategy);
}