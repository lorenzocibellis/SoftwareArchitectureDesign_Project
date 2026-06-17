package org.unisa.musicplaylistmanager.player.strategy;

/**
 * @author gruppo10
 */
public class Sequential implements ExecutionStrategy {
    
    // Implementazione della modalità di riproduzione sequenziale

    /**
     *
     * @param size La dimensione della playlist o dell'elenco complessivo dei brani
     * @param currentIndex L'indice della traccia corrente
     * @return L'array di navigazione del player
     */
    @Override
    public int[] execute(int size, int currentIndex) {
        int[] order = new int[size];
        
        // Popola l'array in ordine crescente: [0, 1, 2, 3, ...]
        for (int i = 0; i < size; i++) {
            order[i] = i;
        }
        
        return order;
    }
}