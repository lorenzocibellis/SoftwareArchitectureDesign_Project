package org.unisa.musicplaylistmanager.collections.playlist.creation;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.collections.playlist.model.Playlist;
import org.unisa.musicplaylistmanager.collections.playlist.model.PlaylistList;

import java.io.IOException;

/**
 * Controller per la finestra di scelta della modalità di creazione playlist.
 * Permette all'utente di scegliere tra creazione manuale e automatica.
 *
 * @author gruppo10
 */
public class PlaylistChooseController {

    // Definizione attributi

    // path alle view
    private String resourceRoot = "/org/unisa/musicplaylistmanager/playlist/";

    // riferimento alla lista di playlist
    private PlaylistList playlistList;

    // riferimento alla lista di playlist osservabile
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

    /**
     *
     * Permette di chiudere la schermata attuale.
     *
     * @param actionEvent evento che genera una chiamata a questa funzione.
     *
     */
    @FXML
    public void goBack(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     *
     * Permette di aprire la schermata che verrà utilizzata per generare una playlist automaticamente.
     *
     * @param actionEvent Evento che genera la chiamata di funzione.
     *
     * @throws IOException Eccezione lanciata nel caso di errori.
     *
     */
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

    /**
     *
     * Permette di aprire la schermata che verrà utilizzata per generare una playlist vuota.
     *
     * @param actionEvent Evento che genera la chiamata di funzione.
     *
     * @throws IOException Eccezione lanciata nel caso di errori.
     *
     */
    @FXML
    public void createManual(ActionEvent actionEvent) throws IOException {
        // Chiudi la finestra di scelta
        Stage chooseStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        chooseStage.close();

        // Apri la finestra di creazione manuale
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistManualView.fxml"));
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
