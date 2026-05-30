package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

public interface PlayerState {
    // Riceve il Player per poterne manipolare lo stato interno
    void execute(Player player);
}