package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.Playlist;

/**
 *
 * Classe astratta che descrive l'attributo comune a tutti i comandi che lavorano con playlist
 *
 */
public abstract class BaseCommands implements AbstractCommand{
    //ATTRIBUTI
    //Permettono di lavorare sulle playlist di interesse (sia quella effettiva che quella visuale)
    private ObservableList obsList;

    protected ObservableList getObservableList() {
        return obsList;
    }

    protected void setObservableList(ObservableList obsList) {
        this.obsList = obsList;
    }
}
