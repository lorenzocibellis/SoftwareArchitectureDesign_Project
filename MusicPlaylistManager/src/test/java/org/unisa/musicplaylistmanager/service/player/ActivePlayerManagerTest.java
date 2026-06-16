package org.unisa.musicplaylistmanager.service.player;

import org.junit.jupiter.api.*;
import org.unisa.musicplaylistmanager.track.list.playlist.Playlist;
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
        assertNull(manager.getCurrentPlaylistIdentifier());
    }
    @Test
    @DisplayName("Singleton: getInstance deve ritornare sempre la stessa istanza")
    void testSingletonIdentity() {
        ActivePlayerManager anotherManager = ActivePlayerManager.getInstance();
        assertSame(manager, anotherManager, "Le istanze dovrebbero essere identiche");
    }

    // -----------------------------------------------------------------------
    // Proprietà osservabili (stato a player chiuso)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("playerActiveProperty: esiste ed è false quando il player è chiuso")
    void testPlayerActivePropertyInitiallyFalse() {
        assertNotNull(manager.playerActiveProperty());
        assertFalse(manager.playerActiveProperty().get());
    }

    @Test
    @DisplayName("currentTrackProperty: esiste ed è null quando il player è chiuso")
    void testCurrentTrackPropertyInitiallyNull() {
        assertNotNull(manager.currentTrackProperty());
        assertNull(manager.currentTrackProperty().get());
    }

    // -----------------------------------------------------------------------
    // setCurrentTrack / currentTrackProperty
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setCurrentTrack: aggiorna la proprietà osservabile della traccia corrente")
    void testSetCurrentTrackUpdatesProperty() {
        manager.setCurrentTrack(track);
        assertSame(track, manager.currentTrackProperty().get());
    }

    @Test
    @DisplayName("getCurrentTrack: resta null anche dopo setCurrentTrack se il player non è aperto")
    void testGetCurrentTrackIndependentFromProperty() {
        // setCurrentTrack agisce solo sulla proprietà osservabile, non sul Player sottostante
        manager.setCurrentTrack(track);
        assertNull(manager.getCurrentTrack(),
                "Senza un PlayerController attivo, getCurrentTrack deve restituire null");
    }

    // -----------------------------------------------------------------------
    // getPlayerHeight
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getPlayerHeight: vale 0 quando non c'è alcun player attivo")
    void testPlayerHeightWhenClosed() {
        assertFalse(manager.hasActivePlayer());
        assertEquals(0.0, manager.getPlayerHeight(), 0.0001);
    }

    // -----------------------------------------------------------------------
    // closePlayer / toggleShuffle: robustezza a player chiuso
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("closePlayer: idempotente, riporta le proprietà allo stato chiuso")
    void testClosePlayerIsIdempotent() {
        manager.setCurrentTrack(track);

        assertDoesNotThrow(() -> manager.closePlayer());
        assertDoesNotThrow(() -> manager.closePlayer());

        assertFalse(manager.hasActivePlayer());
        assertNull(manager.currentTrackProperty().get());
        assertFalse(manager.playerActiveProperty().get());
    }

    @Test
    @DisplayName("toggleShuffle: nessun effetto e nessuna eccezione se il player è chiuso")
    void testToggleShuffleWhenClosedIsSafe() {
        assertDoesNotThrow(() -> manager.toggleShuffle());
    }
}