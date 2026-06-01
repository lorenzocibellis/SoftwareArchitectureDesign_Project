package org.unisa.musicplaylistmanager.playlist;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlaylistCreationController {

    //Definizione attributi
    @FXML
    private Button addPlaylistButton;

    @FXML
    private Button buttonBack;

    @FXML
    private TextField nameInput;

    private PlaylistList playlistList;
    private ObservableList<Playlist> playlistListObservable;

    //METODI
    private void add(Playlist p) {
        playlistList.addPlaylist(p);
        playlistListObservable.add(p);
    }

    private Playlist getPlaylist(){
        return new Playlist(nameInput.getText());
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setPlaylistList(PlaylistList p){
        this.playlistList = p;
    }

    public void setObservable(ObservableList<Playlist> o){
        this.playlistListObservable = o;
    }

    @FXML
    void addPlaylist(ActionEvent actionEvent){
        try{
            Playlist p = this.getPlaylist();
            this.add(p);
            this.goBack(actionEvent);
        } catch (Exception e) {
            showError("Errore", "Si è verificato un errore durante la creazione.", e.getMessage());
        }
    }

    @FXML
    void goBack(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
