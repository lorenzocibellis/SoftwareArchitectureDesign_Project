package org.unisa.musicplaylistmanager.track.list.tracklist;

import java.io.BufferedReader;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.service.alert.AlertManager;
import org.unisa.musicplaylistmanager.command.BaseTrackCommands;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.command.RemoveTrackCommand;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.service.navigation.NavigationManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.unisa.musicplaylistmanager.service.statistics.RankingService;
import org.unisa.musicplaylistmanager.tag.TagManagerController;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackCellController;
import org.unisa.musicplaylistmanager.track.TrackController;

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
    private ListView<Track> listView;
    @FXML
    private Button deleteButton;
    @FXML
    private Button undoButton;
    @FXML
    private Label libraryName;
    @FXML
    private Label topTracksTitle;
    @FXML
    private HBox topTracksContainer;

    //Definizione attributi
    private static final int RANKING_LIMIT = 3;
    //Path per accedere agli oggetti View.fxml
    private String resourceRoot = "/org/unisa/musicplaylistmanager/track/";

    //struttura dati per implementare la lista sulla UI
    private ObservableList<Track> trackListObservable;

    private TrackList trackList;

    // Riferimento forte al listener per prevenire la garbage collection precoce quando usiamo WeakChangeListener
    private ChangeListener<Boolean> playerActiveListener;

    // Riferimento al CommandInvoker
    private CommandInvoker commandInvoker;

    /**
     * Metodo chiamato automaticamente da JavaFX dopo il caricamento del file FXML.
     * Inizializza il Singleton di {@link TrackList} (se non già presente),
     * configura la {@link ListView} per utilizzare le celle personalizzate,
     * imposta la selezione multipla e gestisce l'evento di doppio click
     * per l'apertura del player.
     */

    @FXML
    public void initialize() {

        // ottenimento puntatore alla trackList
        trackList = TrackList.getTrackListPointer();

        libraryName.setText(trackList.getName());

        // caricamento tracce
        if (trackList.getTracks().isEmpty()) {
            loadMockTracksFromCSV();
        }

        //  creazione lista osservabile di tracce
        trackListObservable = FXCollections.observableArrayList(trackList.getTracks());

        // ottenimento puntatore al CommandInvoker
        commandInvoker = CommandInvoker.getCommandInvokerPointer();

        // disabilitazione bottone Undo
        undoButton.disableProperty().bind(commandInvoker.hasCommandsToUndoProperty().not());

        // popolamento lista osservabile dalla View
        listView.setCellFactory(param -> new TrackCellController(this::showTrackDetails, this::moveUp, this::moveDown));
        listView.setItems(trackListObservable);

        // abilitazione selezione multipla di elementi
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // disabilitazione pulsante di eliminazione
        deleteButton.disableProperty().bind(
            Bindings.isEmpty(listView.getSelectionModel().getSelectedItems())
        );

        // definizione comportamento su doppio click
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Track selected = listView.getSelectionModel().getSelectedItem();
                if (selected == null) return;
                openPlayerFor(selected);
            }
        });

        // definizione comportamento del player
        playerActiveListener = (obs, oldVal, newVal) -> updateBottomPadding();
        ActivePlayerManager.getInstance().playerActiveProperty().addListener(
                new WeakChangeListener<>(playerActiveListener)
        );

        updateBottomPadding();

        // imposta il titolo in base al limite
        topTracksTitle.setText("La tua Top " + RANKING_LIMIT);

        // inizializza il RankingService per gestire la classifica
        RankingService<Track> trackRankingService =
            new RankingService<>(trackListObservable, RANKING_LIMIT);

        // Ascolta i cambiamenti
        trackRankingService.getTopItems().addListener((ListChangeListener.Change<? extends Track> c) -> {
            Platform.runLater(() -> refreshTopTracksUI(trackRankingService.getTopItems()));
        });

        // forza refresh della top
        refreshTopTracksUI(trackRankingService.getTopItems());
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
     * Annulla l'ultima operazione effettuata.
     *
     * @param event l'evento generato dal click.
     *
     */
    @FXML
    void undo(ActionEvent event){
        commandInvoker.undoCommand();
        trackListObservable.setAll(trackList.getTracks());
        listView.refresh();

        // Deleghiamo al manager la verifica globale dello stato del player (es. se la playlist è stata eliminata o la traccia rimossa)
        ActivePlayerManager.getInstance().validatePlayerState();
    }

    // Dichiarazione metodi pubblici

    /**
     * Apre la finestra di dialogo per aggiungere un nuovo tag personale.
     */
    @FXML
    public void addPersonalTag(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/unisa/musicplaylistmanager/tag/TagManagerView.fxml"));
            Parent root = loader.load();
            
            // fa in modo che la lista delle canzoni venga aggiornata graficamnete in tempo reale
            TagManagerController controller = loader.getController();
            controller.setOnTagDeleted(() -> listView.refresh());

            Stage stage = new Stage();
            stage.setTitle("Gestione Tag Personali");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // forza il ricaricamento grafico della lista tracce
            // per far sparire eventuali tag appena rimossi
            listView.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
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
        String title = "Conferma Eliminazione";
        String content = "L'azione è irreversibile";
        String header;

        // cambia il messaggio in base al numero di elementi selezionati
        if (selectedItems.size() == 1) {
            header = "Sei sicuro di voler eliminare la traccia selezionata?";
        } else {
            header = "Sei sicuro di voler eliminare le " + selectedItems.size() + " tracce selezionate?";
        }

        // Mostra l'alert e attendi la risposta dell'utente
        boolean result = AlertManager.showConfirmation(title,header,content);

        // controlla se l'utente ha cliccato "OK"
        if (result) {

            // crea la lista di tracce da rimuovere
            ArrayList<Track> toRemove = new ArrayList<>(selectedItems);

            // Memorizziamo lo stato del player PRIMA di distruggere i dati
            Track playingTrack = ActivePlayerManager.getInstance().getCurrentTrack();


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

    /**
     *
     * Permette di spostare di una posizione più in alto una traccia, aggiornando al contempo
     * la lista osservabile.
     *
     * @param track traccia da spostare.
     *
     */
    private void moveUp(Track track){
        int i = trackList.getIndex(track);
        if (i > 0){
            Collections.swap(trackListObservable, i, i-1);
            trackList.swap(i, i-1);
            listView.refresh();
        }
    }

    /**
     *
     * Permette di spostare di una posizione più in basso una traccia, aggiornando al contempo
     * la lista osservabile.
     *
     * @param track traccia da spostare.
     *
     */
    private void moveDown(Track track){
        int i = trackList.getIndex(track);
        if (i < trackList.getSize() - 1) {
            Collections.swap(trackListObservable, i, i+1);
            trackList.swap(i,i+1);
            listView.refresh();
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
                    
                    // estrazione dei tag personali (se presenti nella nona colonna separati da virgola)
                    if (data.length >= 9 && !data[8].trim().isEmpty()) {
                        String[] tags = data[8].split(",");
                        for (String tag : tags) {
                            String trimmedTag = tag.trim();
                            t.addPersonalTag(trimmedTag);
                            org.unisa.musicplaylistmanager.tag.PersonalTagManager.getInstance().addTag(trimmedTag);
                        }
                    }
                    
                    // Aggiunta effettiva alla TrackList!
                    trackList.addTrack(t);
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
    /**
     * Rigenera l'interfaccia grafica della Top (il podio orizzontale).
     * Viene invocato automaticamente ogni volta che il {@link RankingService}
     * rileva un cambiamento (nuovi ascolti, sorpassi in classifica, eliminazioni).
     * Svuota il contenitore e ricrea le card grafiche aggiornate basandosi sulla lista passata in input.
     * Se la lista è vuota, visualizza un messaggio informativo.
     * 
     * @param top La lista aggiornata delle tracce attualmente sul podio
     */
    private void refreshTopTracksUI(java.util.List<Track> top) {
        // reset della lista
        topTracksContainer.getChildren().clear();

        // controlla se la lista è vuota
        if (top.isEmpty()) {

            // se lo è, mostra un messaggio default
            Label emptyLabel = new Label("Ascolta qualche brano per popolare la tua Top " + RANKING_LIMIT + "!");
            emptyLabel.getStyleClass().add("top-track-empty-label");
            topTracksContainer.getChildren().add(emptyLabel);
            return;
        }

        // per ogni traccia presente nella lista
        for (int i = 0; i < top.size(); i++) {
            Track t = top.get(i);

            // creo una card e la inizializzo con i valori che mi servono
            VBox card = new VBox();
            card.setSpacing(2);
            card.setMaxWidth(Double.MAX_VALUE); // Permette alla card di allargarsi
            HBox.setHgrow(card, javafx.scene.layout.Priority.ALWAYS); // Fa espandere la card per riempire lo spazio
            card.getStyleClass().add("top-track-card");

            // mostra la scritta con un certo valore a seconda della posizione in classifica
            String rankClass;
            if (i == 0) rankClass = "top-track-rank-gold";
            else if (i == 1) rankClass = "top-track-rank-silver";
            else if (i == 2) rankClass = "top-track-rank-bronze";
            else rankClass = "top-track-rank-normal";


            HBox topRow = new HBox(5);
            topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label rankLabel = new Label((i + 1) + "°");
            rankLabel.getStyleClass().add(rankClass);

            Label titleLabel = new Label(t.getTitle());
            titleLabel.getStyleClass().add("top-track-title");
            
            topRow.getChildren().addAll(rankLabel, titleLabel);

            HBox bottomRow = new HBox(5);
            bottomRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label authorLabel = new Label(t.getAuthor());
            authorLabel.getStyleClass().add("top-track-author");
            
            Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            
            String playText = (t.getNumOfPlay() == 1) ? "1 ascolto" : t.getNumOfPlay() + " ascolti";
            Label playsLabel = new Label(playText);
            playsLabel.getStyleClass().add("top-track-plays");
            
            bottomRow.getChildren().addAll(authorLabel, spacer, playsLabel);

            card.getChildren().addAll(topRow, bottomRow);

            // aggiungo la card alla UI
            topTracksContainer.getChildren().add(card);
        }
    }
}