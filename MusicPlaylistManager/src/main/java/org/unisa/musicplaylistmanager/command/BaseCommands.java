package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;

/**
 *
 * Classe astratta che descrive l'attributo comune a tutti i comandi che lavorano con playlist.
 *
 */
public abstract class BaseCommands implements AbstractCommand{
    //ATTRIBUTI
    //Permettono di lavorare sulle playlist di interesse (sia quella effettiva che quella visuale)
    private ObservableList obsList;

    /**
     *
     * Metodo che permette di ottenere il riferimento alla lista osservabile da modificare.
     *
     * @return ritorna la lista osservabile di interesse.
     *
     */
    protected ObservableList getObservableList() {
        return obsList;
    }

    /**
     *
     * Metodo che permette di settare la lista osserabile sulla quale si lavora.
     *
     * @param obsList Lista osservabile da settare.
     *
     */
    protected void setObservableList(ObservableList obsList) {
        this.obsList = obsList;
    }
}
