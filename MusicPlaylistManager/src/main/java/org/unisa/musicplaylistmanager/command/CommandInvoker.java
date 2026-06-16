package org.unisa.musicplaylistmanager.command;

import java.util.ArrayDeque;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


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
    //Attributo che permette di implementare il pattern Singleton e l'ottenimento dell'istanza del CommandInvoker
    private static CommandInvoker pnt = null;
    
    private BooleanProperty hasCommandsToUndo = new SimpleBooleanProperty(false);

    //METODI
    
    public BooleanProperty hasCommandsToUndoProperty() {
        return hasCommandsToUndo;
    }

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

        // controllo che la dimensione della coda di comandi non sia piena
        if (commands.size() >= SIZE_LIMIT){
            // se la coda è piena:
            // rimuovo il comando più "vecchio" della coda
            commands.removeFirst();
        }
        // eseguo il comando da inserire
        command.execute();
        // e lo aggiungo alla fine della coda
        commands.addLast(command);
        hasCommandsToUndo.set(!commands.isEmpty());
    }


    /**
     *
     * Funzione che permette di fare l'undo di un comando precedentemente eseguito
     *
     * @throws ArrayIndexOutOfBoundsException Lanciata quando la lista di comandi è vuota
     *
     */
    public void undoCommand(){

        // controllo se la coda di comandi è piena
        if (commands.isEmpty()){
            // se lo è lancio un'eccezione
            throw new IndexOutOfBoundsException();
        }
        //altrimenti:
        //rimuovo il comando più "nuovo" e ne annullo gli effetti
        commands.removeLast().undo();
        hasCommandsToUndo.set(!commands.isEmpty());
    }

    /**
     *
     * Funzione che permette l'ottenimento del puntatore all'istanza esistente di questa classe.
     *
     * @return Ritorna il puntatore all'istanza di questa classe se esiste, altrimenti crea un'istanza e ne ritorna il puntatore
     *
     */
    public static CommandInvoker getCommandInvokerPointer(){
        // se è già stata istanziata un'istanza di questa classe, ne ritorno il puntatore
        if (exists()) return pnt;
        // altrimenti la istanzio e ne ritorno il puntatore
        return new CommandInvoker();
    }


    /**
     *
     * Funzione utilitaria che permette di sapere se è già stata istanziata un'istanza dell'oggetto
     *
     * @return True se la classe è già stata istanziata, False altrimenti
     */
    public static boolean exists(){
        return !(pnt == null);
    }
}
