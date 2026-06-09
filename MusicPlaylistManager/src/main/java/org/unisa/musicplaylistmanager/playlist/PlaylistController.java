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
import org.unisa.musicplaylistmanager.command.AddTrackCommand;
import org.unisa.musicplaylistmanager.command.BaseTrackCommands;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.command.RemoveTrackCommand;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.service.navigation.NavigationManager;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackCellController;
import org.unisa.musicplaylistmanager.track.TrackController;
import org.unisa.musicplaylistmanager.track.TrackList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import java.util.function.BooleanSupplier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.geometry.Insets;

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
    private Button playAllButton;

    @FXML
    private Button shuffleAllButton;

    @FXML
    private Button button;

    @FXML
    private Button undoButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ListView<Track> listView;

    @FXML
    private StackPane mainStackPane;

    @FXML
    private Label namePlaylist;

    private String resourceRoot = "/org/unisa/musicplaylistmanager/playlist/";
    private ObservableList<Track> playlistObservable;
    private Playlist playlist;
    private BooleanSupplier playlistValidator;


    private ChangeListener<Boolean> playerActiveListener;
    private ChangeListener<Track> currentTrackListener;

    private CommandInvoker commandInvoker;

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

        commandInvoker = CommandInvoker.getCommandInvokerPointer();
        undoButton.disableProperty().bind(commandInvoker.hasCommandsToUndoProperty().not());
        
        // Ascolta le variazioni dello stato del player (apertura/chiusura) per aggiornare dinamicamente il padding inferiore della ListView.
        // Utilizziamo un WeakChangeListener associato a un riferimento forte di istanza per evitare memory leak del controller.
        playerActiveListener = (obs, oldVal, newVal) -> updateBottomPadding();
        ActivePlayerManager.getInstance().playerActiveProperty().addListener(
                new WeakChangeListener<>(playerActiveListener)
        );
        updateBottomPadding();

        // Aggiunge un listener sulla proprietà della traccia corrente dell'ActivePlayerManager.
        currentTrackListener = (obs, oldTrack, newTrack) -> listView.refresh();
        ActivePlayerManager.getInstance().currentTrackProperty().addListener(
                new WeakChangeListener<>(currentTrackListener)
        );
    }

    /**
     * Imposta la playlist corrente da visualizzare nel controller.
     * Inizializza la lista osservabile, imposta la cella personalizzata per le tracce
     * e configura gli eventi della ListView (es. avvio riproduzione al doppio click).
     * * @param p la playlist da visualizzare
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
        updateBottomPadding();
    }

    /**
     * Apre il player musicale avviando la traccia selezionata all'interno del
     * contesto di questa playlist.
     * * @param selected la traccia da riprodurre
     */
    private void openPlayerFor(Track selected) {
        ActivePlayerManager.getInstance().openPlayer(selected, playlist);
        updateBottomPadding();
    }

    /**
     * Mostra la finestra di dialogo con i dettagli della traccia selezionata.
     * * @param track la traccia di cui mostrare i dettagli
     */
    private void showTrackDetails(Track track) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/unisa/musicplaylistmanager/track/TrackView.fxml"));
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
     * * @param event l'evento ActionEvent generato dal click
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "AddTracksToPlaylistView.fxml"));
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
                ArrayList<Track> selected = controller.getSelectedTracks();

                // Pattern command
                BaseTrackCommands command = new AddTrackCommand(selected, playlist ,playlistObservable);
                CommandInvoker.getCommandInvokerPointer().setCommand(command);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Rimuove le tracce selezionate dalla playlist corrente, previa conferma.
     * Se la traccia attualmente in riproduzione viene rimossa da questa playlist,
     * il player verrà chiuso automaticamente.
     * * @param event l'evento ActionEvent generato dal click
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
            
            // Memorizza lo stato del player PRIMA di rimuovere i dati, 

            Track playingTrack = ActivePlayerManager.getInstance().getCurrentTrack();
            String identifier = ActivePlayerManager.getInstance().getCurrentPlaylistIdentifier();

            // Pattern Command
            BaseTrackCommands command = new RemoveTrackCommand(toRemove, playlist ,playlistObservable);
            CommandInvoker.getCommandInvokerPointer().setCommand(command);

            if (playingTrack != null && toRemove.contains(playingTrack) && playlist.equals(new Playlist(identifier))) {
                ActivePlayerManager.getInstance().closePlayer();
            }
        }
    }

    /**
     * Torna alla schermata precedente (PlaylistListView).
     * * @param actionEvent l'evento ActionEvent generato dal click
     * @throws IOException se il caricamento del file FXML fallisce
     */
    @FXML
    void goBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistListView.fxml"));
        Parent playlistParent = loader.load();

        // Naviga usando NavigationManager
        NavigationManager.getInstance().navigateTo(playlistParent);
    }
    /**
     * Aggiorna il padding inferiore della ListView per evitare che gli ultimi brani 
     * in lista vengano coperti visivamente dalla barra sovrapposta del mini-player.
     * Applica un padding di 130px se il player è attivo, altrimenti lo azzera.
     */
    private void updateBottomPadding() {
        double padding = ActivePlayerManager.getInstance().getPlayerHeight();
        listView.setPadding(new Insets(0, 0, padding, 0));
    }

    @FXML
    public void handlePlayAll() {
        if (playlist == null || playlist.getTracks().isEmpty()) {
            return;
        }
        // Avvia la prima traccia
        openPlayerFor(playlist.getTracks().get(0));
    }

    @FXML
    public void handleShuffleAll() {
        if (playlist == null || playlist.getTracks().isEmpty()) {
            return;
        }
        // Scegliamo una traccia casuale per iniziare
        int randomIndex = (int) (Math.random() * playlist.getTracks().size());
        openPlayerFor(playlist.getTracks().get(randomIndex));
        
        // Attiva lo shuffle sul player appena aperto tramite il Manager
        ActivePlayerManager.getInstance().toggleShuffle();
    }

    /**
     * Annulla l'ultima operazione effettuata.
     * Se mi trovo all'interno di un playlist la cui creazione è annullata, esco dalla vista.
     *
     * @param event l'evento generato dal click.
     */
    @FXML
    public void undo(ActionEvent event) throws IOException {
        commandInvoker.undoCommand();
        playlistObservable.setAll(playlist.getTracks());
        listView.refresh();

        // controllo che esista il validatore e che coincida con la playlist attuale
        if(playlistValidator != null && !playlistValidator.getAsBoolean()){
            // se entrambe le condizioni sono vere, esco dalla vista della playlist
            goBack(event);
        }
    }

    /**
     *
     * Permette di settare un metodo per sapere se la playlist è ancora presente nella lista delle playlist.
     *
     * @param validator Parametro che permette di ottenere un risultato Booleano (può essere una funzione lambda).
     *
     */
    public void setPlaylistValidator(BooleanSupplier validator){
        this.playlistValidator = validator;
    }
}