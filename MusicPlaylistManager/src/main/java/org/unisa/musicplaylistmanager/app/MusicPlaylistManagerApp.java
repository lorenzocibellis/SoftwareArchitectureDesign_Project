package org.unisa.musicplaylistmanager.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.player.NavigationManager;

import java.io.IOException;

public class MusicPlaylistManagerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlaylistManagerApp.class.getResource("/org/unisa/musicplaylistmanager/TrackListView.fxml"));
        Parent initialContent = fxmlLoader.load();

        // Inizializza il layout radice persistente tramite NavigationManager
        NavigationManager.getInstance().initRootLayout(stage, initialContent);

        stage.setTitle("Music Playlist Manager");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}