package org.unisa.musicplaylistmanager.playlist;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.command.BasePlaylistCommands;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.command.DeletePlaylistCommand;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.service.navigation.NavigationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller per la schermata principale di visualizzazione di tutte le playlist.
 * Gestisce l'elenco delle playlist, l'apertura di una playlist,
 * l'aggiunta di nuove playlist e l'eliminazione di quelle esistenti.
 *
 * @author gruppo10
 */
public class PlaylistListController {

    //Dichiarazione attributi
    @FXML
    private Button button1;

    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button tracksButton;

    @FXML
    private ListView<Playlist> listView;

    @FXML
    private StackPane mainStackPane;

    @FXML
    private Button undoButton;

    private String resourceRoot = "/org/unisa/musicplaylistmanager/playlist/";
    private ObservableList<Playlist> playlistListObservable;
    private PlaylistList playlistList;
    private CommandInvoker commandInvoker;

    //METODI

    /**
     * Apre la schermata di dettaglio per la playlist specificata.
     * Naviga verso la vista della playlist utilizzando il {@link NavigationManager}.
     * * @param p la playlist da visualizzare
     * @throws IOException se il caricamento del file FXML fallisce
     */
    private void openPlaylist(Playlist p) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistView.fxml"));
        Parent root = loader.load();

        PlaylistController controller = loader.getController();
        controller.setPlaylist(p);
        controller.setPlaylistValidator(() -> playlistList.getPlaylists().contains(p));

        // Naviga usando NavigationManager
        NavigationManager.getInstance().navigateTo(root);
    }

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Recupera o inizializza il Singleton delle playlist, imposta la lista 
     * osservabile e configura le funzionalità della ListView.
     */
    @FXML
    public void initialize(){
         playlistList = PlaylistList.getPlaylistListPointer();
         commandInvoker = CommandInvoker.getCommandInvokerPointer();
         undoButton.disableProperty().bind(commandInvoker.hasCommandsToUndoProperty().not());


        playlistListObservable = FXCollections.observableArrayList(playlistList.getPlaylists());

        listView.setCellFactory(param -> new PlaylistCellController());

        listView.setItems(playlistListObservable);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deleteButton.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));


        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Apri la playlist con doppio click
                Playlist selected = listView.getSelectionModel().getSelectedItem();
                if (selected == null) return;
                try {
                    openPlaylist(selected);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Gestisce l'apertura della finestra per la scelta sulla creazione automatica o manuale di una nuova playlist.
     * * @param event l'evento generato dal click
     * @throws IOException se il caricamento del file FXML fallisce
     */
    @FXML
    void addNewPlaylist(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "PlaylistChooseView.fxml"));
        Parent root = loader.load();

        PlaylistChooseController controller = loader.getController();
        controller.setPlaylistList(playlistList);
        controller.setObservable(playlistListObservable);

        Stage stage = new Stage();
        stage.setTitle("Modalità di creazione playlist");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Elimina le playlist selezionate dall'utente.
     * Mostra una finestra di conferma prima di procedere. Se la playlist
     * attualmente in riproduzione viene eliminata, provvede a chiudere il player.
     * * @param event l'evento generato dal click
     */
    @FXML
    void deletePlaylist(ActionEvent event) {
        ObservableList<Playlist> selectedItems = listView.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");

        // cambia il messaggio in base al numero di elementi selezionati
        if (selectedItems.size() == 1) {
            alert.setHeaderText("Sei sicuro di voler eliminare la playlist selezionata?");
            alert.setContentText("L'azione è irreversibile.");
        } else {
            alert.setHeaderText("Sei sicuro di voler eliminare le " + selectedItems.size() + " playlist selezionate?");
            alert.setContentText("L'azione è irreversibile.");
        }

        // Mostra l'alert e attendi la risposta dell'utente
        Optional<ButtonType> result = alert.showAndWait();

        // Se l'utente ha cliccato "OK"
        if (result.isPresent() && result.get() == ButtonType.OK) {

            ArrayList<Playlist> toRemove = new ArrayList<>(selectedItems);


            // Rimuovi gli elementi dalla lista osservabile e dalla tracklist
            BasePlaylistCommands command= new DeletePlaylistCommand(toRemove ,playlistList, playlistListObservable);
            CommandInvoker.getCommandInvokerPointer().setCommand(command);


            // Se stiamo eliminando la playlist in riproduzione, chiudi il player
            // Ottengo l'identificatore della TrackCollection presa in considerazione
            String identifier = ActivePlayerManager.getInstance().getCurrentPlaylistIdentifier();
            if (identifier != null && toRemove.stream().anyMatch(p -> p.getName().equals(identifier))) {
                ActivePlayerManager.getInstance().closePlayer();
            }
        }
    }

    /**
     * Naviga alla schermata principale della libreria musicale (TrackListView).
     * * @param actionEvent l'evento generato dal click
     * @throws IOException se il caricamento del file FXML fallisce
     */
    @FXML
    public void goTrackList(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/unisa/musicplaylistmanager/track/TrackListView.fxml"));
        Parent playlistParent = loader.load();

        // Cambio contenuto mantenendo il player
        NavigationManager.getInstance().navigateTo(playlistParent);
    }

    /**
     * Chiude l'applicazione terminando l'interfaccia JavaFX e l'intero processo.
     * * @param actionEvent l'evento generato dal click
     */
    @FXML
    public void closeApp(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        // Termina l'intero programma
        System.exit(0);

    }

    /**
     * Annulla l'ultima operazione effettuata.
     *
     * @param event l'evento generato dal click.
     */
    @FXML
    public void undo(ActionEvent event){
        commandInvoker.undoCommand();
        playlistListObservable.setAll(playlistList.getPlaylists());
        // ricarico gli elementi visuali
        listView.refresh();
    }

}