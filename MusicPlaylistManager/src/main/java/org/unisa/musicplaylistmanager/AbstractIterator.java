package org.unisa.musicplaylistmanager;

/**
 *
 * @author gruppo10
 */
public interface AbstractIterator extends IterableCollection {
    
    // dichiarazione dei metodi per lo scorrimento delle tracce nella tracklist o nella playlist
    public Track getCurrent();
    public Track getNext();
    public Track getPrevious();
    public void setStrategy(ExecutionStrategy);
    
}
