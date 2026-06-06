package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.*;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ActivePlayerManager}.
 */
class ActivePlayerManagerTest {

    private ActivePlayerManager manager;
    private Playlist playlist;
    private Track track;

    @BeforeEach
    void setUp() {
        manager = ActivePlayerManager.getInstance();
        playlist = new Playlist("Test Playlist");
        track = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 300, false, false, false);
    }

    @AfterEach
    void tearDown() {
        manager.closePlayer();
    }

    @Test
    @DisplayName("Singleton: l'istanza non deve essere nulla")
    void testGetInstance() {
        assertNotNull(manager);
    }

    @Test
    @DisplayName("Stato iniziale: deve essere chiuso")
    void testHasActivePlayerInitiallyFalse() {
        assertFalse(manager.hasActivePlayer());
    }

    @Test
    @DisplayName("Getter: devono ritornare null se il player non è aperto")
    void testGettersReturnNullWhenClosed() {
        assertNull(manager.getCurrentTrack());
        assertNull(manager.getCurrentPlaylist());
    }
    @Test
    @DisplayName("Singleton: getInstance deve ritornare sempre la stessa istanza")
    void testSingletonIdentity() {
        ActivePlayerManager anotherManager = ActivePlayerManager.getInstance();
        assertSame(manager, anotherManager, "Le istanze dovrebbero essere identiche");
    }
}