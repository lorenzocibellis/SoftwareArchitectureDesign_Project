package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;
import org.unisa.musicplaylistmanager.iterator.Iterator;

/**
 * Test class for {@link Iterator}.
 * @author gruppo10
 */
class IteratorTest {
    
    private Playlist playlist;
    private Iterator iterator;
    private Track track1, track2, track3;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Test Playlist");

        track1 = new Track("Bohemian Rhapsody",  "Queen",        Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);
        track3 = new Track("Hotel California",   "Eagles",       Year.of(1977), "Rock", 391, false, false, true);

        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);

        iterator = new Iterator(playlist);
    }

    @Test
    @DisplayName("GetCurrent: dovrebbe restituire la traccia corretta")
    void testGetCurrent() {
        assertNotNull(iterator.getCurrent());
        assertEquals(track1, iterator.getCurrent());
    }

    @Test
    @DisplayName("GetNext: test comportamento circolare")
    void testGetNext_CircularBehavior() {
        assertEquals(track2, iterator.getNext());
        assertEquals(track3, iterator.getNext());
        assertEquals(track1, iterator.getNext());
    }

    @Test
    @DisplayName("GetPrevious: test comportamento circolare")
    void testGetPrevious_CircularBehavior() {
        assertEquals(track3, iterator.getPrevious());
        assertEquals(track2, iterator.getPrevious());
        assertEquals(track1, iterator.getPrevious());
    }

    @Test
    @DisplayName("EmptyPlaylist: gestisce playlist vuota")
    void testEmptyPlaylist() {
        Playlist emptyPlaylist = new Playlist("Vuota");
        Iterator emptyIterator = new Iterator(emptyPlaylist);
        assertNull(emptyIterator.getCurrent());
    }

    @Test
    @DisplayName("SetStrategy: applica correttamente una strategia")
    void testSetStrategy() {
        ExecutionStrategy reverseStrategy = (size, currentIndex) -> new int[]{2, 1, 0};
        iterator.setStrategy(reverseStrategy);
        // Dopo il cambio strategia, la traccia corrente deve essere preservata (track1)
        assertEquals(track1, iterator.getCurrent());
    }

    @Test
    @DisplayName("Constructor: oggetto non nullo")
    void testConstructorNotNull() {
        assertNotNull(iterator);
    }
}