package org.unisa.musicplaylistmanager.player.state;

import org.unisa.musicplaylistmanager.player.model.Player;

/**
 * Rappresenta lo stato "In Riproduzione" del Player (Pattern State).
 * Quando il player è in questo stato, l'azione eseguita avvierà la riproduzione
 * musicale e provocherà la transizione allo stato {@link Pause}.
 *
 * @author gruppo10
 */
public class Play implements PlayerState {
    /**
     * Esegue l'azione di avvio della riproduzione sul player.
     * Al termine dell'azione, cambia lo stato interno del player in {@link Pause}.
     * 
     * @param player il {@link Player} su cui avviare la riproduzione
     */
    @Override
    public void execute(Player player) {
        player.startPlayback();
        player.setState(new Pause()); 
    }
}
