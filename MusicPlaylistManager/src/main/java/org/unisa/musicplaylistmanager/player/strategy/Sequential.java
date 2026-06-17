package org.unisa.musicplaylistmanager.player.strategy;

/**
 * @author gruppo10
 */
public class Sequential implements ExecutionStrategy {
    
    // Implementazione della modalità di riproduzione sequenziale

    /**
     *
     * @param a Indice della traccia 1
     * @param b Indice della traccia 2
     * @return L'array di navigazione del player
     */
    @Override
    public int[] execute(int a, int b) {
        int size = a;
        int[] order = new int[size];
        
        // Popola l'array in ordine crescente: [0, 1, 2, 3, ...]
        for (int i = 0; i < size; i++) {
            order[i] = i;
        }
        
        return order;
    }
}