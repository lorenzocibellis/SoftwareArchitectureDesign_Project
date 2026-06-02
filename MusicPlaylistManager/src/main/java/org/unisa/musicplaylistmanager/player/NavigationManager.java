package org.unisa.musicplaylistmanager.player;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Singleton responsabile esclusivamente della navigazione tra schermate.
 * Gestisce lo StackPane radice e il cambio del contenuto centrale,
 *
 *
 * @author gruppo10
 */
public class NavigationManager implements Navigator {

    private static final NavigationManager instance = new NavigationManager();

    private StackPane rootLayout;
    private Parent currentContent;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        return instance;
    }

    /**
     * Inizializza lo StackPane radice e lo imposta come scena dello Stage principale.
     * Deve essere chiamato una sola volta all'avvio dell'applicazione.
     */
    @Override
    public void initRootLayout(Stage stage, Parent initialContent) {
        rootLayout = new StackPane();
        currentContent = initialContent;
        rootLayout.getChildren().add(currentContent);

        Scene scene = new Scene(rootLayout, 800, 600);
        stage.setScene(scene);
    }

    /**
     * Sostituisce il contenuto visibile (livello 0 dello StackPane)
     * senza toccare gli overlay superiori (es. mini-player).
     */
    @Override
    public void navigateTo(Parent newContent) {
        rootLayout.getChildren().remove(currentContent);
        currentContent = newContent;
        // Inserisce la nuova schermata al livello più basso, sotto gli overlay
        rootLayout.getChildren().add(0, currentContent);
    }

    /**
     * Espone il layout radice per permettere ad ActivePlayerManager
     * di aggiungere/rimuovere l'overlay del mini-player.
     */
    public StackPane getRootLayout() {
        return rootLayout;
    }
}
