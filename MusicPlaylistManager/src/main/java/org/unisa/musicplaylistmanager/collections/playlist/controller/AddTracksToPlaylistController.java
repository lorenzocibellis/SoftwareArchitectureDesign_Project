package org.unisa.musicplaylistmanager.collections.playlist.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.track.model.Track;
import org.unisa.musicplaylistmanager.track.controller.TrackCellController;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller per la finestra di dialogo che permette l'aggiunta
 * di tracce esistenti a una playlist.
 *
 * @author gruppo10
 */
public class AddTracksToPlaylistController {

    @FXML
    private ListView<Track> listView;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private ArrayList<Track> selectedTracks = new ArrayList<>();
    private boolean confirmed = false;

    /**
     * Inizializza il controller. 
     * Imposta la modalità di selezione multipla per la ListView e disabilita
     * il bottone info per le celle della list view.
     */
    @FXML
    void initialize() {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Imposta TrackCellController per mostrare le tracce con il layout personalizzato.
        // Poiché in questa finestra stiamo solo selezionando tracce,
        // possiamo passare un'azione vuota per il pulsante info.
        listView.setCellFactory(param -> {
            TrackCellController cell = new TrackCellController(track -> {}, track -> {}, track -> {});
            cell.setInfoButtonVisible(false);
            cell.setMoveButtonVisible(false);
            return cell;
        });
    }

    /**
     * Popola la ListView con le tracce disponibili per l'aggiunta.
     * 
     * @param availableTracks la lista delle tracce non ancora presenti nella playlist
     */
    public void setAvailableTracks(List<Track> availableTracks) {
        ObservableList<Track> observableTracks = FXCollections.observableArrayList(availableTracks);
        listView.setItems(observableTracks);
    }

    /**
     * Gestisce l'azione del pulsante di conferma.
     * Memorizza le tracce selezionate e chiude la finestra.
     * 
     * @param event l'evento ActionEvent generato dal click
     */
    @FXML
    void confirmSelection(ActionEvent event) {
        selectedTracks.addAll(listView.getSelectionModel().getSelectedItems());
        confirmed = true;
        closeWindow();
    }

    /**
     * Gestisce l'azione del pulsante di annullamento.
     * Segna l'operazione come non confermata e chiude la finestra.
     * 
     * @param event l'evento ActionEvent generato dal click
     */
    @FXML
    void cancel(ActionEvent event) {
        confirmed = false;
        closeWindow();
    }

    /**
     * Metodo di utilità per chiudere la finestra modale corrente.
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Verifica se l'utente ha confermato la selezione delle tracce.
     * 
     * @return {@code true} se ha confermato, {@code false} se ha annullato
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Restituisce la lista delle tracce selezionate dall'utente.
     * 
     * @return una lista di oggetti {@link Track}
     */
    public ArrayList<Track> getSelectedTracks() {
        return selectedTracks;
    }
}