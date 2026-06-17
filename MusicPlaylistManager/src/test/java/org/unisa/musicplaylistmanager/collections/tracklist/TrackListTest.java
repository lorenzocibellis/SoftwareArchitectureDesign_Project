package org.unisa.musicplaylistmanager.collections.tracklist;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;
import org.unisa.musicplaylistmanager.core.observer.BaseObserver;
import org.unisa.musicplaylistmanager.collections.tracklist.model.TrackList;
import org.unisa.musicplaylistmanager.track.model.Track;

import java.time.Year;
import java.util.ArrayList;

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
            // Toolkit JavaFX già inizializzato
        }
        Thread.sleep(200);
    }

    @BeforeEach
    void setUp() {
        trackList = TrackList.getTrackListPointer();

        // reset stato singleton
        trackList.getTracks().clear();

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
    @DisplayName("addTrack: aggiunge una traccia valida")
    void testAddTrackValid() {
        trackList.addTrack(track1);

        assertEquals(1, trackList.getTracks().size());
        assertTrue(trackList.getTracks().contains(track1));
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

    @Test
    @DisplayName("updateTrack: aggiorna tutti i campi preservando l'identità dell'oggetto")
    void testUpdateTrackUpdatesAllFields() {
        trackList.addTrack(track1);

        Track updated = new Track("Nuovo Titolo", "Nuovo Autore", Year.of(2000),
                "Jazz", 250, true, true, true);

        trackList.updateTrack(track1, updated);

        // L'oggetto in lista resta lo stesso (identità preservata per non rompere gli Observer)
        assertSame(track1, trackList.getTracks().get(0));
        assertEquals("Nuovo Titolo", track1.getTitle());
        assertEquals("Nuovo Autore", track1.getAuthor());
        assertEquals(Year.of(2000), track1.getYear());
        assertEquals("Jazz", track1.getGenre());
        assertEquals(250, track1.getDuration());
        assertTrue(track1.isFavourite());
        assertTrue(track1.isExplicitContent());
        assertTrue(track1.isNewRelease());
    }

    // -----------------------------------------------------------------------
    // SINGLETON
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getTrackListPointer: restituisce sempre la stessa istanza")
    void testSingletonIdentity() {
        assertSame(trackList, TrackList.getTrackListPointer());
    }

    @Test
    @DisplayName("getName: la libreria usa il nome riservato di sistema")
    void testTrackListName() {
        assertEquals(TrackList.TRACKLIST_NAME, trackList.getName());
        assertEquals("La Mia Libreria", trackList.getName());
    }

    // -----------------------------------------------------------------------
    // OBSERVER PATTERN
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("removeTrack: notifica gli observer registrati con la traccia rimossa")
    void testRemoveTrackNotifiesObserver() {
        trackList.addTrack(track1);

        ArrayList<Track> notificate = new ArrayList<>();
        BaseObserver observer = notificate::add;
        trackList.attach(observer);

        try {
            trackList.removeTrack(track1);
            assertEquals(1, notificate.size(), "L'observer deve essere notificato una volta");
            assertSame(track1, notificate.get(0), "La notifica deve riguardare la traccia rimossa");
        } finally {
            trackList.detach(observer);
        }
    }

    @Test
    @DisplayName("removeAllTracks: notifica gli observer per ciascuna traccia rimossa")
    void testRemoveAllTracksNotifiesEachTrack() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);

        ArrayList<Track> notificate = new ArrayList<>();
        BaseObserver observer = notificate::add;
        trackList.attach(observer);

        try {
            trackList.removeAllTracks(new ArrayList<>(trackList.getTracks()));
            assertTrue(notificate.contains(track1));
            assertTrue(notificate.contains(track2));
            assertEquals(2, notificate.size());
        } finally {
            trackList.detach(observer);
        }
    }

    @Test
    @DisplayName("detach: un observer rimosso non riceve più notifiche")
    void testDetachStopsNotifications() {
        trackList.addTrack(track1);
        trackList.addTrack(track2);

        ArrayList<Track> notificate = new ArrayList<>();
        BaseObserver observer = notificate::add;
        trackList.attach(observer);
        trackList.detach(observer);

        trackList.removeTrack(track1);
        assertTrue(notificate.isEmpty(), "Dopo il detach non devono arrivare notifiche");
    }

    @Test
    @DisplayName("attach: registrare due volte lo stesso observer non genera notifiche doppie")
    void testAttachDuplicateIgnored() {
        trackList.addTrack(track1);

        ArrayList<Track> notificate = new ArrayList<>();
        BaseObserver observer = notificate::add;
        trackList.attach(observer);
        trackList.attach(observer); // secondo attach ignorato

        try {
            trackList.removeTrack(track1);
            assertEquals(1, notificate.size(), "L'observer deve essere notificato una sola volta");
        } finally {
            trackList.detach(observer);
        }
    }

    @Test
    @DisplayName("attach: un observer null viene ignorato senza causare errori")
    void testAttachNullIgnored() {
        trackList.addTrack(track1);
        assertDoesNotThrow(() -> trackList.attach(null));
        assertDoesNotThrow(() -> trackList.removeTrack(track1),
                "La notifica non deve sollevare NullPointerException per un observer null");
    }

    @Test
    @DisplayName("detach: rimuovere un observer mai registrato non causa errori")
    void testDetachUnknownObserverIsSafe() {
        BaseObserver observer = t -> { };
        assertDoesNotThrow(() -> trackList.detach(observer));
    }

}