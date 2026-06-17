package org.unisa.musicplaylistmanager.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.unisa.musicplaylistmanager.player.strategy.Sequential;

class SequentialTest {

    private Sequential sequentialStrategy;

    @BeforeEach
    void setUp() {
        sequentialStrategy = new Sequential();
    }

    @Test
    void testExecuteGeneratesSequentialOrder() {
        int size = 5;
        // Il parametro 'b' (currentTrackIndex) nella tua classe Sequential non viene usato, 
        // quindi passiamo 0 come valore neutro.
        int[] result = sequentialStrategy.execute(size, 0);

        assertEquals(size, result.length, "La lunghezza dell'array deve essere uguale a size");

        // Verifica che l'array sia [0, 1, 2, 3, 4]
        for (int i = 0; i < size; i++) {
            assertEquals(i, result[i], "L'indice alla posizione " + i + " dovrebbe essere " + i);
        }
    }

    @Test
    void testExecuteWithZeroSize() {
        int[] result = sequentialStrategy.execute(0, 0);
        assertEquals(0, result.length, "Con size 0, l'array deve essere vuoto");
    }

    @Test
    void testExecuteWithLargeSize() {
        int size = 1000;
        int[] result = sequentialStrategy.execute(size, 0);
        
        assertEquals(size, result.length);
        assertEquals(999, result[999], "L'ultimo elemento deve essere size - 1");
    }
}