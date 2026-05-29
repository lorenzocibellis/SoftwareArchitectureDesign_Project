package org.unisa.musicplaylistmanager;

/**
 *
 * @author gruppo10
 */
public interface ExecutionStrategy {
    
    // dichiarazione del metodo per eseguire o la riproduzione casuale o la riproduzione in loop
    public int[] execute(int a, int b);
}
