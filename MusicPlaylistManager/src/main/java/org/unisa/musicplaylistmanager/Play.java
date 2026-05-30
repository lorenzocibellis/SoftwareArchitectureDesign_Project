package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

public class Play implements PlayerState {
    @Override
    public void execute(Player player) {
        player.startPlayback();
        // Cambia lo stato interno del player al prossimo stato logico
        player.setState(new Pause()); 
    }
}
