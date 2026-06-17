package org.unisa.musicplaylistmanager.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.core.navigation.NavigationManager;

import java.io.IOException;

/**
 * Classe principale dell'applicazione Music Playlist Manager.
 * Questa classe estende {@link javafx.application.Application} e funge da punto
 * di ingresso principale per il ciclo di vita dell'interfaccia utente JavaFX.
 *
 * @author gruppo10
 */
public class MusicPlaylistManagerApp extends Application {

    /**
     * Metodo di avvio principale di JavaFX.
     * Inizializza lo Stage principale, carica la prima schermata dell'applicazione
     * (TrackListView) e configura il layout di base (NavigationManager) per 
     * supportare la navigazione a singola finestra con player persistente.
     *
     * @param stage lo Stage primario fornito dal framework JavaFX
     * @throws IOException se il caricamento del file FXML iniziale fallisce
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlaylistManagerApp.class.getResource("/org/unisa/musicplaylistmanager/collections/tracklist/TrackListView.fxml"));
        Parent initialContent = fxmlLoader.load();

        // Inizializza il layout radice persistente tramite NavigationManager
        NavigationManager.getInstance().initRootLayout(stage, initialContent);

        stage.setTitle("Music Playlist Manager");
        stage.show();
    }

    /**
     * Metodo main di fallback, necessario per far partire correttamente
     * il processo JavaFX invocando {@link #launch(String...)}.
     * 
     * @param args gli eventuali argomenti della riga di comando
     */
    public static void main(String[] args) {
        launch();
    }
}