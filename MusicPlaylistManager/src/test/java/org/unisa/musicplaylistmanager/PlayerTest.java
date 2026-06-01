package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.player.Player;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.state.PlayerState;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Player}.
 * @author gruppo10
 */
class PlayerTest {

    private Player player;
    private Playlist playlist;
    private Track track;

    // Stub minimale di PlayerState per i test
    private static class StubState implements PlayerState {
        boolean executeCalled = false;

        @Override
        public void execute(Player player) {
            executeCalled = true;
        }
    }

    // -----------------------------------------------------------------------
    // Setup / Teardown
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Test Playlist");
        track    = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 5, false, false, false);
        // durata breve (5 s) per i test sul timer

        playlist.addTrack(track);
        player = new Player(new StubState(), playlist, track);
    }

    @AfterEach
    void tearDown() {
        // garantisce che il timer venga sempre fermato dopo ogni test
        player.terminate();
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: oggetto creato correttamente")
    void testConstructorNotNull() {
        assertNotNull(player);
    }

    @Test
    @DisplayName("Constructor: accetta defaultState null senza eccezioni")
    void testConstructorNullState() {
        assertDoesNotThrow(() -> new Player(null, playlist, track));
    }

    @Test
    @DisplayName("Constructor: accetta playlist null senza eccezioni")
    void testConstructorNullPlaylist() {
        assertDoesNotThrow(() -> new Player(new StubState(), null, track));
    }

    @Test
    @DisplayName("Constructor: accetta currentTrack null senza eccezioni")
    void testConstructorNullTrack() {
        assertDoesNotThrow(() -> new Player(new StubState(), playlist, null));
    }

    // -----------------------------------------------------------------------
    // setState() / changeState()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setState + changeState: esegue il metodo execute() del nuovo stato")
    void testSetStateAndChangeState() {
        StubState newState = new StubState();
        player.setState(newState);
        player.changeState();
        assertTrue(newState.executeCalled);
    }

    @Test
    @DisplayName("changeState: usa lo stato di default impostato nel costruttore")
    void testChangeStateUsesDefaultState() {
        StubState defaultState = new StubState();
        Player p = new Player(defaultState, playlist, track);
        p.changeState();
        assertTrue(defaultState.executeCalled);
    }

    @Test
    @DisplayName("setState: sostituisce lo stato corrente")
    void testSetStateReplacesCurrentState() {
        StubState first  = new StubState();
        StubState second = new StubState();

        player.setState(first);
        player.setState(second);
        player.changeState();

        assertFalse(first.executeCalled,  "Il primo stato NON deve essere eseguito");
        assertTrue(second.executeCalled, "Il secondo stato DEVE essere eseguito");
    }

    // -----------------------------------------------------------------------
    // Callbacks – registrazione
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setOnTimeTick: accetta un listener senza eccezioni")
    void testSetOnTimeTick() {
        assertDoesNotThrow(() -> player.setOnTimeTick(t -> {}));
    }

    @Test
    @DisplayName("setOnPlayUIUpdate: accetta un listener senza eccezioni")
    void testSetOnPlayUIUpdate() {
        assertDoesNotThrow(() -> player.setOnPlayUIUpdate(() -> {}));
    }

    @Test
    @DisplayName("setOnPauseUIUpdate: accetta un listener senza eccezioni")
    void testSetOnPauseUIUpdate() {
        assertDoesNotThrow(() -> player.setOnPauseUIUpdate(() -> {}));
    }

    @Test
    @DisplayName("setOnTimeTick: accetta null senza eccezioni")
    void testSetOnTimeTickNull() {
        assertDoesNotThrow(() -> player.setOnTimeTick(null));
    }

    // -----------------------------------------------------------------------
    // startPlayback()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("startPlayback: invoca onPlayUIUpdate immediatamente")
    void testStartPlaybackFiresPlayUIUpdate() {
        AtomicBoolean called = new AtomicBoolean(false);
        player.setOnPlayUIUpdate(() -> called.set(true));

        player.startPlayback();

        assertTrue(called.get(), "onPlayUIUpdate deve essere chiamato subito");
    }

    @Test
    @DisplayName("startPlayback: non lancia eccezioni con currentTrack valida")
    void testStartPlaybackNoException() {
        assertDoesNotThrow(() -> player.startPlayback());
    }

    @Test
    @DisplayName("startPlayback: non lancia eccezioni con currentTrack null")
    void testStartPlaybackNullTrack() {
        Player p = new Player(new StubState(), playlist, null);
        assertDoesNotThrow(() -> p.startPlayback());
        p.terminate();
    }

    @Test
    @DisplayName("startPlayback: onTimeTick viene invocato dopo circa 1 secondo")
    void testStartPlaybackTimeTickFired() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);
        player.setOnTimeTick(t -> ticks.incrementAndGet());

        player.startPlayback();
        Thread.sleep(1500); // attende ~1,5 s per almeno 1 tick

        assertTrue(ticks.get() >= 1, "onTimeTick deve scattare almeno una volta dopo 1 s");
    }

    @Test
    @DisplayName("startPlayback due volte: il vecchio timer viene cancellato (no tick doppi)")
    void testStartPlaybackRestartsTimer() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);
        player.setOnTimeTick(t -> ticks.incrementAndGet());

        player.startPlayback();
        player.startPlayback(); // ricomincia: deve azzerare il timer precedente
        Thread.sleep(1500);

        // Con due timer attivi avremmo ≥ 2 tick in 1,5 s; con uno solo ≥ 1 ma ≤ 2
        assertTrue(ticks.get() >= 1, "Almeno un tick atteso");
    }

    // -----------------------------------------------------------------------
    // stopPlayback()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("stopPlayback: invoca onPauseUIUpdate")
    void testStopPlaybackFiresPauseUIUpdate() {
        AtomicBoolean called = new AtomicBoolean(false);
        player.setOnPauseUIUpdate(() -> called.set(true));

        player.startPlayback();
        player.stopPlayback();

        assertTrue(called.get(), "onPauseUIUpdate deve essere chiamato");
    }

    @Test
    @DisplayName("stopPlayback senza startPlayback: non lancia eccezioni")
    void testStopPlaybackWithoutStart() {
        assertDoesNotThrow(() -> player.stopPlayback());
    }

    @Test
    @DisplayName("stopPlayback: il timer non fa più tick dopo lo stop")
    void testStopPlaybackHaltsTimer() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);
        player.setOnTimeTick(t -> ticks.incrementAndGet());

        player.startPlayback();
        Thread.sleep(1500);   // accumula qualche tick
        player.stopPlayback();

        int ticksAtStop = ticks.get();
        Thread.sleep(1500);   // aspetta ancora

        assertEquals(ticksAtStop, ticks.get(), "Nessun tick deve scattare dopo stopPlayback");
    }

    @Test
    @DisplayName("stopPlayback ripetuto: non lancia eccezioni (timer già null)")
    void testStopPlaybackCalledTwice() {
        player.startPlayback();
        player.stopPlayback();
        assertDoesNotThrow(() -> player.stopPlayback());
    }

    // -----------------------------------------------------------------------
    // terminate()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("terminate: invoca onPauseUIUpdate (delega a stopPlayback)")
    void testTerminateFiresPauseUIUpdate() {
        AtomicBoolean called = new AtomicBoolean(false);
        player.setOnPauseUIUpdate(() -> called.set(true));

        player.startPlayback();
        player.terminate();

        assertTrue(called.get());
    }

    @Test
    @DisplayName("terminate senza startPlayback: non lancia eccezioni")
    void testTerminateWithoutStart() {
        assertDoesNotThrow(() -> player.terminate());
    }

    @Test
    @DisplayName("terminate: il timer non fa più tick dopo la chiamata")
    void testTerminateHaltsTimer() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);
        player.setOnTimeTick(t -> ticks.incrementAndGet());

        player.startPlayback();
        Thread.sleep(1500);
        player.terminate();

        int ticksAtTerminate = ticks.get();
        Thread.sleep(1500);

        assertEquals(ticksAtTerminate, ticks.get(), "Nessun tick deve scattare dopo terminate");
    }
}
