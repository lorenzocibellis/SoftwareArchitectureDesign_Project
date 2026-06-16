package org.unisa.musicplaylistmanager.state;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.player.Player;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.state.Pause;
import org.unisa.musicplaylistmanager.state.Play;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author gruppo10
 */
class PlayTest {

    private Player player;
    private Play playState;

    @BeforeEach
    void setUp() {
        // Creiamo una traccia dummy per far felice il costruttore del Player
        Track dummyTrack = new Track("Titolo", "Autore", Year.of(2020), "Pop", 100, false, false, false);
        
        // CORREZIONE: Il costruttore di Playlist accetta una sola stringa!
        Playlist dummyPlaylist = new Playlist("Mia Playlist");
        
        playState = new Play();
        // Inizializziamo il player partendo dallo stato Play
        player = new Player(playState, dummyPlaylist, dummyTrack);
    }

    @AfterEach
    void tearDown() {
        // Ferma il timer interno avviato da startPlayback per non lasciare thread attivi
        player.terminate();
    }

    @Test
    @DisplayName("execute: avvia la riproduzione e passa allo stato Pause")
    void testExecuteChangesStateToPause() {
        // Eseguiamo l'azione di Play
        playState.execute(player);

        // Verifichiamo che la transizione di stato sia avvenuta correttamente.
        // In JUnit 5, assertInstanceOf verifica che l'oggetto sia esattamente di quella classe.
       assertTrue(player.getCurrentState() instanceof Pause, "Dopo aver eseguito Play, lo stato del player deve diventare Pause");
    }

    @Test
    @DisplayName("execute: avvia effettivamente la riproduzione (callback UI di play invocata)")
    void testExecuteStartsPlayback() {
        AtomicBoolean playbackAvviato = new AtomicBoolean(false);
        // La callback di play viene invocata in modo sincrono da startPlayback()
        player.setOnPlayUIUpdate(() -> playbackAvviato.set(true));

        playState.execute(player);

        assertTrue(playbackAvviato.get(),
                "execute() dello stato Play deve invocare startPlayback() sul player");
    }
}