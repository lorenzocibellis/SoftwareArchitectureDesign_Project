package org.unisa.musicplaylistmanager.service.navigation;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;

/**
 * Singleton responsabile esclusivamente della navigazione tra schermate.
 * Gestisce lo {@link StackPane} radice e il cambio del contenuto centrale (livello 0),
 * consentendo di mantenere intatti gli overlay superiori (come il mini-player).
 *
 * @author gruppo10
 */
public class NavigationManager implements Navigator {

    private static final NavigationManager instance = new NavigationManager();

    private StackPane rootLayout;
    private Parent currentContent;

    private NavigationManager() {}

    /**
     * Restituisce l'istanza singleton di {@code NavigationManager}.
     * 
     * @return l'unica istanza di questa classe
     */
    public static NavigationManager getInstance() {
        return instance;
    }

    /**
     * Inizializza lo StackPane radice e lo imposta come scena dello Stage principale.
     * Deve essere chiamato una sola volta all'avvio dell'applicazione.
     * 
     * @param stage lo Stage principale di JavaFX
     * @param initialContent il contenuto iniziale da visualizzare al livello 0
     */
    @Override
    public void initRootLayout(Stage stage, Parent initialContent) {
        rootLayout = new StackPane();
        currentContent = initialContent;
        rootLayout.getChildren().add(currentContent);

        Scene scene = new Scene(rootLayout, 1100, 740);
        stage.setScene(scene);
    }

    /**
     * Sostituisce il contenuto visibile (livello 0 dello StackPane)
     * senza toccare gli overlay superiori (es. mini-player).
     * 
     * @param newContent il nuovo contenuto da mostrare
     */
    @Override
    public void navigateTo(Parent newContent) {
        rootLayout.getChildren().remove(currentContent);
        currentContent = newContent;
        // Inserisce la nuova schermata al livello più basso, sotto gli overlay
        rootLayout.getChildren().add(0, currentContent);
    }

    /**
     * Espone il layout radice per permettere ad {@link ActivePlayerManager}
     * di aggiungere o rimuovere l'overlay del mini-player al livello superiore.
     * 
     * @return lo {@code StackPane} radice dell'applicazione
     */
    public StackPane getRootLayout() {
        return rootLayout;
    }
}
