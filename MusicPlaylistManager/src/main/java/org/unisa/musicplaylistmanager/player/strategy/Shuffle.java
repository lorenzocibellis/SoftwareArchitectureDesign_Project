package org.unisa.musicplaylistmanager.player.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gruppo10
 */
public class Shuffle implements ExecutionStrategy {
    
    // Implementazione della modalità di riproduzione casuale

    /**
     *
     * @param a Indice della traccia 1
     * @param b Indice della traccia 2
     * @return L'array di navgiazione del player
     */
    @Override
    public int[] execute(int a, int b) {
        int size = a;
        int currentTrackIndex = b;
        int[] order = new int[size];
        
        if (size <= 0) return order;

        // 1. Mettiamo la traccia corrente in prima posizione
        order[0] = currentTrackIndex;
        
        // 2. Creiamo una lista con tutti gli altri indici della playlist
        List<Integer> otherIndices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (i != currentTrackIndex) {
                otherIndices.add(i);
            }
        }
        
        // 3. Mescoliamo gli altri indici casualmente
        Collections.shuffle(otherIndices);
        
        // 4. Inseriamo gli indici mescolati nell'array finale
        for (int i = 0; i < otherIndices.size(); i++) {
            order[i + 1] = otherIndices.get(i);
        }
        
        return order;
    } 
}