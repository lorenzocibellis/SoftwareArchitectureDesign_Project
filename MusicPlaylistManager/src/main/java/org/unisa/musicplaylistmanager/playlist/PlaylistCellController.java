package org.unisa.musicplaylistmanager.playlist;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * Controller per una singola cella (riga) nella ListView delle tracce.
 * Estende ListCell per integrarsi con la ListView di JavaFX.
 */
public class PlaylistCellController extends ListCell<Playlist> {

    @FXML
    private Label nameLabel;

    @FXML
    private Label detailsLabel;

    private String resourceRoot = "/org/unisa/musicplaylistmanager/";
    private HBox root;

    /**
     * Costruttore.
     */
    public PlaylistCellController() {
        loadFXML();
    }

    /**
     * Carica il file FXML associato e imposta questo controller.
     */
    private void loadFXML() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistCellView.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo chiamato da JavaFX ogni volta che la cella deve essere aggiornata.
     *
     * @param playlist L'oggetto Playlist da visualizzare in questa riga.
     * @param empty true se la riga è vuota, false altrimenti.
     */
    @Override
    protected void updateItem(Playlist playlist, boolean empty) {
        super.updateItem(playlist, empty);

        if (empty || playlist == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Popola i componenti FXML con i dati della traccia
            nameLabel.setText(playlist.getName());
            detailsLabel.setText(playlist.getSize() + " Tracce");

            // Imposta l'azione per il bottone "info"

            // Imposta il layout FXML caricato come grafica della cella
            setText(null);
            setGraphic(root);
        }
    }
}