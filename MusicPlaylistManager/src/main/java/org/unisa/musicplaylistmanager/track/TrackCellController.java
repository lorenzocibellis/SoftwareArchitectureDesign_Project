package org.unisa.musicplaylistmanager.track;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Controller per una singola cella (riga) nella ListView delle tracce.
 * Estende ListCell per integrarsi con la ListView di JavaFX.
 */
public class TrackCellController extends ListCell<Track> {

    //DEFINIZIONE OGGETTI JAVAFX
    @FXML
    private Label titleLabel;
    @FXML
    private Label detailsLabel;
    @FXML
    private Button infoButton;

    // definizione attributi

    // path verso i file View.fxml
    private String resourceRoot = "/org/unisa/musicplaylistmanager/";

    // indica l'oggetto che compone tutta la vista
    private HBox root;

    // utilitaria per operazioni "on-click"
    private final Consumer<Track> onInfoClicked;


    // METODI

    /**
     * Costruttore.
     *
     * @param onInfoClicked funzione che viene eseguita quando il bottone "info" viene cliccato.
     */
    public TrackCellController(Consumer<Track> onInfoClicked) {
        this.onInfoClicked = onInfoClicked;
        loadFXML();
    }

    /**
     * Carica il file FXML associato e imposta questo controller.
     */
    private void loadFXML() {
        try {
            // caricamento della View e settaggio del controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "TrackCellView.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo chiamato da JavaFX ogni volta che la cella deve essere aggiornata.
     *
     * @param track L'oggetto Track da visualizzare in questa riga.
     * @param empty true se la riga è vuota, false altrimenti.
     */
    @Override
    protected void updateItem(Track track, boolean empty) {
        super.updateItem(track, empty);

        // controllo sull'esistenza dei dati da aggiorare
        if (empty || track == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Popola i componenti FXML con i dati della traccia
            titleLabel.setText(track.getTitle());
            detailsLabel.setText(track.getAuthor() + " - " + track.getYear().getValue());

            // Imposta l'azione per il bottone "info"
            infoButton.setOnAction(event -> {
                event.consume(); // Impedisce al click di propagarsi alla cella sottostante
                if (onInfoClicked != null) {
                    onInfoClicked.accept(track);
                }
            });

            // Imposta il layout FXML caricato come grafica della cella
            setText(null);
            setGraphic(root);
        }
    }

    // rende il bottone info visibile o nascosto in base al parametro passato, lo nasconde se siamo in AddTracksToPlaylist
    public void setInfoButtonVisible(boolean visible) {
        if (infoButton != null) {
            infoButton.setVisible(visible);
            infoButton.setManaged(visible);
        }
    }
}