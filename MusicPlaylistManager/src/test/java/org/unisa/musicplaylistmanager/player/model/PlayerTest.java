package org.unisa.musicplaylistmanager.player.model;

import org.junit.jupiter.api.*;
import org.unisa.musicplaylistmanager.core.iterator.AbstractIterator;
import org.unisa.musicplaylistmanager.core.iterator.IterableCollection;
import org.unisa.musicplaylistmanager.player.state.PlayerState;
import org.unisa.musicplaylistmanager.player.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.track.model.Track;

import java.time.Year;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Player}.
 */
public class PlayerTest {

    private Player player;
    private IterableCollection collection;
    private Track track;

    // -----------------------------------------------------------------------
    // Stub PlayerState
    // -----------------------------------------------------------------------
    private static class StubState implements PlayerState {
        boolean executeCalled = false;

        @Override
        public void execute(Player player) {
            executeCalled = true;
        }
    }

    // -----------------------------------------------------------------------
    // Stub IterableCollection + AbstractIterator Fixato
    // -----------------------------------------------------------------------
    private static class StubCollection implements IterableCollection {

        private final Track track;

        StubCollection(Track track) {
            this.track = track;
        }

        @Override
        public AbstractIterator createIterator() {
            return new AbstractIterator() {

                private Track current = track;

                @Override
                public Track getCurrent() {
                    return current;
                }

                @Override
                public Track getNext() {
                    return current;
                }

                @Override
                public Track getPrevious() {
                    return current;
                }

                @Override
                public void moveToTrack(Track t) {
                    current = t;
                }

                @Override
                public String getIdentifier() {
                    return "STUB";
                }

                // FIX COMPILAZIONE: metodo richiesto dalla nuova AbstractIterator
                @Override
                public void setStrategy(ExecutionStrategy strategy) {
                    // no-op per test
                }
            };
        }
    }

    // -----------------------------------------------------------------------
    // Setup JavaFX
    // -----------------------------------------------------------------------
    @BeforeAll
    static void initJFX() {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {}
    }

    @BeforeEach
    void setUp() {
        track = new Track("Bohemian Rhapsody", "Queen",
                Year.of(1975), "Rock", 5, false, false, false);

        collection = new StubCollection(track);
        player = new Player(new StubState(), collection, track);
    }

    @AfterEach
    void tearDown() {
        player.terminate();
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("Constructor: crea Player correttamente")
    void testConstructor() {
        assertNotNull(player);
    }

    @Test
    @DisplayName("Constructor: null collection lancia eccezione")
    void testConstructorNullCollection() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player(new StubState(), null, track));
    }

    @Test
    @DisplayName("Constructor: null track lancia eccezione")
    void testConstructorNullTrack() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player(new StubState(), collection, null));
    }

    // -----------------------------------------------------------------------
    // State pattern
    // -----------------------------------------------------------------------
    @Test
    void testChangeState() {
        StubState state = new StubState();
        player.setState(state);

        player.changeState();

        assertTrue(state.executeCalled);
    }

    @Test
    void testChangeStateDefault() {
        StubState state = new StubState();
        Player p = new Player(state, collection, track);

        p.changeState();

        assertTrue(state.executeCalled);
    }

    @Test
    void testSetStateReplace() {
        StubState s1 = new StubState();
        StubState s2 = new StubState();

        player.setState(s1);
        player.setState(s2);

        player.changeState();

        assertFalse(s1.executeCalled);
        assertTrue(s2.executeCalled);
    }

    // -----------------------------------------------------------------------
    // Callbacks
    // -----------------------------------------------------------------------
    @Test
    void testCallbacksSetters() {
        assertDoesNotThrow(() -> player.setOnTimeTick(t -> {}));
        assertDoesNotThrow(() -> player.setOnPlayUIUpdate(() -> {}));
        assertDoesNotThrow(() -> player.setOnPauseUIUpdate(() -> {}));
        assertDoesNotThrow(() -> player.setOnTrackChanged(() -> {}));
    }

    // -----------------------------------------------------------------------
    // Playback
    // -----------------------------------------------------------------------
    @Test
    void testStartPlaybackUI() {
        AtomicBoolean called = new AtomicBoolean(false);

        player.setOnPlayUIUpdate(() -> called.set(true));
        player.startPlayback();

        assertTrue(called.get());
    }

    @Test
    void testStartPlaybackNoCrash() {
        assertDoesNotThrow(() -> player.startPlayback());
    }

    @Test
    void testTimeTick() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);

        player.setOnTimeTick(t -> ticks.incrementAndGet());
        player.startPlayback();

        Thread.sleep(1200);

        assertTrue(ticks.get() >= 1);
    }

    @Test
    void testRestartTimer() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);

        player.setOnTimeTick(t -> ticks.incrementAndGet());

        player.startPlayback();
        player.startPlayback();

        Thread.sleep(1200);

        assertTrue(ticks.get() >= 1);
    }

    // -----------------------------------------------------------------------
    // Stop
    // -----------------------------------------------------------------------
    @Test
    void testStopPlaybackUI() {
        AtomicBoolean called = new AtomicBoolean(false);

        player.setOnPauseUIUpdate(() -> called.set(true));

        player.startPlayback();
        player.stopPlayback();

        assertTrue(called.get());
    }

    @Test
    void testStopDoesNotCrash() {
        assertDoesNotThrow(() -> player.stopPlayback());
    }

    @Test
    void testStopStopsTimer() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);

        player.setOnTimeTick(t -> ticks.incrementAndGet());

        player.startPlayback();
        Thread.sleep(1200);
        player.stopPlayback();

        int before = ticks.get();

        Thread.sleep(1200);

        assertEquals(before, ticks.get());
    }

    // -----------------------------------------------------------------------
    // Terminate
    // -----------------------------------------------------------------------
    @Test
    void testTerminateUI() {
        AtomicBoolean called = new AtomicBoolean(false);

        player.setOnPauseUIUpdate(() -> called.set(true));

        player.startPlayback();
        player.terminate();

        assertTrue(called.get());
    }

    @Test
    void testTerminateNoCrash() {
        assertDoesNotThrow(() -> player.terminate());
    }

    @Test
    void testTerminateStopsTimer() throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger(0);

        player.setOnTimeTick(t -> ticks.incrementAndGet());

        player.startPlayback();
        Thread.sleep(1200);
        player.terminate();

        int before = ticks.get();

        Thread.sleep(1200);

        assertEquals(before, ticks.get());
    }


    @Test
    @DisplayName("Seek: scorrimento normale entro i limiti")
    void testSeekToNormal() {
        AtomicInteger reportedTime = new AtomicInteger(-1);
        player.setOnTimeTick(t -> reportedTime.set(t));

        // la traccia di test creata nel setUp (Bohemian Rhapsody) ha durata 5 secondi.
        player.seekTo(3);

        // verifica che il tempo impostato sia 3 e la callback sia scattata
        assertEquals(3, reportedTime.get());
    }

    @Test
    @DisplayName("Seek: scorrimento con valore negativo limitato a 0")
    void testSeekToNegative() {
        AtomicInteger reportedTime = new AtomicInteger(-1);
        player.setOnTimeTick(t -> reportedTime.set(t));

        player.seekTo(-10);

        // Verifica che Math.max(0, ...) abbia forzato il tempo a 0
        assertEquals(0, reportedTime.get());
    }

    @Test
    @DisplayName("Seek: scorrimento oltre durata massima")
    void testSeekToBeyondDuration() {
        AtomicInteger reportedTime = new AtomicInteger(-1);
        player.setOnTimeTick(t -> reportedTime.set(t));

        // Durata massima = 5
        player.seekTo(100);

        // Verifica che Math.min(seconds, duration) abbia forzato il tempo a 5
        assertEquals(5, reportedTime.get());
    }
}