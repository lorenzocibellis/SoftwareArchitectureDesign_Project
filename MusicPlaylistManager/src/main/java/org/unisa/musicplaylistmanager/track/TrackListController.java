package org.unisa.musicplaylistmanager.track;

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
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.service.navigation.NavigationManager;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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

    //Definizione attributi
    //Path per accedere agli oggetti View.fxml
    private String resourceRoot = "/org/unisa/musicplaylistmanager/track/";

    //struttura dati per implementare la lista sulla UI
    private ObservableList<Track> trackListObservable;
    //Struttura dati per memorizzazione tracce
    private TrackList trackList;

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

        //Controlla se la TrackList non è stata già inizializzata
        //Se è la prima volra la inizializza
        trackList = TrackList.getTrackListPointer();
        //altrimenti ottiene il puntatore alla TrackList già creata

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

        // chiamata al metodo di prova per aggiungere delle canzoni all'avvio dell'app
        addSampleSongs();


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

            // Pattern Observer: notifica tutte le playlist registrate per rimuovere le tracce
            for (Track t : toRemove) {
                trackList.getSubjectTrackList().notifyObserver(t);
            }

            // Rimuovi gli elementi dalla lista osservabile e dalla tracklist
            trackListObservable.removeAll(toRemove);
            trackList.getTracks().removeAll(toRemove);

            // Se stiamo eliminando la traccia in riproduzione, chiudi il player
            Track playingTrack = ActivePlayerManager.getInstance().getCurrentTrack();
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


    // metodo di prova per aggiungere delle canzoni all'avvio dell'app
    public void addSampleSongs(){

        Track track1 = new Track("La canzone del sole", "Lucio Battisti", Year.of(1971), "Pop", 210, false, false, false);
        Track track2 = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 354, true, false, false);
        Track track3 = new Track("Shape of You", "Ed Sheeran", Year.of(2017), "Pop", 233, false, false, true);
        Track track4 = new Track("Smells Like Teen Spirit", "Nirvana", Year.of(1991), "Grunge", 301, true, true, false);
        Track track5 = new Track("Billie Jean", "Michael Jackson", Year.of(1982), "Pop", 294, true, false, false);
        Track track6 = new Track("Shape of my heart", "Sting", Year.of(1993), "Pop", 258, false, false, false);
        Track track7 = new Track("Demons", "Imagine Dragons", Year.of(2012), "Alternative Rock", 177, true, false, true);
        Track track8 = new Track("Master of puppets", "Metallica", Year.of(1986), "Metal", 515, false, true, false);
        Track track9 = new Track("Cinque giorni", "Michele Zarrillo", Year.of(1990), "Pop", 240, true, false, false);
        Track track10 = new Track("Losing my religion", "R.E.M.", Year.of(1991), "Alternative Rock", 269, false, true, false);


        trackList.getTracks().addAll(Arrays.asList(track1, track2, track3, track4, track5, track6, track7, track8, track9, track10));
        trackListObservable.addAll(trackList.getTracks());
    }









}