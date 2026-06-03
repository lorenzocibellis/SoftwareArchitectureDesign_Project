package org.unisa.musicplaylistmanager.playlist;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.service.NavigationManager;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackCellController;
import org.unisa.musicplaylistmanager.track.TrackController;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller per la visualizzazione e gestione del contenuto di una singola playlist.
 * Permette di visualizzare le tracce, aggiungerne di nuove dalla libreria, rimuoverle,
 * e avviarne la riproduzione.
 *
 * @author gruppo10
 */
public class PlaylistController {

    @FXML
    private Button backButton;

    @FXML
    private Button button;

    @FXML
    private Button deleteButton;

    @FXML
    private ListView<Track> listView;

    @FXML
    private StackPane mainStackPane;

    @FXML
    private Label namePlaylist;

    private String resourceRoot = "/org/unisa/musicplaylistmanager/";
    private ObservableList<Track> playlistObservable;
    private Playlist playlist;

    //METODI
    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Collega la disabilitazione del bottone di eliminazione alla selezione
     * degli elementi nella ListView.
     */
    @FXML
    void initialize(){
        // Lega la proprietà 'disable' del bottone 'deleteButton' allo stato della selezione della lista.
        // Se non ci sono elementi selezionati (isEmpty() è true), il bottone sarà disabilitato.
        deleteButton.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));
    }

    /**
     * Imposta la playlist corrente da visualizzare nel controller.
     * Inizializza la lista osservabile, imposta la cella personalizzata per le tracce
     * e configura gli eventi della ListView (es. avvio riproduzione al doppio click).
     * 
     * @param p la playlist da visualizzare
     */
    public void setPlaylist(Playlist p){

        if (p != null) {

            playlist = p;
            namePlaylist.setText(playlist.getName());

            playlistObservable = FXCollections.observableArrayList(playlist.getTracks());

            // fa in modo che la list view usi la cella personalizzata
            listView.setCellFactory(param -> new TrackCellController(this::showTrackDetails));

            listView.setItems(playlistObservable);

            //Abilito la selezione multipla di elementi
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


            // Gestione del doppio click su una riga per aprire il player
            listView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Apri il player con un doppio click
                    Track selected = listView.getSelectionModel().getSelectedItem();
                    if (selected == null) return;
                    openPlayerFor(selected);
                }
            });
        }
    }

    /**
     * Apre il player musicale avviando la traccia selezionata all'interno del
     * contesto di questa playlist.
     * 
     * @param selected la traccia da riprodurre
     */
    private void openPlayerFor(Track selected) {
        ActivePlayerManager.getInstance().openPlayer(selected, playlist);
    }

    /**
     * Mostra la finestra di dialogo con i dettagli della traccia selezionata.
     * 
     * @param track la traccia di cui mostrare i dettagli
     */
    private void showTrackDetails(Track track) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "TrackView.fxml"));
            Parent root = loader.load();

            TrackController controller = loader.getController();
            // Passa la traccia e imposta la modalità di sola lettura
            controller.setTrackDetails(track);
            // Passa anche la lista osservabile per permettere l'aggiornamento della lista nella UI
            // se viene modificata una traccia
            controller.setObservable(playlistObservable);

            controller.setTrackList(playlist);

            Stage stage = new Stage();
            stage.setTitle("Dettagli Traccia");
            stage.initModality(Modality.APPLICATION_MODAL); // Blocca l'interazione con la finestra principale
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apre una finestra per aggiungere nuove tracce alla playlist.
     * Mostra solo le tracce della libreria che non sono già presenti nella playlist.
     * 
     * @param event l'evento ActionEvent generato dal click
     */
    @FXML
    void addTrack(ActionEvent event) {
        if (!TrackList.exists()) {
            return;
        }
        // ottengo la lista completa di tutte le tracce presenti nel sistema
        TrackList allTracksList = TrackList.getTrackListPointer();
        List<Track> allTracks = allTracksList.getTracks();
        
        // Filtra le tracce che sono già nella playlist
        List<Track> availableTracks = allTracks.stream()
                .filter(t -> !playlist.getTracks().contains(t))
                .collect(Collectors.toList());


        // Carico AddTracksToPlaylistView.fxml ) e gli passo la lista delle tracce filtrata
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/unisa/musicplaylistmanager/AddTracksToPlaylistView.fxml"));
            Parent root = loader.load();

            AddTracksToPlaylistController controller = loader.getController();
            controller.setAvailableTracks(availableTracks);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi Tracce");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();


            // Quando l'utente seleziona una o più tracce e preme "Conferma" nella nuova finestra, il
            // metodo estrae le tracce selezionate e le
            // aggiunge alla playlist e alla lista osservabile della playlist

            if (controller.isConfirmed()) {
                List<Track> selected = controller.getSelectedTracks();
                for (Track t : selected) {
                    playlist.addTrack(t);
                    playlistObservable.add(t);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Rimuove le tracce selezionate dalla playlist corrente, previa conferma.
     * Se la traccia attualmente in riproduzione viene rimossa da questa playlist,
     * il player verrà chiuso automaticamente.
     * 
     * @param event l'evento ActionEvent generato dal click
     */
    @FXML
    void removeTrack(ActionEvent event) {
        ObservableList<Track> selectedItems = listView.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");

        if (selectedItems.size() == 1) {
            alert.setHeaderText("Sei sicuro di voler rimuovere la traccia selezionata dalla playlist?");
            alert.setContentText("La traccia non sarà eliminata dalla libreria principale.");
        } else {
            alert.setHeaderText("Sei sicuro di voler rimuovere le " + selectedItems.size() + " tracce selezionate dalla playlist?");
            alert.setContentText("Le tracce non saranno eliminate dalla libreria principale.");
        }

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ArrayList<Track> toRemove = new ArrayList<>(selectedItems);
            
            playlistObservable.removeAll(toRemove);
            playlist.getTracks().removeAll(toRemove);

            // Se stiamo rimuovendo la traccia in riproduzione E il player è stato avviato da QUESTA playlist, chiudi il player
            Track playingTrack = ActivePlayerManager.getInstance().getCurrentTrack();
            Playlist playingPlaylist = ActivePlayerManager.getInstance().getCurrentPlaylist();
            if (playingTrack != null && toRemove.contains(playingTrack) && playlist == playingPlaylist) {
                ActivePlayerManager.getInstance().closePlayer();
            }
        }
    }

    /**
     * Torna alla schermata precedente (PlaylistListView).
     * 
     * @param actionEvent l'evento ActionEvent generato dal click
     * @throws IOException se il caricamento del file FXML fallisce
     */
    @FXML
    void goBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistListView.fxml"));
        Parent playlistParent = loader.load();

        // Naviga usando NavigationManager
        NavigationManager.getInstance().navigateTo(playlistParent);
    }

}