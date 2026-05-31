package org.unisa.musicplaylistmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.IOException;

/**
 * @author gruppo10
 */
public class TrackListController {

    @FXML
    private StackPane mainStackPane;
    @FXML
    private ListView<Track> listView;

    private ObservableList<Track> trackListObservable;
    private TrackList trackList;

    @FXML
    public void initialize() {
        if (trackList == null) trackList = new TrackList();
        trackListObservable = FXCollections.observableArrayList(trackList.getTracks());

        // fa in modo che la list view usi la cella peronalizzata
        listView.setCellFactory(param -> new TrackCellController(this::showTrackDetails));

        listView.setItems(trackListObservable);

        // Gestione del doppio click su una riga per aprire il player
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Apri il player solo con un doppio click
                Track selected = listView.getSelectionModel().getSelectedItem();
                if (selected == null) return;
                openPlayerFor(selected);
            }
        });
    }

    // Metodo chiamato quando viene cliccato il bottone "i" in una riga
    private void showTrackDetails(Track track) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackView.fxml"));
            Parent root = loader.load();

            TrackController controller = loader.getController();
            // Passa la traccia e imposta la modalità di sola lettura
            controller.setTrackDetails(track);

            Stage stage = new Stage();
            stage.setTitle("Dettagli Traccia");
            stage.initModality(Modality.APPLICATION_MODAL); // Blocca l'interazione con la finestra principale
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPlayerFor(Track selected) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerView.fxml"));
            AnchorPane playerRoot = loader.load();
            PlayerController ctrl = loader.getController();
            ctrl.setPlaylistContext(selected, trackList);
            ctrl.setPlayerRoot(playerRoot);

            if (mainStackPane.getChildren().size() > 1) {
                mainStackPane.getChildren().remove(1);
            }

            mainStackPane.getChildren().add(playerRoot);
            StackPane.setAlignment(playerRoot, Pos.BOTTOM_CENTER);
            playerRoot.setTranslateY(mainStackPane.getHeight());

            TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.4), playerRoot);
            slideUp.setToY(0);
            slideUp.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewTrack(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Aggiungi Traccia");

        Scene scene = new Scene(root);
        TrackController controller = loader.getController();
        controller.setTrackList(trackList);
        controller.setObservable(trackListObservable);
        stage.setScene(scene);
        stage.show();
    }
}