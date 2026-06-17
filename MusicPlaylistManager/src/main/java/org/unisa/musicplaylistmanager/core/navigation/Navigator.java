package org.unisa.musicplaylistmanager.core.navigation;

import javafx.scene.Parent;

/**
 * Interfaccia per la gestione della navigazione tra schermate.
 *
 * @author gruppo10
 */
public interface Navigator {

    /**
     * Inizializza il layout radice persistente dell'applicazione.
     *
     * @param stage           lo Stage principale di JavaFX
     * @param initialContent  il contenuto iniziale da visualizzare
     */
    void initRootLayout(javafx.stage.Stage stage, Parent initialContent);

    /**
     * Sostituisce il contenuto centrale della finestra mantenendo il mini-player visibile.
     *
     * @param newContent il nuovo nodo da mostrare come schermata principale
     */
    void navigateTo(Parent newContent);
}
