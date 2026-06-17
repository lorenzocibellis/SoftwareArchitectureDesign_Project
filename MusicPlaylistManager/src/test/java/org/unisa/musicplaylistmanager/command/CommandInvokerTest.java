package org.unisa.musicplaylistmanager.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.core.command.AbstractCommand;
import org.unisa.musicplaylistmanager.core.command.CommandInvoker;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Classe di test per {@link CommandInvoker}
 *
 */
public class CommandInvokerTest {

    //Attributi
    private CommandInvoker invoker;

    //Dimensione della coda dei comandi
    private final int x = 10;

    @BeforeEach
    void setUp() {
        // Otteniamo l'istanza del Singleton
        invoker = CommandInvoker.getCommandInvokerPointer();

        // Pulizia manuale della coda
        try {
            while (true) {
                invoker.undoCommand();
            }
        } catch (IndexOutOfBoundsException e) {
            // La coda è stata svuotata prima di ogni test
        }
    }

    /**
     *
     * Verifica dell'integrità del pattern Singleton.
     * Controlla che la classe non possa generare istanze multiple e che mantenga lo stato globale.
     *
     */
    @Test
    void testSingletonInvariance() {
        assertTrue(CommandInvoker.exists(), "L'invoker deve risultare esistente dopo l'inizializzazione.");
        CommandInvoker pnt2 = CommandInvoker.getCommandInvokerPointer();
        assertSame(invoker, pnt2, "Il pattern Singleton deve restituire lo stesso identico riferimento in memoria.");
    }

    /**
     *
     * Verifica l'ordine LIFO (Last In, First Out) dei comandi.
     * Permette di osservare visivamente le stampe in console nell'ordine corretto.
     *
     */
    @Test
    void testOrdineUndoStandard() {
        System.out.println("=== INIZIO: testOrdineUndoStandard ===");
        DummyCommand cmd1 = new DummyCommand(1);
        DummyCommand cmd2 = new DummyCommand(2);
        DummyCommand cmd3 = new DummyCommand(3);

        // Stampa dei msg
        invoker.setCommand(cmd1); // "do 1"
        invoker.setCommand(cmd2); // "do 2"
        invoker.setCommand(cmd3); // "do 3"

        // L'ordine di annullamento deve essere inverso rispetto all'inserimento
        invoker.undoCommand(); // "undo: 3"
        invoker.undoCommand(); // "undo: 2"
        invoker.undoCommand(); // "undo: 1"
        System.out.println("=== FINE: testOrdineUndoStandard ===\n");
    }

    /**
     *
     * Verifica che il sistema risponda con l'eccezione corretta se non ci sono operazioni da annullare.
     *
     */
    @Test
    void testEccezioneCodaVuota() {
        assertThrows(IndexOutOfBoundsException.class, () -> invoker.undoCommand(),
                "L'undo effettuato su una coda vuota deve lanciare IndexOutOfBoundsException.");
    }

    /**
     *
     * Raggiungimento dell'esatta capacità massima della coda (SIZE_LIMIT = x).
     * Verifica che la coda accetti esattamente x elementi e permetta di annullarli tutti.
     *
     */
    @Test
    void testLimiteEsattoCapacita() {
        System.out.println("=== INIZIO: testLimiteEsattoCapacita ===");
        // Riempiamo la coda fino al limite esatto di x
        for (int i = 1; i <= x; i++) {
            invoker.setCommand(new DummyCommand(i));
        }

        // Tutti e gli x i comandi devono essere memorizzati e annullabili senza eccezioni
        assertDoesNotThrow(() -> {
            for (int i = 0; i < x; i++) {
                invoker.undoCommand();
            }
        }, "Il sistema deve gestire ed effettuare l'undo di tutti gli x i comandi memorizzati.");
        System.out.println("=== FINE: testLimiteEsattoCapacita ===\n");
    }

    /**
     *
     * Superamento della capacità massima (Overflow FIFO).
     * Inserendo 11 comandi, il comando 1 deve essere rimosso.
     * Gli undo rimanenti devono essere solo x (dal comando x+1 fino al comando 2).
     *
     */
    @Test
    void testSuperamentoLimiteCapacita() {
        System.out.println("=== INIZIO: testSuperamentoLimiteCapacita ===");
        // Inseriamo x+1 elementi. Il primo ("do 1") viene rimosso automaticamente dalla testa (commands.removeFirst())
        for (int i = 1; i <= x+1; i++) {
            invoker.setCommand(new DummyCommand(i));
        }

        // Svuotiamo gli x comandi rimasti disponibili (dall'11 al 2)
        for (int i = 0; i < x; i++) {
            invoker.undoCommand();
        }

        // L'x-esimo+1 undo deve fallire perché il comando 1 è stato eliminato dalla coda
        assertThrows(IndexOutOfBoundsException.class, () -> invoker.undoCommand(),
                "Il primo comando inserito deve essere rimosso e l'x-esimo+1 undo deve lanciare eccezione.");
        System.out.println("=== FINE: testSuperamentoLimiteCapacita ===\n");
    }

    /**
     *
     * Robustezza agli input non validi (Null pointer).
     * Verifica come reagisce l'invoker se viene passato un oggetto nullo.
     *
     */
    @Test
    void testInserimentoComandoNull() {
        assertThrows(NullPointerException.class, () -> invoker.setCommand(null),
                "L'invoker deve sollevare un NullPointerException se viene passato un comando nullo.");
    }

    /**
     *
     * Classe di supporto interna per implementare un AbstractCommand che permetta una visualizzazione semplice
     * degli effetti dei test sui comandi
     *
     */
    class DummyCommand implements AbstractCommand {
        private int i;
        public DummyCommand(int i){
            this.i = i;
        }
        @Override public void execute() {
            System.out.println("do " + i);
        }
        @Override public void undo() {
            System.out.println("undo: " + i);
        }
    }
}