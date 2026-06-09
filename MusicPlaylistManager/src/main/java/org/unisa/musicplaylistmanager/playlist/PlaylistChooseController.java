package org.unisa.musicplaylistmanager.playlist;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller per la finestra di scelta della modalità di creazione playlist.
 * Permette all'utente di scegliere tra creazione manuale e automatica.
 *
 * @author gruppo10
 */
public class PlaylistChooseController {
    @FXML
    private Button buttonManual;
    @FXML
    private Button buttonAutomatic;
    @FXML
    private Button buttonBack;
    private String resourceRoot = "/org/unisa/musicplaylistmanager/playlist/";

    private PlaylistList playlistList;
    private ObservableList<Playlist> playlistListObservable;

    /**
     * Imposta il riferimento alla collezione globale delle playlist.
     *
     * @param p l'oggetto {@link PlaylistList}
     */
    public void setPlaylistList(PlaylistList p) {
        this.playlistList = p;
    }

    /**
     * Imposta la lista osservabile per aggiornamento UI.
     *
     * @param o l'{@link ObservableList} delle playlist
     */
    public void setObservable(ObservableList<Playlist> o) {
        this.playlistListObservable = o;
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void createAutomatic(ActionEvent actionEvent) throws IOException {
        // Chiudi la finestra di scelta
        Stage chooseStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        chooseStage.close();

        // Apri la finestra di creazione automatica
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistAutomaticView.fxml"));
        Parent root = loader.load();

        PlaylistAutomaticController controller = loader.getController();
        controller.setPlaylistList(playlistList);
        controller.setObservable(playlistListObservable);

        Stage stage = new Stage();
        stage.setTitle("Creazione Automatica Playlist");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void createManual(ActionEvent actionEvent) throws IOException {
        // Chiudi la finestra di scelta
        Stage chooseStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        chooseStage.close();

        // Apri la finestra di creazione manuale
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistCreationView.fxml"));
        Parent root = loader.load();

        PlaylistManualController controller = loader.getController();
        controller.setPlaylistList(playlistList);
        controller.setObservable(playlistListObservable);

        Stage stage = new Stage();
        stage.setTitle("Aggiungi Playlist");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
