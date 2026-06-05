package org.unisa.musicplaylistmanager.iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Iterator}.
 * @author gruppo10
 */
class IteratorTest {

    private Playlist playlist;
    private Iterator iterator;

    private Track track1;
    private Track track2;
    private Track track3;

    // -----------------------------------------------------------------------
    // Setup
    // -----------------------------------------------------------------------

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

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: oggetto creato correttamente con playlist valida")
    void testConstructorNotNull() {
        assertNotNull(iterator);
    }

    @Test
    @DisplayName("Constructor: accetta playlist vuota senza eccezioni")
    void testConstructorEmptyPlaylist() {
        Playlist empty = new Playlist("Empty");
        assertDoesNotThrow(() -> new Iterator(empty));
    }

    @Test
    @DisplayName("Constructor: accetta playlist null senza eccezioni")
    void testConstructorNullPlaylist() {
        assertDoesNotThrow(() -> new Iterator(null));
    }

    // -----------------------------------------------------------------------
    // createIterator()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createIterator: restituisce null (comportamento attuale)")
    void testCreateIteratorReturnsNull() {
        assertNull(iterator.createIterator());
    }

    // -----------------------------------------------------------------------
    // Metodi non ancora implementati – UnsupportedOperationException
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getCurrent: lancia UnsupportedOperationException")
    void testGetCurrentUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> iterator.getCurrent());
    }

    @Test
    @DisplayName("getNext: lancia UnsupportedOperationException")
    void testGetNextUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> iterator.getNext());
    }

    @Test
    @DisplayName("getPrevious: lancia UnsupportedOperationException")
    void testGetPreviousUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> iterator.getPrevious());
    }

    @Test
    @DisplayName("setStrategy: lancia UnsupportedOperationException")
    void testSetStrategyUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> iterator.setStrategy(null));
    }

    // -----------------------------------------------------------------------
    // Ereditarietà
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Iterator implementa AbstractIterator")
    void testImplementsAbstractIterator() {
        assertTrue(iterator instanceof AbstractIterator);
    }
}