package org.unisa.musicplaylistmanager.state;

import org.unisa.musicplaylistmanager.player.Player;

/**
 * @author gruppo10
 */

public interface PlayerState {
    // Riceve il Player per poterne manipolare lo stato interno
    void execute(Player player);
}