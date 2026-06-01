package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TrackList}.
 * @author gruppo10
 */
class TrackListTest {

    private TrackList trackList;

    private Track track1;
    private Track track2;
    private Track track3;

    // -----------------------------------------------------------------------
    // Setup
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        trackList = new TrackList();

        track1 = new Track("Bohemian Rhapsody",  "Queen",         Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin",  Year.of(1971), "Rock", 482, false, false, false);
        track3 = new Track("Hotel California",   "Eagles",        Year.of(1977), "Rock", 391, false, false, true);
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: oggetto creato correttamente")
    void testConstructorNotNull() {
        assertNotNull(trackList);
    }

    @Test
    @DisplayName("Constructor: getTracks() restituisce una lista non null e vuota")
    void testConstructorEmptyTracks() {
        assertNotNull(trackList.getTracks());
        assertTrue(trackList.getTracks().isEmpty());
    }

    @Test
    @DisplayName("Constructor: il nome è null (super(null))")
    void testConstructorNullName() {
        assertNull(trackList.getName());
    }

    @Test
    @DisplayName("Constructor: getSize() è 0")
    void testConstructorSizeZero() {
        assertEquals(0, trackList.getSize());
    }

    // -----------------------------------------------------------------------
    // setName() / getName() – ereditati da Playlist
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setName: aggiorna il nome correttamente")
    void testSetName() {
        trackList.setName("My TrackList");
        assertEquals("My TrackList", trackList.getName());
    }

    @Test
    @DisplayName("setName: sovrascrive un nome già impostato")
    void testSetNameOverwrite() {
        trackList.setName("First");
        trackList.setName("Second");
        assertEquals("Second", trackList.getName());
    }

    // -----------------------------------------------------------------------
    // addTrack() – ereditato da Playlist
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("addTrack: aggiunge una traccia, size diventa 1")
    void testAddTrackSingle() {
        trackList.addTrack(track1);
        assertEquals(1, trackList.getSize());
        assertTrue(trackList.getTracks().contains(track1));
    }

    @Test
    @DisplayName("addTrack: aggiunge più tracce mantenendo l'ordine di inserimento")
    void testAddTrackMultiple() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        trackList.addTrack(track3);

        assertEquals(3, trackList.getSize());
        assertEquals(track1, trackList.getTracks().get(0));
        assertEquals(track2, trackList.getTracks().get(1));
        assertEquals(track3, trackList.getTracks().get(2));
    }

    @Test
    @DisplayName("addTrack: aggiungere un duplicato (equals) lancia IllegalArgumentException")
    void testAddTrackDuplicateThrows() {
        trackList.addTrack(track1);

        // stessa identità
        assertThrows(IllegalArgumentException.class, () -> trackList.addTrack(track1));
    }

    @Test
    @DisplayName("addTrack: duplicato per equals (titolo/autore/anno uguali) lancia IllegalArgumentException")
    void testAddTrackDuplicateByEquality() {
        trackList.addTrack(track1);

        Track sameTrack = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Pop", 200, false, true, true);

        assertThrows(IllegalArgumentException.class, () -> trackList.addTrack(sameTrack));
    }

    // -----------------------------------------------------------------------
    // removeTrack() – @Override in TrackList
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("removeTrack: rimuove correttamente una traccia presente")
    void testRemoveTrackPresent() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);

        trackList.removeTrack(track1);

        assertEquals(1, trackList.getSize());
        assertFalse(trackList.getTracks().contains(track1));
        assertTrue(trackList.getTracks().contains(track2));
    }

    @Test
    @DisplayName("removeTrack: rimuovere una traccia assente non lancia eccezioni")
    void testRemoveTrackNotPresent() {
        trackList.addTrack(track1);
        assertDoesNotThrow(() -> trackList.removeTrack(track2));
        assertEquals(1, trackList.getSize());
    }

    @Test
    @DisplayName("removeTrack: lista vuota dopo rimozione dell'unica traccia")
    void testRemoveTrackUntilEmpty() {
        trackList.addTrack(track1);
        trackList.removeTrack(track1);
        assertTrue(trackList.getTracks().isEmpty());
        assertEquals(0, trackList.getSize());
    }

    @Test
    @DisplayName("removeTrack: rimuove per equals (titolo/autore/anno), non per identità di riferimento")
    void testRemoveTrackByEquality() {
        trackList.addTrack(track1);

        Track sameTrack = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Pop", 200, false, true, true);

        trackList.removeTrack(sameTrack);
        assertTrue(trackList.getTracks().isEmpty());
    }

    @Test
    @DisplayName("removeTrack: rimuove solo la traccia specificata, le altre restano intatte")
    void testRemoveTrackOnlyTarget() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        trackList.addTrack(track3);

        trackList.removeTrack(track2);

        assertEquals(2, trackList.getSize());
        assertTrue(trackList.getTracks().contains(track1));
        assertFalse(trackList.getTracks().contains(track2));
        assertTrue(trackList.getTracks().contains(track3));
    }

    @Test
    @DisplayName("removeTrack su lista vuota non lancia eccezioni")
    void testRemoveTrackFromEmptyList() {
        assertDoesNotThrow(() -> trackList.removeTrack(track1));
        assertEquals(0, trackList.getSize());
    }

    // -----------------------------------------------------------------------
    // getSize() – ereditato da Playlist
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getSize: riflette correttamente le aggiunte")
    void testGetSizeAfterAdds() {
        assertEquals(0, trackList.getSize());
        trackList.addTrack(track1);
        assertEquals(1, trackList.getSize());
        trackList.addTrack(track2);
        assertEquals(2, trackList.getSize());
    }

    @Test
    @DisplayName("getSize: riflette correttamente le rimozioni")
    void testGetSizeAfterRemoves() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        trackList.removeTrack(track1);
        assertEquals(1, trackList.getSize());
    }

    // -----------------------------------------------------------------------
    // Metodi che lanciano UnsupportedOperationException (ereditati da Playlist)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getTrack: lancia UnsupportedOperationException")
    void testGetTrackUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> trackList.getTrack());
    }

    @Test
    @DisplayName("getIndex: lancia UnsupportedOperationException")
    void testGetIndexUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> trackList.getIndex(track1));
    }

    @Test
    @DisplayName("undo: lancia UnsupportedOperationException")
    void testUndoUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> trackList.undo());
    }

    // -----------------------------------------------------------------------
    // Ereditarietà
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("TrackList è un'istanza di Playlist")
    void testIsInstanceOfPlaylist() {
        assertInstanceOf(Playlist.class, trackList);
    }
}
