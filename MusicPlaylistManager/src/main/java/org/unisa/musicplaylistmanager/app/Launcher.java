package org.unisa.musicplaylistmanager.app;

/**
 * Classe di lancio alternativa per l'applicazione.
 * Questa classe risolve problemi comuni di caricamento delle librerie grafiche all'avvio
 * dell'applicazione tramite file JAR.
 *
 * @author gruppo10
 */
public class Launcher {
    /**
     * Il punto di ingresso per la JVM quando eseguita come jar.
     * Invoca il metodo main della vera applicazione JavaFX.
     * 
     * @param args gli eventuali argomenti della riga di comando passati all'avvio
     */
    public static void main(String[] args) {
        MusicPlaylistManagerApp.main(args);
    }
}
