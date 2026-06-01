package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Year;
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

    @Test
    @DisplayName("execute: avvia la riproduzione e passa allo stato Pause")
    void testExecuteChangesStateToPause() {
        // Eseguiamo l'azione di Play
        playState.execute(player);

        // Verifichiamo che la transizione di stato sia avvenuta correttamente.
        // In JUnit 5, assertInstanceOf verifica che l'oggetto sia esattamente di quella classe.
       assertInstanceOf(Pause.class, player.getCurrentState(), 
                "Dopo aver eseguito Play, lo stato del player deve diventare Pause");
    }
}