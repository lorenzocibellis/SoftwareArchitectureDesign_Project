package org.unisa.musicplaylistmanager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    //Dichiarazione attributi
    @FXML
    private Button button1;

    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button tracksButton;

    @FXML
    private ListView<Playlist> listView;

    @FXML
    private StackPane mainStackPane;

    private ObservableList<Playlist> playlistListObservable;
    private PlaylistList playlistList;

    //METODI
    @FXML
    public void initialize(){
        if (!PlaylistList.exists()) playlistList = new PlaylistList();
         else playlistList = PlaylistList.getPlaylistListPointer();

        playlistListObservable = FXCollections.observableArrayList(playlistList.getPlaylists());

        listView.setCellFactory(param -> new PlaylistCellController());

        listView.setItems(playlistListObservable);
    }

    @FXML
    void addNewPlaylist(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlaylistCreationView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Aggiungi Traccia");

        Scene scene = new Scene(root);
        PlaylistCreationController controller = loader.getController();
        controller.setPlaylistList(playlistList);
        controller.setObservable(playlistListObservable);
        stage.setScene(scene);
        stage.show();
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
