package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Playlist}.
 * @author gruppo10
 */
class PlaylistTest {

    private Playlist playlist;

    private Track track1;
    private Track track2;
    private Track track3;

    // -----------------------------------------------------------------------
    // Setup
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        playlist = new Playlist("My Playlist");

        track1 = new Track("Bohemian Rhapsody",  "Queen",        Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);
        track3 = new Track("Hotel California",   "Eagles",       Year.of(1977), "Rock", 391, false, false, true);
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: oggetto creato correttamente")
    void testConstructorNotNull() {
        assertNotNull(playlist);
    }

    @Test
    @DisplayName("Constructor: nome impostato correttamente")
    void testConstructorName() {
        assertEquals("My Playlist", playlist.getName());
    }

    @Test
    @DisplayName("Constructor: nome null è accettato")
    void testConstructorNullName() {
        Playlist p = new Playlist(null);
        assertNull(p.getName());
    }

    @Test
    @DisplayName("Constructor: getTracks() restituisce lista non null e vuota")
    void testConstructorEmptyTracks() {
        assertNotNull(playlist.getTracks());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    @DisplayName("Constructor: getSize() è 0")
    void testConstructorSizeZero() {
        assertEquals(0, playlist.getSize());
    }

    // -----------------------------------------------------------------------
    // setName() / getName()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setName: aggiorna il nome correttamente")
    void testSetName() {
        playlist.setName("Rock Hits");
        assertEquals("Rock Hits", playlist.getName());
    }

    @Test
    @DisplayName("setName: sovrascrive un nome esistente")
    void testSetNameOverwrite() {
        playlist.setName("First");
        playlist.setName("Second");
        assertEquals("Second", playlist.getName());
    }

    @Test
    @DisplayName("setName: accetta null")
    void testSetNameNull() {
        playlist.setName(null);
        assertNull(playlist.getName());
    }

    // -----------------------------------------------------------------------
    // addTrack()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("addTrack: aggiunge una traccia, size diventa 1")
    void testAddTrackSingle() {
        playlist.addTrack(track1);
        assertEquals(1, playlist.getSize());
        assertTrue(playlist.getTracks().contains(track1));
    }

    @Test
    @DisplayName("addTrack: aggiunge più tracce mantenendo l'ordine di inserimento")
    void testAddTrackMultiple() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);

        assertEquals(3, playlist.getSize());
        assertEquals(track1, playlist.getTracks().get(0));
        assertEquals(track2, playlist.getTracks().get(1));
        assertEquals(track3, playlist.getTracks().get(2));
    }

    @Test
    @DisplayName("addTrack: duplicato per identità lancia IllegalArgumentException")
    void testAddTrackDuplicateSameReference() {
        playlist.addTrack(track1);
        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(track1));
    }

    @Test
    @DisplayName("addTrack: duplicato per equals (titolo/autore/anno uguali) lancia IllegalArgumentException")
    void testAddTrackDuplicateByEquality() {
        playlist.addTrack(track1);

        Track copy = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Pop", 99, false, true, true);

        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(copy));
    }

    @Test
    @DisplayName("addTrack: tracce diverse (titolo/autore/anno diversi) vengono aggiunte senza eccezioni")
    void testAddTrackNoDuplicate() {
        playlist.addTrack(track1);
        assertDoesNotThrow(() -> playlist.addTrack(track2));
        assertEquals(2, playlist.getSize());
    }

    // -----------------------------------------------------------------------
    // removeTrack()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("removeTrack: rimuove una traccia presente")
    void testRemoveTrackPresent() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);

        playlist.removeTrack(track1);

        assertEquals(1, playlist.getSize());
        assertFalse(playlist.getTracks().contains(track1));
        assertTrue(playlist.getTracks().contains(track2));
    }

    @Test
    @DisplayName("removeTrack: rimuovere una traccia assente non lancia eccezioni")
    void testRemoveTrackNotPresent() {
        playlist.addTrack(track1);
        assertDoesNotThrow(() -> playlist.removeTrack(track2));
        assertEquals(1, playlist.getSize());
    }

    @Test
    @DisplayName("removeTrack: lista vuota dopo rimozione dell'unica traccia")
    void testRemoveTrackUntilEmpty() {
        playlist.addTrack(track1);
        playlist.removeTrack(track1);
        assertTrue(playlist.getTracks().isEmpty());
        assertEquals(0, playlist.getSize());
    }

    @Test
    @DisplayName("removeTrack: rimuove per equals, non per identità di riferimento")
    void testRemoveTrackByEquality() {
        playlist.addTrack(track1);

        Track copy = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Pop", 200, false, true, true);

        playlist.removeTrack(copy);
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    @DisplayName("removeTrack: rimuove solo la traccia specificata, le altre restano")
    void testRemoveTrackOnlyTarget() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);

        playlist.removeTrack(track2);

        assertEquals(2, playlist.getSize());
        assertTrue(playlist.getTracks().contains(track1));
        assertFalse(playlist.getTracks().contains(track2));
        assertTrue(playlist.getTracks().contains(track3));
    }

    @Test
    @DisplayName("removeTrack su lista vuota non lancia eccezioni")
    void testRemoveTrackFromEmptyList() {
        assertDoesNotThrow(() -> playlist.removeTrack(track1));
        assertEquals(0, playlist.getSize());
    }

    // -----------------------------------------------------------------------
    // getSize()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getSize: si aggiorna correttamente dopo addTrack")
    void testGetSizeAfterAdds() {
        assertEquals(0, playlist.getSize());
        playlist.addTrack(track1);
        assertEquals(1, playlist.getSize());
        playlist.addTrack(track2);
        assertEquals(2, playlist.getSize());
        playlist.addTrack(track3);
        assertEquals(3, playlist.getSize());
    }

    @Test
    @DisplayName("getSize: si aggiorna correttamente dopo removeTrack")
    void testGetSizeAfterRemoves() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.removeTrack(track1);
        assertEquals(1, playlist.getSize());
        playlist.removeTrack(track2);
        assertEquals(0, playlist.getSize());
    }

    // -----------------------------------------------------------------------
    // Metodi che lanciano UnsupportedOperationException
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getTrack: lancia UnsupportedOperationException")
    void testGetTrackUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> playlist.getTrack());
    }

    @Test
    @DisplayName("getIndex: lancia UnsupportedOperationException")
    void testGetIndexUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> playlist.getIndex(track1));
    }

    @Test
    @DisplayName("undo: lancia UnsupportedOperationException")
    void testUndoUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> playlist.undo());
    }
}

