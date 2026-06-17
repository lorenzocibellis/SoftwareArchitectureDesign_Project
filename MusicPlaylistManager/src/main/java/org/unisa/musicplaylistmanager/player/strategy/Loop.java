package org.unisa.musicplaylistmanager.player.strategy;

/**
 * Implementazione della Strategy per la ripetizione continua (Loop) di un singolo brano.
 * @author gruppo10
 */
public class Loop implements ExecutionStrategy {

    /**
     * Metodo per la riproduzione in loop di una traccia
     * @param size La dimensione della playlist o dell'elenco complessivo dei brani
     * @param currentIndex L'indice della traccia corrente
     * @return L'array di navigazione delle tracce per l'Iterator
     */
    @Override
    public int[] execute(int size, int currentIndex) {
        // Se la playlist è vuota, restituisce un array vuoto
        if (size <= 0) {
            return new int[0];
        }

        // Creiamo un array grande quanto la playlist
        int[] order = new int[size];

        // Riempiamo l'intero array solo con l'indice della traccia corrente.
        // In questo modo, chiamare getNext() o getPrevious() restituirà sempre lo stesso brano.
        for (int i = 0; i < size; i++) {
            order[i] = currentIndex;
        }
        return order;
    }
}