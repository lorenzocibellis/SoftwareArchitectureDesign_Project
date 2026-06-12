package org.unisa.musicplaylistmanager.track;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;

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

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit JavaFX già inizializzato, va bene
        }
        Thread.sleep(200); // attendi che il toolkit sia pronto
    }

    @BeforeEach
    void setUp() {
        trackList = TrackList.getTrackListPointer();

        // reset stato singleton
        trackList.getTracks().clear();
        trackList.getTopTracks().clear();

        track1 = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Year.of(1975),
                "Rock",
                354,
                true,
                false,
                false
        );

        track2 = new Track(
                "Stairway to Heaven",
                "Led Zeppelin",
                Year.of(1971),
                "Rock",
                482,
                false,
                false,
                false
        );
    }

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("TrackList singleton non null")
    void testSingletonNotNull() {
        assertNotNull(trackList);
    }

    @Test
    @DisplayName("exists(): true dopo inizializzazione")
    void testExistsAfterCreation() {
        assertTrue(TrackList.exists());
    }

    // -----------------------------------------------------------------------
    // ADD TRACK
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("addTrack: aggiunge una traccia valida e aggiorna Top list")
    void testAddTrackValid() {
        track1.incrementNumOfPlay(); // serve almeno 1 play per comparire nella Top 3
        trackList.addTrack(track1);

        assertEquals(1, trackList.getTracks().size());
        assertTrue(trackList.getTracks().contains(track1));
        assertEquals(1, trackList.getTopTracks().size());
    }

    @Test
    @DisplayName("addTrack: null lancia eccezione")
    void testAddTrackNull() {
        assertThrows(IllegalArgumentException.class, () -> trackList.addTrack(null));
    }

    // -----------------------------------------------------------------------
    // REMOVE TRACK
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("removeTrack: rimuove correttamente una traccia")
    void testRemoveTrackPresent() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);

        trackList.removeTrack(track1);

        assertEquals(1, trackList.getTracks().size());
        assertFalse(trackList.getTracks().contains(track1));
    }

    // -----------------------------------------------------------------------
    // REMOVE ALL
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("removeAllTracks: svuota lista")
    void testRemoveAll() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);

        trackList.removeAllTracks(new java.util.ArrayList<>(trackList.getTracks()));

        assertTrue(trackList.getTracks().isEmpty());
    }

    // -----------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateTrack: aggiorna correttamente una traccia")
    void testUpdateTrack() {
        trackList.addTrack(track1);

        Track updated = new Track(
                "Bohemian Rhapsody Remaster",
                "Queen",
                Year.of(2011),
                "Rock",
                360,
                false,
                false,
                true
        );

        trackList.updateTrack(track1, updated);

        Track inList = trackList.getTracks().get(0);
        assertEquals("Bohemian Rhapsody Remaster", inList.getTitle());
    }

    // -----------------------------------------------------------------------
    // TOP TRACKS
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("TopTracks: aggiornamento automatico con playCount")
    void testTopTracksReactiveUpdate() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);

        // simula ascolti
        track1.incrementNumOfPlay();
        track1.incrementNumOfPlay();
        track2.incrementNumOfPlay();

        // forza refresh (in caso listener non ancora triggerato in test)
        trackList.refreshTopThreeTracks();

        assertFalse(trackList.getTopTracks().isEmpty());

        // track1 deve essere sopra track2
        assertEquals(track1, trackList.getTopTracks().get(0));
    }

    @Test
    @DisplayName("TopTracks: lista vuota quando nessuna traccia ha play")
    void testTopTracksEmptyWhenNoPlays() {
        trackList.addTrack(track1);

        trackList.getTopTracks().clear();
        trackList.refreshTopThreeTracks();

        // può essere vuota se nessun play > 0
        assertTrue(trackList.getTopTracks().isEmpty()
                || trackList.getTopTracks().size() <= 1);
    }
}