package org.unisa.musicplaylistmanager.track;

import java.io.BufferedReader;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.command.BaseTrackCommands;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.command.RemoveTrackCommand;
import org.unisa.musicplaylistmanager.player.PlayerController;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.service.navigation.NavigationManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import javafx.geometry.Insets;

/**
 * Controller per la schermata principale della libreria musicale (TrackListView).
 * Gestisce la visualizzazione di tutte le tracce presenti nel sistema,
 * permettendone l'apertura nel player, l'aggiunta di nuove tracce, l'eliminazione
 * e la navigazione verso la schermata delle playlist.
 *
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
    @FXML
    private Button undoButton;

    //Definizione attributi
    //Path per accedere agli oggetti View.fxml
    private String resourceRoot = "/org/unisa/musicplaylistmanager/track/";

    //struttura dati per implementare la lista sulla UI
    private ObservableList<Track> trackListObservable;

    private TrackList trackList;

    // Riferimenti forti ai listener per prevenire la garbage collection precoce quando usiamo WeakChangeListener
    private ChangeListener<Boolean> playerActiveListener;
    private ChangeListener<Track> currentTrackListener;

    private CommandInvoker commandInvoker;

    //METODI
    //METODI FXML

    /**
     * Metodo chiamato automaticamente da JavaFX dopo il caricamento del file FXML.
     * Inizializza il Singleton di {@link TrackList} (se non già presente),
     * configura la {@link ListView} per utilizzare le celle personalizzate,
     * imposta la selezione multipla e gestisce l'evento di doppio click
     * per l'apertura del player.
     */
    @FXML
    public void initialize() {
        // Ottieni l'istanza (Singleton) in modo sicuro
        trackList = TrackList.getTrackListPointer();

        // Carichiamo le tracce dal CSV se la lista è vuota
        if (trackList.getTracks().isEmpty()) {
            loadMockTracksFromCSV();
        }

        // Inizializza l'ObservableList con i dati esistenti
        trackListObservable = FXCollections.observableArrayList(trackList.getTracks());
        commandInvoker = CommandInvoker.getCommandInvokerPointer();

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

        // Ascolta le variazioni dello stato del player (apertura/chiusura) per aggiornare dinamicamente il padding inferiore della ListView.
        // Utilizziamo un WeakChangeListener associato a un riferimento forte di istanza per evitare memory leak del controller.
        playerActiveListener = (obs, oldVal, newVal) -> updateBottomPadding();
        ActivePlayerManager.getInstance().playerActiveProperty().addListener(
                new WeakChangeListener<>(playerActiveListener)
        );
        updateBottomPadding();

        // Aggiunge un ascoltatore (listener) sulla proprietà della traccia corrente dell'ActivePlayerManager.
        currentTrackListener = (obs, oldTrack, newTrack) -> listView.refresh();
        ActivePlayerManager.getInstance().currentTrackProperty().addListener(
                new WeakChangeListener<>(currentTrackListener)
        );
    }

    /**
     * Naviga verso la schermata delle Playlist (PlaylistListView).
     *
     * @param event l'evento generato dal click
     * @throws IOException se il caricamento del file FXML fallisce
     */
    @FXML
    void goPlaylist(ActionEvent event) throws IOException {

        //caricamento della View
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/unisa/musicplaylistmanager/playlist/PlaylistListView.fxml"));
        Parent playlistParent = loader.load();

        // cambio contenuto mantenendo il player
        NavigationManager.getInstance().navigateTo(playlistParent);
    }

    /**
     * Naviga annulla l'ultima operazione effettuata.
     *
     * @param event l'evento generato dal click
     *
     */
    @FXML
    void undo(ActionEvent event){
        CommandInvoker.getCommandInvokerPointer().undoCommand();
        trackListObservable.setAll(trackList.getTracks());
        listView.refresh();
    }

    // Dichiarazione metodi pubblici

    /**
     * Apre la finestra per l'aggiunta di una nuova traccia alla libreria.
     *
     * @param actionEvent l'evento generato dal click
     * @throws IOException se il caricamento del file FXML fallisce
     */
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

    /**
     * Gestisce l'eliminazione delle tracce selezionate dall'utente.
     * Mostra un avviso di conferma. Se la traccia attualmente in riproduzione
     * viene eliminata, provvede a chiudere il player.
     *
     * @param actionEvent l'evento generato dal click
     */
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

            // Memorizziamo lo stato del player PRIMA di distruggere i dati
            Track playingTrack = ActivePlayerManager.getInstance().getCurrentTrack();
/*
            // Aggiorniamo l'interfaccia visiva
            trackListObservable.removeAll(toRemove);
            // La TrackList si occuperà in automatico di rimuovere i dati e avvisare gli observer
            trackList.removeAllTracks(toRemove);
 */
            BaseTrackCommands command = new RemoveTrackCommand(toRemove, trackList, trackListObservable);
            CommandInvoker.getCommandInvokerPointer().setCommand(command);

            // 4. Se stavamo eliminando la traccia in riproduzione, chiudiamo il player
            if (playingTrack != null && toRemove.contains(playingTrack)) {
                ActivePlayerManager.getInstance().closePlayer();
            }
        }
    }

    /**
     * Chiude l'applicazione terminando l'interfaccia JavaFX e il processo di sistema.
     *
     * @param actionEvent l'evento generato dal click
     */
    public void closeApp(ActionEvent actionEvent) {

        // ottiene la finestra in cui è stato clickato il bottone e la chiude
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        // Termina l'intero programma
        System.exit(0);

    }

    // Metodi utilitari
    /**
     * Apre il player per riprodurre la traccia passata come parametro,
     * specificando la TrackList principale come contesto di riproduzione.
     *
     * @param selected la traccia da riprodurre
     */
    private void openPlayerFor(Track selected) {

    ActivePlayerManager.getInstance().openPlayer(selected, trackList);

    updateBottomPadding();
}

    /**
     * Mostra i dettagli della traccia selezionata in una finestra di sola lettura.
     * Viene chiamato quando l'utente clicca sul bottone "info" (i) nella riga.
     *
     * @param track la traccia di cui mostrare i dettagli
     */
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
    
    private void loadMockTracksFromCSV() {
        String resourcePath = "/data/tracks.csv";
        InputStream is = getClass().getResourceAsStream(resourcePath);

        if (is == null) {
            System.err.println("Errore: impossibile trovare il file " + resourcePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            
            // Salta la prima riga di intestazione
            br.readLine(); 
            
            while ((line = br.readLine()) != null) {
                // Ignora le righe vuote
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(";");
                
                if (data.length >= 8) {
                    // Estrazione e conversione dei dati
                    String title = data[0].trim();
                    String author = data[1].trim();
                    String genre = data[2].trim();
                    java.time.Year year = java.time.Year.of(Integer.parseInt(data[3].trim()));
                    int duration = Integer.parseInt(data[4].trim());
                    boolean favourite = Boolean.parseBoolean(data[5].trim());
                    boolean explicit = Boolean.parseBoolean(data[6].trim());
                    boolean newRelease = Boolean.parseBoolean(data[7].trim());
                    
                    // Creazione della traccia
                    Track t = new Track(title, author, year, genre, duration, favourite, explicit, newRelease);
                    
                    // Aggiunta effettiva alla TrackList!
                    trackList.getTracks().add(t); 
                }
            }
            System.out.println("Tracce caricate e aggiunte alla lista con successo!");
            
        } catch (Exception e) { 
            // Uso Exception generica per catturare anche eventuali errori di conversione numeri (NumberFormatException)
            System.err.println("C'è un errore in una riga del file CSV: " + e.getMessage());
            e.printStackTrace();
        }
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

}