package org.unisa.musicplaylistmanager.playlist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackCellController;

import java.util.List;
import java.util.ArrayList;

public class AddTracksToPlaylistController {

    @FXML
    private ListView<Track> listView;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private List<Track> selectedTracks = new ArrayList<>();
    private boolean confirmed = false;

    @FXML
    void initialize() {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Imposta TrackCellController per mostrare le tracce con il layout personalizzato.
        // Poiché in questa finestra stiamo solo selezionando tracce,
        // possiamo passare un'azione vuota per il pulsante info.
        listView.setCellFactory(param -> {
            TrackCellController cell = new TrackCellController(track -> {});
            cell.setInfoButtonVisible(false);
            return cell;
        });
    }

    public void setAvailableTracks(List<Track> availableTracks) {
        ObservableList<Track> observableTracks = FXCollections.observableArrayList(availableTracks);
        listView.setItems(observableTracks);
    }

    @FXML
    void confirmSelection(ActionEvent event) {
        selectedTracks.addAll(listView.getSelectionModel().getSelectedItems());
        confirmed = true;
        closeWindow();
    }

    @FXML
    void cancel(ActionEvent event) {
        confirmed = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Track> getSelectedTracks() {
        return selectedTracks;
    }
}