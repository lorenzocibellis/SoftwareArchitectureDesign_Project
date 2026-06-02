package org.unisa.musicplaylistmanager.state;

import org.unisa.musicplaylistmanager.player.Player;

/**
 * Rappresenta lo stato "In Pausa" del Player (Pattern State).
 * Quando il player è in questo stato, l'azione eseguita fermerà la riproduzione
 * musicale e provocherà la transizione allo stato {@link Play}.
 *
 * @author gruppo10
 */
public class Pause implements PlayerState {
    /**
     * Esegue l'azione di pausa della riproduzione sul player.
     * Al termine dell'azione, cambia lo stato interno del player in {@link Play}.
     * 
     * @param player il {@link Player} su cui mettere in pausa la riproduzione
     */
    @Override
    public void execute(Player player) {
        player.stopPlayback();
        player.setState(new Play()); 
    }
}