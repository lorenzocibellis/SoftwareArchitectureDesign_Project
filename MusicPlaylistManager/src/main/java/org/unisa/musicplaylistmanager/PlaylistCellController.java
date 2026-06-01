package org.unisa.musicplaylistmanager;

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
public class PlaylistCellController extends ListCell<Track> {

    @FXML
    private Label detailsLabel;

    @FXML
    private Label titleLabel;

}