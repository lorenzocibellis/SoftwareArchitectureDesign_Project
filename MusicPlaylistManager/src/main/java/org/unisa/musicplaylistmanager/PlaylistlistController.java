package org.unisa.musicplaylistmanager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PlaylistlistController {

    @FXML
    private Button button1;

    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;

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
    public void closeApp(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        // Termina l'intero programma
        System.exit(0);

    }

}
