package org.unisa.musicplaylistmanager;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author gruppo10
 */
public class TrackListController {

    @FXML
    private StackPane mainStackPane;
    @FXML
    private ListView<Track> listView;

    @FXML
    private Button deleteButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button goPlaylistButton;


    private ObservableList<Track> trackListObservable;
    private TrackList trackList;

    @FXML
    public void initialize() {
        if (trackList == null) trackList = new TrackList();
        trackListObservable = FXCollections.observableArrayList(trackList.getTracks());

        // fa in modo che la list view usi la cella personalizzata
        listView.setCellFactory(param -> new TrackCellController(this::showTrackDetails));

        listView.setItems(trackListObservable);

        //Abilito la selezione multipla di elementi
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Lega la proprietà 'disable' del bottone 'deleteButton' allo stato della selezione della lista.
        // Se non ci sono elementi selezionati (isEmpty() è true), il bottone sarà disabilitato.
        deleteButton.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));


        // Gestione del doppio click su una riga per aprire il player
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Apri il player con un doppio click
                Track selected = listView.getSelectionModel().getSelectedItem();
                if (selected == null) return;
                openPlayerFor(selected);
            }
        });

    }

    @FXML
    void goPlaylist(ActionEvent event) throws IOException {
        Parent playlistParent = FXMLLoader.load(getClass().getResource("PlaylistListView.fxml"));

        // 2. Crea la nuova scena
        Scene playlistScene = new Scene(playlistParent);

        // 3. Ottieni lo stage (finestra) corrente dall'evento
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 4. Cambia la scena
        window.setScene(playlistScene);
        window.show();
    }

    // Metodo chiamato quando viene cliccato il bottone "i" in una riga
    private void showTrackDetails(Track track) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackView.fxml"));
            Parent root = loader.load();

            TrackController controller = loader.getController();
            // Passa la traccia e imposta la modalità di sola lettura
            controller.setTrackDetails(track);
            // Passa anche la lista osservabile per permettere l'aggiornamento della lista nella UI
            // se viene modificata una traccia
            controller.setObservable(trackListObservable);

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

    public void deleteTrack(ActionEvent actionEvent) {
        ObservableList<Track> selectedItems = listView.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");

        // cambia il messaggio in base al numero di elementi selezionati
        if (selectedItems.size() == 1) {
            alert.setHeaderText("Sei sicuro di voler eliminare la traccia selezionata?");
            alert.setContentText("L'azione è irreversibile.");
        } else {
            alert.setHeaderText("Sei sicuro di voler eliminare le " + selectedItems.size() + " tracce selezionate?");
            alert.setContentText("L'azione è irreversibile.");
        }

        // Mostra l'alert e attendi la risposta dell'utente
        Optional<ButtonType> result = alert.showAndWait();

        // Se l'utente ha cliccato "OK"
        if (result.isPresent() && result.get() == ButtonType.OK) {

            ArrayList<Track> toRemove = new ArrayList<>(selectedItems);

            // Rimuovi gli elementi dalla lista osservabile e dalla tracklist
            trackListObservable.removeAll(toRemove);
            trackList.getTracks().removeAll(toRemove);
        }
    }

    public void closeApp(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        // Termina l'intero programma
        System.exit(0);



    }

}