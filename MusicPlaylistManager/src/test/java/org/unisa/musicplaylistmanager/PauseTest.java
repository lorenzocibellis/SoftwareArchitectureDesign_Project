package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Pause}.
 * @author gruppo10
 */
class PauseTest {

    private Player player;
    private Pause pauseState;

    @BeforeEach
    void setUp() {
        // Creiamo una traccia dummy per far felice il costruttore del Player
        Track dummyTrack = new Track("Titolo", "Autore", Year.of(2020), "Pop", 100, false, false, false);
        
        // Il costruttore di Playlist accetta una sola stringa
        Playlist dummyPlaylist = new Playlist("Mia Playlist"); 
        
        pauseState = new Pause();
        // Inizializziamo il player partendo dallo stato Pause
        player = new Player(pauseState, dummyPlaylist, dummyTrack);
    }

    @Test
    @DisplayName("execute: interrompe la riproduzione e passa allo stato Play")
    void testExecuteChangesStateToPlay() {
        // Eseguiamo l'azione di Pause
        pauseState.execute(player);

        // Verifichiamo che la transizione di stato sia avvenuta correttamente.
        // In JUnit 5, assertInstanceOf verifica che l'oggetto sia esattamente di quella classe.
        assertInstanceOf(Play.class, player.getCurrentState(), 
                "Dopo aver eseguito Pause, lo stato del player deve diventare Play");
    }
}