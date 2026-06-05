package org.unisa.musicplaylistmanager.track;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.unisa.musicplaylistmanager.playlist.Playlist;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TrackList}.
 *
 * ATTENZIONE: TrackList usa il pattern Singleton tramite campo statico (pnt).
 * Ogni test che chiama new TrackList() sovrascrive il puntatore globale.
 *
 * @author gruppo10
 */
class TrackListTest {

    private TrackList trackList;
    private Track track1;
    private Track track2;
    private Track track3;

    @BeforeEach
    void setUp() {
        trackList = TrackList.getTrackListPointer();
        track1 = new Track("Bohemian Rhapsody",  "Queen",        Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);
        track3 = new Track("Hotel California",   "Eagles",       Year.of(1977), "Rock", 391, false, false, true);
    }

    // --- Constructor ---

    @Test @DisplayName("Constructor: oggetto non null")
    void testConstructorNotNull() { assertNotNull(trackList); }

    @Test @DisplayName("Constructor: nome null (super(null))")
    void testConstructorNullName() { assertNull(trackList.getName()); }

    @Test @DisplayName("Constructor: lista vuota e non null")
    void testConstructorEmptyTracks() {
        assertNotNull(trackList.getTracks());
        assertTrue(trackList.getTracks().isEmpty());
    }

    @Test @DisplayName("Constructor: getSize() è 0")
    void testConstructorSizeZero() { assertEquals(0, trackList.getSize()); }

    @Test @DisplayName("Constructor: subjectTrackList non null")
    void testConstructorSubjectNotNull() { assertNotNull(trackList.getSubjectTrackList()); }

    // --- Singleton ---

    @Test @DisplayName("exists(): true dopo la creazione")
    void testExistsAfterCreation() { assertTrue(TrackList.exists()); }

    @Test @DisplayName("getTrackListPointer(): restituisce l'istanza corrente")
    void testGetPointerReturnsSameInstance() { assertSame(trackList, TrackList.getTrackListPointer()); }

    @Test @DisplayName("Singleton: new TrackList() sovrascrive il puntatore")
    void testSingletonPointerOverwritten() {
        TrackList second = TrackList.getTrackListPointer();
        assertSame(second, TrackList.getTrackListPointer());
        assertNotSame(trackList, TrackList.getTrackListPointer());
    }

    // --- addTrack() ---

    @Test @DisplayName("addTrack: aggiunge una traccia valida")
    void testAddTrackValid() {
        trackList.addTrack(track1);
        assertEquals(1, trackList.getSize());
        assertTrue(trackList.getTracks().contains(track1));
    }

    @Test @DisplayName("addTrack: null lancia IllegalArgumentException")
    void testAddTrackNull() {
        assertThrows(IllegalArgumentException.class, () -> trackList.addTrack(null));
    }

    @Test @DisplayName("addTrack: duplicato lancia IllegalArgumentException")
    void testAddTrackDuplicate() {
        trackList.addTrack(track1);
        assertThrows(IllegalArgumentException.class, () -> trackList.addTrack(track1));
    }

    // --- removeTrack() ---

    @Test @DisplayName("removeTrack: rimuove una traccia presente")
    void testRemoveTrackPresent() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        trackList.removeTrack(track1);
        assertEquals(1, trackList.getSize());
        assertFalse(trackList.getTracks().contains(track1));
    }

    @Test @DisplayName("removeTrack: traccia assente non lancia eccezioni")
    void testRemoveTrackNotPresent() {
        assertDoesNotThrow(() -> trackList.removeTrack(track1));
    }

    @Test @DisplayName("removeTrack: lista vuota dopo rimozione unica traccia")
    void testRemoveTrackUntilEmpty() {
        trackList.addTrack(track1);
        trackList.removeTrack(track1);
        assertTrue(trackList.getTracks().isEmpty());
    }

    // --- updateTrack() ---

    @Test @DisplayName("updateTrack: aggiorna i campi in-place")
    void testUpdateTrackUpdatesFields() {
        trackList.addTrack(track1);
        Track newData = new Track("Bohemian Rhapsody Remaster", "Queen",
                Year.of(2011), "Classic Rock", 360, false, false, true);
        trackList.updateTrack(track1, newData);

        Track inList = trackList.getTracks().get(0);
        assertSame(track1, inList);
        assertEquals("Bohemian Rhapsody Remaster", inList.getTitle());
        assertEquals(Year.of(2011),  inList.getYear());
        assertEquals("Classic Rock", inList.getGenre());
    }

    @Test @DisplayName("updateTrack: size invariata")
    void testUpdateTrackSizeUnchanged() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        Track newData = new Track("New Title", "New Author", Year.of(2000), "Pop", 200, false, false, false);
        trackList.updateTrack(track1, newData);
        assertEquals(2, trackList.getSize());
    }

    @Test @DisplayName("updateTrack: duplicato → IllegalArgumentException")
    void testUpdateTrackDuplicateThrows() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        Track newData = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Blues", 300, false, false, false);
        assertThrows(IllegalArgumentException.class, () -> trackList.updateTrack(track1, newData));
    }

    // --- Integrazione TrackList <-> Playlist ---

    @Test @DisplayName("Integrazione: updateTrack su Playlist propaga i campi alla TrackList (in-place)")
    void testUpdateTrackPropagatesFromPlaylist() {
        trackList.addTrack(track1);

        Playlist userPlaylist = new Playlist("User Playlist");
        userPlaylist.addTrack(track1); // stesso oggetto

        Track newData = new Track("Updated Title", "Queen", Year.of(1975), "Rock", 354, true, false, false);
        userPlaylist.updateTrack(track1, newData);

        // track1 è lo stesso riferimento → i campi aggiornati sono visibili dalla TrackList
        assertEquals("Updated Title", trackList.getTracks().get(0).getTitle());
    }

    // --- Ereditarietà ---

    @Test @DisplayName("TrackList è istanza di Playlist")
    void testIsInstanceOfPlaylist() { assertTrue(trackList instanceof Playlist);}
}