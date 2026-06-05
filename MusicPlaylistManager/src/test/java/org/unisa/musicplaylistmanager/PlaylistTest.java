package org.unisa.musicplaylistmanager.playlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.unisa.musicplaylistmanager.observer.BaseObserverPlaylist;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Playlist} (versione aggiornata).
 * @author gruppo10
 */
class PlaylistTest {

    private Playlist playlist;
    private Track track1;
    private Track track2;
    private Track track3;

    // Stub minimo di BaseObserverPlaylist
    private static class StubObserver extends BaseObserverPlaylist {
        boolean updateCalled = false;
        Track lastTrack = null;
        StubObserver(Playlist p) { super(p); }
        @Override
        public void update(Track track) { updateCalled = true; lastTrack = track; }
    }

    @BeforeEach
    void setUp() {
        playlist = new Playlist("My Playlist");
        track1 = new Track("Bohemian Rhapsody",  "Queen",        Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);
        track3 = new Track("Hotel California",   "Eagles",       Year.of(1977), "Rock", 391, false, false, true);
    }

    // --- Constructor ---

    @Test
    @DisplayName("Constructor: oggetto creato correttamente")
    void testConstructorNotNull() { assertNotNull(playlist); }

    @Test
    @DisplayName("Constructor: nome impostato correttamente")
    void testConstructorName() { assertEquals("My Playlist", playlist.getName()); }

    @Test
    @DisplayName("Constructor: getTracks() non null e vuoto")
    void testConstructorEmptyTracks() {
        assertNotNull(playlist.getTracks());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    @DisplayName("Constructor: getSize() è 0")
    void testConstructorSizeZero() { assertEquals(0, playlist.getSize()); }

    @Test
    @DisplayName("Constructor: observer è null di default")
    void testConstructorObserverNull() { assertNull(playlist.getObserver()); }

    // --- setName / getName ---

    @Test
    @DisplayName("setName: aggiorna il nome")
    void testSetName() { playlist.setName("Rock Hits"); assertEquals("Rock Hits", playlist.getName()); }

    @Test
    @DisplayName("setName: accetta null")
    void testSetNameNull() { playlist.setName(null); assertNull(playlist.getName()); }

    // --- setObserver / getObserver ---

    @Test
    @DisplayName("setObserver: imposta l'observer correttamente")
    void testSetObserver() {
        StubObserver obs = new StubObserver(playlist);
        playlist.setObserver(obs);
        assertSame(obs, playlist.getObserver());
    }

    @Test
    @DisplayName("setObserver: accetta null")
    void testSetObserverNull() { playlist.setObserver(null); assertNull(playlist.getObserver()); }

    // --- addTrack() ---

    @Test
    @DisplayName("addTrack: aggiunge una traccia valida")
    void testAddTrackValid() {
        playlist.addTrack(track1);
        assertEquals(1, playlist.getSize());
        assertTrue(playlist.getTracks().contains(track1));
    }

    @Test
    @DisplayName("addTrack: aggiunge più tracce mantenendo l'ordine")
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
    @DisplayName("addTrack: traccia null lancia IllegalArgumentException")
    void testAddTrackNull() {
        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(null));
    }

    @Test
    @DisplayName("addTrack: duplicato per equals lancia IllegalArgumentException")
    void testAddTrackDuplicate() {
        playlist.addTrack(track1);
        Track copy = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Pop", 99, false, true, true);
        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(copy));
    }

    @Test
    @DisplayName("addTrack: duplicato per identità lancia IllegalArgumentException")
    void testAddTrackDuplicateSameReference() {
        playlist.addTrack(track1);
        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(track1));
    }

    // --- removeTrack() ---

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
    @DisplayName("removeTrack: traccia assente non lancia eccezioni")
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
    }

    @Test
    @DisplayName("removeTrack: rimuove per equals, non per identità")
    void testRemoveTrackByEquality() {
        playlist.addTrack(track1);
        Track copy = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Pop", 200, false, true, true);
        playlist.removeTrack(copy);
        assertTrue(playlist.getTracks().isEmpty());
    }

    // --- updateTrack() ---

    @Test
    @DisplayName("updateTrack: aggiorna tutti i campi della traccia in-place")
    void testUpdateTrackUpdatesFields() {
        playlist.addTrack(track1);
        Track newData = new Track("Bohemian Rhapsody Remaster", "Queen",
                Year.of(2011), "Classic Rock", 360, false, false, true);

        playlist.updateTrack(track1, newData);

        Track inList = playlist.getTracks().get(0);
        assertSame(track1, inList); // stesso riferimento
        assertEquals("Bohemian Rhapsody Remaster", inList.getTitle());
        assertEquals(Year.of(2011),  inList.getYear());
        assertEquals("Classic Rock", inList.getGenre());
        assertEquals(360,            inList.getDuration());
        assertFalse(inList.isFavourite());
        assertTrue(inList.isNewRelease());
    }

    @Test
    @DisplayName("updateTrack: la size non cambia")
    void testUpdateTrackSizeUnchanged() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        Track newData = new Track("New Title", "New Author", Year.of(2020), "Pop", 200, false, false, false);
        playlist.updateTrack(track1, newData);
        assertEquals(2, playlist.getSize());
    }

    @Test
    @DisplayName("updateTrack: crea duplicato con altra traccia → IllegalArgumentException")
    void testUpdateTrackCreatesDuplicateThrows() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        // stessi titolo/autore/anno di track2
        Track newData = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Blues", 300, false, false, false);
        assertThrows(IllegalArgumentException.class, () -> playlist.updateTrack(track1, newData));
    }

    @Test
    @DisplayName("updateTrack: aggiornare con stessi dati (equals) non lancia eccezioni")
    void testUpdateTrackSameDataNoException() {
        playlist.addTrack(track1);
        Track sameData = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 354, true, false, false);
        assertDoesNotThrow(() -> playlist.updateTrack(track1, sameData));
    }

    @Test
    @DisplayName("updateTrack: il riferimento oggetto in lista rimane lo stesso (in-place)")
    void testUpdateTrackSameReference() {
        playlist.addTrack(track1);
        Track ref = playlist.getTracks().get(0);
        Track newData = new Track("New Title", "New Author", Year.of(2000), "Pop", 100, false, false, false);
        playlist.updateTrack(track1, newData);
        assertSame(ref, playlist.getTracks().get(0));
    }

    // --- deleteAll() ---

    @Test
    @DisplayName("deleteAll: svuota la playlist")
    void testDeleteAll() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        playlist.deleteAll();
        assertTrue(playlist.getTracks().isEmpty());
        assertEquals(0, playlist.getSize());
    }

    @Test
    @DisplayName("deleteAll: su playlist vuota non lancia eccezioni")
    void testDeleteAllEmptyPlaylist() {
        assertDoesNotThrow(() -> playlist.deleteAll());
        assertEquals(0, playlist.getSize());
    }

    // --- UnsupportedOperationException ---

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

    // --- equals() ---

    @Test
    @DisplayName("equals: stessa istanza → true")
    void testEqualsSameInstance() { assertEquals(playlist, playlist); }

    @Test
    @DisplayName("equals: stesso nome → true")
    void testEqualsSameName() { assertEquals(playlist, new Playlist("My Playlist")); }

    @Test
    @DisplayName("equals: nome diverso → false")
    void testEqualsDifferentName() { assertNotEquals(playlist, new Playlist("Other")); }

    @Test
    @DisplayName("equals: null → false")
    void testEqualsNull() { assertNotEquals(null, playlist); }

    @Test
    @DisplayName("equals: tipo diverso → false")
    void testEqualsDifferentType() { assertNotEquals("My Playlist", playlist); }

    // --- hashCode() ---

    @Test
    @DisplayName("hashCode: stesso nome → stesso hashCode")
    void testHashCodeSameName() {
        assertEquals(playlist.hashCode(), new Playlist("My Playlist").hashCode());
    }

    @Test
    @DisplayName("hashCode: nome diverso → hashCode diverso")
    void testHashCodeDifferentName() {
        assertNotEquals(playlist.hashCode(), new Playlist("Other").hashCode());
    }

    // --- toString() ---

    @Test
    @DisplayName("toString: restituisce il nome della playlist")
    void testToString() { assertEquals("My Playlist", playlist.toString()); }
}