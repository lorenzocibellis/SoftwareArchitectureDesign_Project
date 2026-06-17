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
     * @param size La dimensione della playlist o dell'elenco complessivo dei brani
     * @param currentIndex L'indice della traccia corrente
     * @return L'array di navgiazione del player
     */
    @Override
    public int[] execute(int size, int currentIndex) {
        int[] order = new int[size];
        
        if (size <= 0) return order;

        // 1. Mettiamo la traccia corrente in prima posizione
        order[0] = currentIndex;
        
        // 2. Creiamo una lista con tutti gli altri indici della playlist
        List<Integer> otherIndices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (i != currentIndex) {
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