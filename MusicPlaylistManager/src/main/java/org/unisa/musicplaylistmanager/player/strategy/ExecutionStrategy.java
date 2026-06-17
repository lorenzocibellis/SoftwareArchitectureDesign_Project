package org.unisa.musicplaylistmanager.player.strategy;

/**
 *
 * @author gruppo10
 */
public interface ExecutionStrategy {
    
    // dichiarazione del metodo per eseguire o la riproduzione casuale o la riproduzione in loop
    /**
     * Esegue la strategia di riproduzione.
     * 
     * @param size il primo parametro (dimensione)
     * @param currentIndex il secondo parametro (indice attuale)
     * @return un array di interi risultante dall'esecuzione
     */
    public int[] execute(int size, int currentIndex);
}
