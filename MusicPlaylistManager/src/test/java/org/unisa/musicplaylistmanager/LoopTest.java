package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.unisa.musicplaylistmanager.strategy.Loop;

class LoopTest {

    private Loop loopStrategy;

    @BeforeEach
    void setUp() {
        loopStrategy = new Loop();
    }

    @Test
    void testExecuteWithValidPlaylist() {
        int size = 5;
        int currentTrackIndex = 2; // Supponiamo di essere sulla terza traccia

        int[] result = loopStrategy.execute(size, currentTrackIndex);

        assertEquals(size, result.length, "L'array deve avere la stessa dimensione della playlist");
        
        // Verifica che ogni posizione contenga l'indice della traccia corrente
        for (int index : result) {
            assertEquals(currentTrackIndex, index, "Ogni elemento deve puntare alla traccia corrente");
        }
    }

    @Test
    void testExecuteWithZeroSize() {
        int[] result = loopStrategy.execute(0, 0);
        
        assertEquals(0, result.length, "Con size 0, l'array deve essere vuoto");
    }

    @Test
    void testExecuteWithNegativeSize() {
        int[] result = loopStrategy.execute(-5, 0);
        
        assertEquals(0, result.length, "Con size negativa, l'array deve essere vuoto");
    }

    @Test
    void testExecuteBoundaryIndex() {
        int size = 3;
        int currentTrackIndex = 0; // Prima traccia

        int[] result = loopStrategy.execute(size, currentTrackIndex);
        
        for (int index : result) {
            assertEquals(0, index, "Tutti gli indici devono essere 0");
        }
    }
}