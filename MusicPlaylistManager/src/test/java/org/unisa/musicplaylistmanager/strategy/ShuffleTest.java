package org.unisa.musicplaylistmanager.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.unisa.musicplaylistmanager.strategy.Shuffle;

class ShuffleTest {

    private Shuffle shuffleStrategy;

    @BeforeEach
    void setUp() {
        shuffleStrategy = new Shuffle();
    }

    @Test
    void testExecuteMainLogic() {
        int size = 10;
        int currentTrack = 3;
        int[] result = shuffleStrategy.execute(size, currentTrack);

        assertEquals(size, result.length, "La dimensione dell'array deve essere uguale alla size");
        assertEquals(currentTrack, result[0], "La traccia corrente deve essere sempre in prima posizione");

        // Verifica che tutti gli indici siano presenti (nessun duplicato e tutti i numeri da 0 a size-1)
        Set<Integer> uniqueIndices = new HashSet<>();
        for (int index : result) {
            uniqueIndices.add(index);
        }

        assertEquals(size, uniqueIndices.size(), "Tutti gli indici devono essere unici");
        for (int i = 0; i < size; i++) {
            assertTrue(uniqueIndices.contains(i), "L'indice " + i + " dovrebbe essere presente nell'array");
        }
    }

    @Test
    void testExecuteWithZeroSize() {
        int[] result = shuffleStrategy.execute(0, 0);
        assertEquals(0, result.length, "Con size 0, l'array deve essere vuoto");
    }

    @Test
    void testExecuteWithSingleTrack() {
        // Con una sola traccia, la playlist deve contenere solo l'indice 0
        int[] result = shuffleStrategy.execute(1, 0);
        assertEquals(1, result.length);
        assertEquals(0, result[0]);
    }
}