package org.unisa.musicplaylistmanager;

/**
 *
 * @author gruppo10
 */
public class Iterator implements AbstractIterator {
    
    // variabili di istanza della classe Iterator
    private int currentindext;
    private int[] iterationindex;
    private Playlist playlist;

    // costruttore della classe Iterator
    public Iterator(Playlist playlist) {
        this.playlist = playlist;
    }
    
    // implementazione del metodo per ottenere la traccia corrente in esecuzione
    @Override
    public Track getCurrent() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // implementazione del metodo per ottenere la traccia successiva
    @Override
    public Track getNext() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    // implementazione del metodo per ottenere la traccia precedente
    @Override
    public Track getPrevious() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    // implementazione del metodo per definire la modalità di riproduzione 
    @Override
    public void setStrategy(ExecutionStrategy es) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
