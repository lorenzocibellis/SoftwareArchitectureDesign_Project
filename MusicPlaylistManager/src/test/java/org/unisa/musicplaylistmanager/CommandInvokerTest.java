package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.command.AbstractCommand;
import org.unisa.musicplaylistmanager.command.CommandInvoker;

import static org.junit.jupiter.api.Assertions.*;

class CommandInvokerTest {

    private CommandInvoker invoker;

    private int x = 10;

    @BeforeEach
    void setUp() {
        // Otteniamo l'istanza del singleton
        invoker = CommandInvoker.getCommandInvokerPointer();
        // Svuotiamo la coda prima di ogni test
        try {
            while (true) { invoker.undoCommand(); }
        } catch (IndexOutOfBoundsException e) { /* la coda è vuota*/ }
    }

    @Test
    void testSingletonIsUnique() {
        CommandInvoker instance1 = CommandInvoker.getCommandInvokerPointer();
        CommandInvoker instance2 = CommandInvoker.getCommandInvokerPointer();
        assertSame(instance1, instance2, "Per Pattern Singleton devono avere la stessa istanza.");
    }

    @Test
    void testEsecuzioneEAnnullamento() {
        DummyCommand cmd = new DummyCommand(1);

        invoker.setCommand(cmd);
        // Verifichiamo che l'operazione si possa fare
        assertDoesNotThrow(() -> invoker.undoCommand(), "L'undo deve funzionare dopo un setCommand.");
    }

    @Test
    void testLimiteCapacitàCoda() {
        // Il limite definito nella classe è x (numero definito nella definzione della classe)
        for (int i = 1; i <= x + 1; i++) {
            invoker.setCommand(new DummyCommand(i));
        }

        // Dopo x+1 inserimenti, il primo comando (1) dovrebbe essere stato rimosso
        // Se facciamo x+1 undo, lancio eccezione.
        assertDoesNotThrow(() -> {
            for (int i = 0; i < x; i++) invoker.undoCommand();
        });

        assertThrows(IndexOutOfBoundsException.class, () -> invoker.undoCommand(),
                "La coda deve essere vuota dopo x undo.");
    }

    @Test
    void testEccezioneCodaVuota() {
        assertThrows(IndexOutOfBoundsException.class, () -> invoker.undoCommand(),
                "L'undo deve lanciare IndexOutOfBounds.");
    }


    // Classe per implementare interfaccia AbstractCommand
    class DummyCommand implements AbstractCommand {
        private int i;
        public DummyCommand(int i) { this.i = i; }
        @Override public void execute() { System.out.println("do " + i); }
        @Override public void undo() { System.out.println("undo: " + i); }
    }
}