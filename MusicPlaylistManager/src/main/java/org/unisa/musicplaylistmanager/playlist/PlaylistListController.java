package org.unisa.musicplaylistmanager.playlist;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.command.BasePlaylistCommands;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.command.DeletePlaylistCommand;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.service.navigation.NavigationManager;
import org.unisa.musicplaylistmanager.service.statistics.RankingService;

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
public class  PlaylistListController {

    // DEFINIZIONE OGGETTI JAVAFX
    @FXML
    private Button deleteButton;
    @FXML
    private ListView<Playlist> listView;
    @FXML
    private HBox topPlaylistContainer;
    @FXML
    private Button undoButton;
    @FXML
    private Label topPlaylistTitle;

    // definizione attributi

    // root per le view
    private String resourceRoot = "/org/unisa/musicplaylistmanager/playlist/";

    // lista osservabile di playlist
    private ObservableList<Playlist> playlistListObservable;

    // lista di playlist
    private PlaylistList playlistList;

    // riferimento al CommandInvoker
    private CommandInvoker commandInvoker;

    // riferimento al servizio di Ranking
    private RankingService<Playlist> PlaylistRankingService;

    // limite di oggetti da rankare
    private int RANKING_LIMIT = 3;

    //METODI
    /**
     * Apre la schermata di dettaglio per la playlist specificata.
     * Naviga verso la vista della playlist utilizzando il {@link NavigationManager}.
     * * @param p la playlist da visualizzare
     *
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
     *
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Recupera o inizializza il Singleton delle playlist, imposta la lista 
     * osservabile e configura le funzionalità della ListView.
     *
     */
    @FXML
    public void initialize(){
         playlistList = PlaylistList.getPlaylistListPointer();

         // ottiene il riferimento al commandInvoker
         commandInvoker = CommandInvoker.getCommandInvokerPointer();

         // disattiva inizialmente il bottone di undo
        undoButton.disableProperty().bind(commandInvoker.hasCommandsToUndoProperty().not());

        // wrappa come lista osservabile la lista da mostrare nella UI
        playlistListObservable = FXCollections.observableArrayList(playlistList.getPlaylists());

        // definisce la UI come una lista di CellView
        listView.setCellFactory(param -> new PlaylistCellController());

        // definisce gli elementi da visualizzare sulla UI
        listView.setItems(playlistListObservable);

        // abilita la selezione multipla
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // disabilita inizialmente il bottone di eliminazione
        deleteButton.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));


        // definisce evento al doppio click
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

        // imposta il titolo in base al limite
        topPlaylistTitle.setText("La tua Top " + RANKING_LIMIT);

        // inizializzo il ranking
        PlaylistRankingService = new RankingService<>(playlistListObservable, RANKING_LIMIT);

        // Ascolta i cambiamenti
        PlaylistRankingService.getTopItems().addListener((ListChangeListener.Change<? extends Playlist> c) -> {
            Platform.runLater(() -> refreshTopTracksUI(PlaylistRankingService.getTopItems()));
        });

        refreshTopTracksUI(PlaylistRankingService.getTopItems());
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
    void undo(ActionEvent event){
        commandInvoker.undoCommand();
        playlistListObservable.setAll(playlistList.getPlaylists());
        // ricarico gli elementi visuali
        listView.refresh();
    }

    private void refreshTopTracksUI(java.util.List<Playlist> top) {
        topPlaylistContainer.getChildren().clear();

        if (top.isEmpty()) {
            Label emptyLabel = new Label("Ascolta qualche brano per popolare la tua Top " + RANKING_LIMIT + "!");
            emptyLabel.getStyleClass().add("top-track-empty-label");
            topPlaylistContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int i = 0; i < top.size(); i++) {
            Playlist p = top.get(i);

            VBox card = new VBox();
            card.setSpacing(2);
            card.setMaxWidth(Double.MAX_VALUE); // Permette alla card di allargarsi
            HBox.setHgrow(card, Priority.ALWAYS); // Fa espandere la card per riempire lo spazio
            card.getStyleClass().add("top-track-card");

            String rankClass;
            if (i == 0) rankClass = "top-track-rank-gold";
            else if (i == 1) rankClass = "top-track-rank-silver";
            else if (i == 2) rankClass = "top-track-rank-bronze";
            else rankClass = "top-track-rank-normal";

            HBox topRow = new HBox(5);
            topRow.setAlignment(Pos.CENTER_LEFT);

            Label rankLabel = new Label((i + 1) + "°");
            rankLabel.getStyleClass().add(rankClass);

            Label titleLabel = new Label(p.getName());
            titleLabel.getStyleClass().add("top-track-title");

            topRow.getChildren().addAll(rankLabel, titleLabel);

            HBox bottomRow = new HBox(5);
            bottomRow.setAlignment(Pos.CENTER_LEFT);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            String playText = (p.getNumOfPlay() == 1) ? "1 ascolto" : p.getNumOfPlay() + " ascolti";
            Label playsLabel = new Label(playText);
            playsLabel.getStyleClass().add("top-track-plays");

            bottomRow.getChildren().addAll(playsLabel);

            card.getChildren().addAll(topRow, bottomRow);
            topPlaylistContainer.getChildren().add(card);
        }
    }

}