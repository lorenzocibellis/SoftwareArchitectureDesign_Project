package org.unisa.musicplaylistmanager.track;

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
import org.unisa.musicplaylistmanager.player.PlayerController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author gruppo10
 */
public class TrackListController {

    //DEFINIZIONE OGGETTI JAVAFX
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

    //Definizione attributi
    //Path per accedere agli oggetti View.fxml
    private String resourceRoot = "/org/unisa/musicplaylistmanager/";

    //struttura dati per implementare la lista sulla UI
    private ObservableList<Track> trackListObservable;
    //Struttura dati per memorizzazione tracce
    private TrackList trackList;

    //METODI
    //METODI FXML

    //Inizializzatore
    @FXML
    public void initialize() {

        //Controlla se la TrackList non è stata già inizializzata
        //Se è la prima volra la inizializza
        if (!TrackList.exists()) trackList = new TrackList();
        //altrimenti ottiene il puntatore alla TrackList già creata
        else trackList = TrackList.getTrackListPointer();

        //inizializzazione della struttura dati osservabile
        trackListObservable = FXCollections.observableArrayList(trackList.getTracks());

        // fa in modo che la list view usi la cella personalizzata
        listView.setCellFactory(param -> new TrackCellController(this::showTrackDetails));

        //wrapping della struttura dati osservabile in una lista della UI
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

    //passaggio alla lista di Playlist
    @FXML
    void goPlaylist(ActionEvent event) throws IOException {

        //caricamento della View
        Parent playlistParent = FXMLLoader.load(getClass().getResource(resourceRoot + "PlaylistListView.fxml"));

        // creazione della nuova scena
        Scene playlistScene = new Scene(playlistParent);

        // ottienimento della finestra corrente dall'evento
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // cambio scena
        window.setScene(playlistScene);
        window.show();
    }

    // Dichiarazione metodi pubblici

    // apertura della finestra di aggiunta di una traccia alla TrackList
    public void addNewTrack(ActionEvent actionEvent) throws IOException {

        // caricamento della View e della finestra in cui verranno immessi i dati di input
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "TrackView.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Aggiungi Traccia");
        Scene scene = new Scene(root);

        // ottenimento controller della finestra
        TrackController controller = loader.getController();

        // settaggio della lista di memorizzazione e di visualizzazione delle tracce
        controller.setTrackList(trackList);
        controller.setObservable(trackListObservable);

        // visualizzazione finestra
        stage.setScene(scene);
        stage.show();
    }

    // eliminazione di una traccia
    public void deleteTrack(ActionEvent actionEvent) {

        // ottenimento tracce selezionate
        ObservableList<Track> selectedItems = listView.getSelectionModel().getSelectedItems();

        // return se non sono state selezionate le tracce (missfire)
        if (selectedItems.isEmpty()) {
            return;
        }

        // apertura finestra di Alert per accertarsi dell'eliminazione
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

        // controlla se l'utente ha cliccato "OK"
        if (result.isPresent() && result.get() == ButtonType.OK) {

            // crea la lista di tracce da rimuovere
            ArrayList<Track> toRemove = new ArrayList<>(selectedItems);

            // Rimuovi gli elementi dalla lista osservabile e dalla tracklist
            trackListObservable.removeAll(toRemove);
            trackList.getTracks().removeAll(toRemove);
        }
    }

    // chiusura dell'applicazione
    public void closeApp(ActionEvent actionEvent) {

        // ottiene la finestra in cui è stato clickato il bottone e la chiude
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        // Termina l'intero programma
        System.exit(0);

    }

    // Metodi utilitari
    // apertura del player
    private void openPlayerFor(Track selected) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlayerView.fxml"));
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

    // Metodo chiamato quando viene cliccato il bottone "i" in una riga
    // permette di mostrare le info di una traccia
    private void showTrackDetails(Track track) {
        try {

            // caricamento della View
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "TrackView.fxml"));
            Parent root = loader.load();

            // ottenimento del controller
            TrackController controller = loader.getController();
            // Passa la traccia e imposta la modalità di sola lettura
            controller.setTrackDetails(track);
            // Passa anche la lista osservabile per permettere l'aggiornamento della lista nella UI
            // se viene modificata una traccia
            controller.setObservable(trackListObservable);

            // settaggio della lista di tracce in cui memorizzare effettivamente i dati della traccia
            controller.setTrackList(trackList);


            // caricamento della finestra
            Stage stage = new Stage();
            stage.setTitle("Dettagli Traccia");
            stage.initModality(Modality.APPLICATION_MODAL); // Blocca l'interazione con la finestra principale
            stage.setScene(new Scene(root));
            // apertura della finestra
            stage.showAndWait();

        } catch (IOException e) { //catch di eventuali eccezioni e print dello stack
            e.printStackTrace();
        }
    }

}