package org.unisa.musicplaylistmanager.playlist;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.app.MusicPlaylistManagerApp;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

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

    private String resourceRoot = "/org/unisa/musicplaylistmanager/";
    private ObservableList<Playlist> playlistListObservable;
    private PlaylistList playlistList;

    //METODI

    private void openPlaylist(Playlist p) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistView.fxml"));
        Parent root = loader.load();

        PlaylistController controller = loader.getController();
        controller.setPlaylist(p);

        Stage stage = new Stage();
        stage.setTitle("Playlist");


        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize(){
        if (!PlaylistList.exists()) playlistList = new PlaylistList();
         else playlistList = PlaylistList.getPlaylistListPointer();

        playlistListObservable = FXCollections.observableArrayList(playlistList.getPlaylists());

        listView.setCellFactory(param -> new PlaylistCellController());

        listView.setItems(playlistListObservable);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deleteButton.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));


        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Apri la playlist con doppio click
                Playlist selected = listView.getSelectionModel().getSelectedItem();
                if (selected == null) return;
                try {
                    openPlaylist(selected);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void addNewPlaylist(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistCreationView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Aggiungi Playlist");

        Scene scene = new Scene(root);
        PlaylistCreationController controller = loader.getController();
        controller.setPlaylistList(playlistList);
        controller.setObservable(playlistListObservable);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void deletePlaylist(ActionEvent event) {
        ObservableList<Playlist> selectedItems = listView.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");

        // cambia il messaggio in base al numero di elementi selezionati
        if (selectedItems.size() == 1) {
            alert.setHeaderText("Sei sicuro di voler eliminare la playlist selezionata?");
            alert.setContentText("L'azione è irreversibile.");
        } else {
            alert.setHeaderText("Sei sicuro di voler eliminare le " + selectedItems.size() + " playlist selezionate?");
            alert.setContentText("L'azione è irreversibile.");
        }

        // Mostra l'alert e attendi la risposta dell'utente
        Optional<ButtonType> result = alert.showAndWait();

        // Se l'utente ha cliccato "OK"
        if (result.isPresent() && result.get() == ButtonType.OK) {

            ArrayList<Playlist> toRemove = new ArrayList<>(selectedItems);

            // Rimuovi gli elementi dalla lista osservabile e dalla tracklist
            playlistListObservable.removeAll(toRemove);
            playlistList.deletePlaylists(toRemove);
        }
    }

    @FXML
    public void goTrackList(ActionEvent actionEvent) throws IOException {
        Parent playlistParent = FXMLLoader.load(getClass().getResource(resourceRoot + "TrackListView.fxml"));

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
