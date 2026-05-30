package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

public class Pause implements PlayerState {
    @Override
    public void execute(Player player) {
        player.stopPlayback();
        // Cambia lo stato interno del player al prossimo stato logico
        player.setState(new Play()); 
    }
}