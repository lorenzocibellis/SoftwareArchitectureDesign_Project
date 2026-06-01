package org.unisa.musicplaylistmanager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PlaylistListController {

    @FXML
    private Button button1;

    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button tracksButton;

    @FXML
    private ListView<?> listView;

    @FXML
    private StackPane mainStackPane;

    @FXML
    void addNewPlaylist(ActionEvent event) {

    }

    @FXML
    void deletePlaylist(ActionEvent event) {

    }

    @FXML
    public void goTrackList(ActionEvent actionEvent) throws IOException {
        Parent playlistParent = FXMLLoader.load(getClass().getResource("TrackListView.fxml"));

        // 2. Crea la nuova scena
        Scene playlistScene = new Scene(playlistParent);

        // 3. Ottieni lo stage (finestra) corrente dall'evento
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // 4. Cambia la scena
        window.setScene(playlistScene);
        window.show();
    }

    @FXML
    public void closeApp(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        // Termina l'intero programma
        System.exit(0);

    }

}
