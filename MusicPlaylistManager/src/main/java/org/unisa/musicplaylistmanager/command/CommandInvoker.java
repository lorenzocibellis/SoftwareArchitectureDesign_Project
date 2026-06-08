package org.unisa.musicplaylistmanager.command;

import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayDeque;


/**
 * Classe che implementa la logica di esecuzione, memorizzazione e annullamento delle operazioni.
 *
 * Questa Classe è il punto di ingresso del pattern Command, in modo da centralizzare la gestione dei vari comandi.
 * Implementa inoltre il pattern Singleton in modo da limitare il numero di istanze esistenti e per permettere l'ottenimento
 * dell'istanza esistente senza doverne creare una nuova.
 * Le varie classi che usano i comandi gestiti dal CommandInvoker si limitano a creare i comandi, per poi passarli all'invoker
 * che si preoccuperà di memorizzarli e annullarli (nel caso venga richiamato l'apposito metodo)
 */
public class CommandInvoker {

    //ATTRIBUTI
    //Double-Ended queue per gestire in modo ottimale l'aggiunta e rimozione di comandi
    private ArrayDeque<AbstractCommand> commands;
    //Attributo final che gestisce la dimensione massima della lista
    private final int SIZE_LIMIT = 10;
    //Attributo che permette
    private static CommandInvoker pnt = null;

    //METODI

    /**
     * Costruttore
     *
     */
    private CommandInvoker(){
        commands = new ArrayDeque<>();
        pnt = this;
    }

    /**
     *
     * Funzione che permette di settare un comando nella lista di comandi eseguiti
     *
     * @param command Comando da aggiungere alla lista
     *
     */
    public void setCommand(AbstractCommand command){

        if (commands.size() < SIZE_LIMIT){
            command.execute();
            commands.addLast(command);
            return;
        }
        commands.removeFirst();
        command.execute();
        commands.addLast(command);
    }


    /**
     *
     * Funzione che permette di fare l'undo di un comando precedentemente eseguito
     *
     * @throws ArrayIndexOutOfBoundsException Lanciata quando la lista di comandi è vuota
     *
     */
    public void undoCommand(){
        if (!commands.isEmpty()){
            commands.removeLast().undo();
            return;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    /**
     *
     * Funzione che permette l'ottenimento del puntatore all'istanza esistente di questa classe.
     *
     * @return Ritorna il puntatore all'istanza di questa classe se esiste, altrimenti crea un'istanza e ne ritorna il puntatore
     *
     */
    public static CommandInvoker getCommandInvokerPointer(){
        if (exists()) return pnt;
        return new CommandInvoker();
    }

    public static boolean exists(){
        return !(pnt == null);
    }
}
