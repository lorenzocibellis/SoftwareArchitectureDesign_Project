package org.unisa.musicplaylistmanager;

/**
 *
 * @author gruppo10
 */
public class Player {
    
    // metodi d'istanza della classe Player
    private Iterator it;
    private Track currentTrack;
    private PlayerState defaultState;
//    private PlayerState currentState;
  
    // costruttore della classe Player
    public Player(PlayerState defaultState, Playlist playlist) {
        this.defaultState = defaultState;
        //this.it = playlist.createIterator();
    }
    
    // implementazione del metodo per cambiare lo stato di riproduzione
    public void changeState() {
        
        
    }
    // 
    public void defaultState() {
    }
    // implementazione del metodo che chiama l'omonimo metodo della classe Iterator per definire la modalità di riproduzione
    public void setStrategy(ExecutionStrategy es) {
        it.setStrategy(es);
    }
    
    // implementazione del metodo che chiama l'omonimo metodo della classe Iterator per ottenere la traccia corrente
    public Track getCurrent() {
        
        return it.getCurrent();
    }
    // implementazione del metodo che chiama l'omonimo metodo della classe Iterator per ottenere la traccia successiva
    public Track getNext() {
        
        return it.getNext();
    }
    // implementazione del metodo che chiama l'omonimo metodo della classe Iterator per ottenere la traccia precedente
    public Track getPrevious() {
        
        return it.getPrevious();
    }
}
