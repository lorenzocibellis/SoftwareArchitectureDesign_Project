package org.unisa.musicplaylistmanager.playlist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Controller per una singola cella (riga) nella ListView delle playlist.
 * Estende {@link ListCell} per integrarsi con la ListView di JavaFX.
 *
 * @author gruppo10
 */
public class PlaylistCellController extends ListCell<Playlist> {

    // DEFINIZIONE OGGETTI FXML
    @FXML
    private Label nameLabel;
    @FXML
    private Label detailsLabel;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;


    private String resourceRoot = "/org/unisa/musicplaylistmanager/playlist/";
    private HBox root;


    private Consumer<Playlist> moveUp;
    private Consumer<Playlist> moveDown;


    /**
     * Costruttore.
     */
    public PlaylistCellController() {
        // carico la View sulla UI
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
     * @param playlist l'oggetto {@link Playlist} da visualizzare in questa riga
     * @param empty    {@code true} se la riga è vuota, {@code false} altrimenti
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
            detailsLabel.setText(playlist.getSize() + (playlist.getSize() == 1 ? " Traccia" : " Tracce"));

            // Imposta il layout FXML caricato come grafica della cella
            setText(null);
            setGraphic(root);
        }
    }

    public void moveUp(ActionEvent event){

    }

    public void moveDown(ActionEvent event){

    }
}