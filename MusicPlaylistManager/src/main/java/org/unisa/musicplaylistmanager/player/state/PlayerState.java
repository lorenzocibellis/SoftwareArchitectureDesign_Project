package org.unisa.musicplaylistmanager.player.state;

import org.unisa.musicplaylistmanager.player.model.Player;

/**
 * Interfaccia comune per gli stati del Player (Pattern State).
 * Definisce il comportamento che deve essere implementato da ogni stato 
 * per poter eseguire azioni specifiche sul player.
 *
 * @author gruppo10
 */
public interface PlayerState {
    /**
     * Esegue l'azione specifica associata a questo stato e gestisce
     * l'eventuale transizione allo stato successivo.
     * 
     * @param player il {@link Player} su cui agire
     */
    void execute(Player player);
}