package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

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

    @BeforeEach
    void setUp() {
        // Otteniamo l'istanza singleton
        trackList = TrackList.getTrackListPointer();
        
        // Puliamo la lista prima di ogni test
        trackList.getTracks().clear();

        track1 = new Track("Bohemian Rhapsody",  "Queen",        Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);
    }

    // --- Constructor & Singleton ---

    @Test @DisplayName("Constructor: oggetto non null")
    void testConstructorNotNull() { assertNotNull(trackList); }

    @Test @DisplayName("exists(): true dopo la creazione")
    void testExistsAfterCreation() { assertTrue(TrackList.exists()); }

    // --- Metodi di gestione tracce ---

    @Test @DisplayName("addTrack: aggiunge una traccia valida")
    void testAddTrackValid() {
        trackList.addTrack(track1);
        assertEquals(1, trackList.getSize());
        assertTrue(trackList.getTracks().contains(track1));
    }

    @Test @DisplayName("removeTrack: rimuove una traccia presente")
    void testRemoveTrackPresent() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        trackList.removeTrack(track1);
        assertEquals(1, trackList.getSize());
        assertFalse(trackList.getTracks().contains(track1));
    }

    @Test @DisplayName("updateTrack: aggiorna i campi in-place")
    void testUpdateTrackUpdatesFields() {
        trackList.addTrack(track1);
        Track newData = new Track("Bohemian Rhapsody Remaster", "Queen",
                Year.of(2011), "Classic Rock", 360, false, false, true);
        
        trackList.updateTrack(track1, newData);

        Track inList = trackList.getTracks().get(0);
        assertEquals("Bohemian Rhapsody Remaster", inList.getTitle());
    }
}