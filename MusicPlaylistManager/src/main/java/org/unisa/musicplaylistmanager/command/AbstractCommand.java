package org.unisa.musicplaylistmanager.command;

/**
 * Interfaccia che definisce i metodi comuni a tutti i comandi
 *
 */
public interface AbstractCommand {

    /**
     *
     * Metodo che implementa l'annullamento (undo) del comando
     *
     */
    public void undo();

    /**
     *
     * metodo che implementa l'esecuzione del comando
     *
     */
    public void execute();

}
