package org.unisa.musicplaylistmanager.track;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.playlist.Playlist;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gruppo10
 */
public class TrackController {


    //DEFINIZIONE OGGETTI JAVAFX
    @FXML
    private Button addTrackButton;
    @FXML
    private Button buttonBack;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField titleInput;
    @FXML
    private TextField yearInput;
    @FXML
    private TextField genreInput;
    @FXML
    private TextField authorInput;
    @FXML
    private TextField minutesInput;
    @FXML
    private TextField secondsInput;
    @FXML
    private RadioButton favouriteRadio;
    @FXML
    private RadioButton newReleaseRadio;
    @FXML
    private RadioButton explicitContentRadio;

    // definizione attributi

    // lista di tracce su cui lavorare
    private Playlist trackList;

    //struttura dati della UI da aggiornare
    private ObservableList<Track> observableList;

    // flag utilitario per UI dinamica
    private boolean isReadOnly = false;

    // Riferimento alla traccia che stiamo visualizzando/modificando
    private Track currentTrack;

    //METODI
    //METODI FXML

    //Inizializzatore
    @FXML
    public void initialize() {
        // Listener per forzare l'input a essere solo numerico per i minuti
        minutesInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isReadOnly && !newValue.matches("\\d*")) {
                minutesInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Listener per forzare l'input a essere solo numerico per i secondi
        secondsInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isReadOnly && !newValue.matches("\\d*")) {
                secondsInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Listener per forzare l'input a essere solo numerico e di massimo 4 cifre per l'anno
        yearInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isReadOnly) return;
            String filtered = newValue.replaceAll("[^\\d]", "");
            if (filtered.length() > 4) {
                filtered = filtered.substring(0, 4);
            }
            if (!yearInput.getText().equals(filtered)) {
                yearInput.setText(filtered);
            }
        });

        // gestisci la visibilità dei bottoni
        manageButtonVisibility();
    }

    @FXML
    public void editTrack(ActionEvent event) {
        setEditMode();
    }

    @FXML
    public void cancelChanges(ActionEvent event) {
        // Ripristina i valori originali e torna in modalità Info
        populateFieldsFromTrack(currentTrack);
        setInfoMode();
    }

    // funzione che salva le modifiche apportate alla traccia
    @FXML
    public void saveChanges(ActionEvent event) {
        List<String> errors = inputValidation();

        if (!errors.isEmpty()) {
            String errorContent = String.join("\n", errors);
            showError("Errore di Validazione", "Per favore, correggi i seguenti errori:", errorContent);
            return;
        }

        try {
            // Calcoliamo i valori dai campi di input
            int minutes = Integer.parseInt(minutesInput.getText());
            int seconds = Integer.parseInt(secondsInput.getText());
            int totalSeconds = (minutes * 60) + seconds;
            Year year = Year.of(Integer.parseInt(yearInput.getText()));

            // Creiamo un oggetto traccia temporaneo (usato solo per trasportare i nuovi dati)
            Track updatedData = new Track(
                    titleInput.getText(),
                    authorInput.getText(),
                    year,
                    genreInput.getText(),
                    totalSeconds,
                    favouriteRadio.isSelected(),
                    explicitContentRadio.isSelected(),
                    newReleaseRadio.isSelected()
            );

            // Troviamo l'indice ESATTO della traccia nella ObservableList usando il riferimento di memoria (==)
            // Facciamo questo controllo PRIMA della modifica, così l'identificazione è sicura al 100%
            int index = -1;
            if (observableList != null) {
                for (int i = 0; i < observableList.size(); i++) {
                    if (observableList.get(i) == currentTrack) {
                        index = i;
                        break;
                    }
                }
            }

            // Deleghiamo il controllo dei duplicati e l'aggiornamento dei campi alla logica del Modello.
            // Se i nuovi dati creano un duplicato, Playlist lancerà una IllegalArgumentException
            trackList.updateTrack(currentTrack, updatedData);

            // 5. Se il modello non ha lanciato eccezioni, aggiorniamo la ListView tramite la ObservableList
            if (index != -1) {
                observableList.set(index, currentTrack);
            }

            // Torna alla schermata Info per mostrare le modifiche salvate
            setInfoMode();

        } catch (IllegalArgumentException e) {
            // Cattura l'errore di duplicato lanciato dal modello e lo mostra all'utente
            showError("Modifica non consentita", "Conflitto rilevato tra le tracce", e.getMessage());
        } catch (Exception e) {
            showError("Errore Inaspettato", "Si è verificato un errore durante il salvataggio.", e.getMessage());
        }
    }

    // aggiunta traccia alla TrackList
    @FXML
    public void addTrack(ActionEvent actionEvent) {

        //lista di errori
        List<String> errors = inputValidation();

        //se ci sono errori, stampa un messaggio che li indica e termina l'operazione di aggiunta
        if (!errors.isEmpty()) {
            String errorContent = String.join("\n", errors);
            showError("Errore di Validazione", "Per favore, correggi i seguenti errori:", errorContent);
            return;
        }


        try {
            // crea la traccia dai dati passati in input
            Track track = getTrack();

            //aggiungi la traccia alla lista di tracce
            this.add(track);

            // chiudi il popUp
            goBack(actionEvent);
        } catch (Exception e) { //nel caso di errori durante la creazione e l'aggiunta della traccia
            showError("Errore Inaspettato", "Si è verificato un errore durante la creazione.", e.getMessage());
        }
    }

    // metodo per chiudere il popUp attuale
    @FXML
    public void goBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    // Dichiarazione metodi pubblici

    // settaggio TrackList a cui aggingere la traccia
    public void setTrackList(Playlist tl) {
        trackList = tl;
    }

    // settaggio struttura dati osservabile in cui mostrare la traccia
    public void setObservable(ObservableList<Track> ol) {
        this.observableList = ol;
    }


    /**
     * Metodo chiamato dal TrackListController per mostrare i dettagli in sola lettura.
     */
    public void setTrackDetails(Track track) {

        //ottiene la traccia di cui si stanno visualizzando le informazioni
        this.currentTrack = track;

        // Popola i campi con i dati attuali
        populateFieldsFromTrack(track);

        // Imposta la modalità Info (blocca i campi, mostra Modifica/Chiudi)
        setInfoMode();
    }

    /**
     * Valida tutti i campi di input e restituisce una lista di messaggi di errore.
     */
    public List<String> inputValidation() {

        // dichiara una lista per contenere eventuali errori
        List<String> errors = new ArrayList<>();

        // controlli vari sugli input dei campi di testo e conseguente aggiunta dell'errore alla lista di errori
        if (titleInput.getText() == null || titleInput.getText().trim().isEmpty()) {
            errors.add("Il titolo non può essere vuoto.");
        }
        if (authorInput.getText() == null || authorInput.getText().trim().isEmpty()) {
            errors.add("L'autore non può essere vuoto.");
        }
        if (genreInput.getText() == null || genreInput.getText().trim().isEmpty()) {
            errors.add("Il genere non può essere vuoto.");
        }

        String yearText = yearInput.getText() == null ? "" : yearInput.getText().trim();
        if (!yearText.matches("\\d{4}")) {
            errors.add("L'anno deve contenere esattamente 4 cifre numeriche.");
        } else {
            try {
                int yearValue = Integer.parseInt(yearText);
                int currentYear = Year.now().getValue();
                if (yearValue > currentYear) {
                    errors.add("L'anno non può essere maggiore di quello attuale.");
                }
            } catch (NumberFormatException e) {
                errors.add("L'anno non è un numero valido.");
            }
        }

        String minutesText = minutesInput.getText() == null ? "" : minutesInput.getText().trim();
        String secondsText = secondsInput.getText() == null ? "" : secondsInput.getText().trim();

        if (minutesText.isEmpty() || !minutesText.matches("\\d+")) {
            errors.add("I minuti devono essere un numero intero valido.");
        }
        if (secondsText.isEmpty() || !secondsText.matches("\\d+")) {
            errors.add("I secondi devono essere un numero intero valido.");
        } else {
            try {
                int seconds = Integer.parseInt(secondsText);
                if (seconds >= 60) {
                    errors.add("I secondi devono essere inferiori a 60.");
                }
            } catch (NumberFormatException e) {
            }
        }

        // ritorna gli errori trovati durante la validazione
        return errors;
    }

    // Metodi utilitari

    // metodo usato per creare un popUp di Alert riguardo degli errori
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // metodo usato per raggruppare in un metodo solo l'aggiunta di una traccia alla TrackList e alla lista osservabile
    private void add(Track t) {
        trackList.addTrack(t);
        observableList.add(t);
    }

    // metodo usato per creare un oggetto di tipo traccia a partire dagli input delle zone testuali del popUp
    private Track getTrack() {
        int minutes = Integer.parseInt(minutesInput.getText());
        int seconds = Integer.parseInt(secondsInput.getText());
        int totalSeconds = (minutes * 60) + seconds;
        Year year = Year.of(Integer.parseInt(yearInput.getText()));

        // crea e ritorna un oggetto di tipo Track usando gli input delle zone testuali
        return new Track(
                titleInput.getText(),
                authorInput.getText(),
                year,
                genreInput.getText(),
                totalSeconds,
                favouriteRadio.isSelected(),
                explicitContentRadio.isSelected(),
                newReleaseRadio.isSelected()
        );
    }

    // Gestisci la visibilità dei bottoni in base alla modalità corrente, se i bottoni non
    // vengono mostrati, viene tolto anche il loro spazio occupato nel layout
    private void manageButtonVisibility() {
        editButton.managedProperty().bind(editButton.visibleProperty());
        saveButton.managedProperty().bind(saveButton.visibleProperty());
        cancelButton.managedProperty().bind(cancelButton.visibleProperty());
        addTrackButton.managedProperty().bind(addTrackButton.visibleProperty());
        buttonBack.managedProperty().bind(buttonBack.visibleProperty());
    }

    // ripopola i text input con i dati della traccia di cui si sono visualizzati i dettagli
    private void populateFieldsFromTrack(Track track) {

        // setting dei campi della UI con i dati della traccia
        titleInput.setText(track.getTitle());
        authorInput.setText(track.getAuthor());
        yearInput.setText(String.valueOf(track.getYear().getValue()));
        genreInput.setText(track.getGenre());

        int minutes = track.getDuration() / 60;
        int seconds = track.getDuration() % 60;

        minutesInput.setText(String.valueOf(minutes));
        secondsInput.setText(String.format("%02d", seconds));

        favouriteRadio.setSelected(track.isFavourite());
        explicitContentRadio.setSelected(track.isExplicitContent());
        newReleaseRadio.setSelected(track.isNewRelease());
    }

    // setta i campi di input come modificabili o non a seconda del parametro passato
    // disabilitando i campi si setta la modalità di visualizzazione
    private void setFieldsEditable(boolean editable) {
        this.isReadOnly = !editable;

        titleInput.setEditable(editable);
        authorInput.setEditable(editable);
        yearInput.setEditable(editable);
        genreInput.setEditable(editable);
        minutesInput.setEditable(editable);
        secondsInput.setEditable(editable);

        favouriteRadio.setDisable(!editable);
        explicitContentRadio.setDisable(!editable);
        newReleaseRadio.setDisable(!editable);
    }


    // Imposta i bottoni da visualizzare nella modalità Info
    private void setInfoMode() {
        // disabilita i campi di input
        setFieldsEditable(false);

        // bottoni da rendere invisibili
        addTrackButton.setVisible(false);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);

        // bottoni da lasciare visibili
        editButton.setVisible(true);
        buttonBack.setVisible(true);
        buttonBack.setText("Chiudi");
    }

    // Imposta i bottoni visibili per la modalità Modifica
    // I campi riguardanti la durata del brano rimangono non modificabili
    private void setEditMode() {
        // abilita i campi di input
        setFieldsEditable(true);

        // disabilita i campi riguardanti la durata
        minutesInput.setEditable(false);
        secondsInput.setEditable(false);

        // bottoni da rendere invisibili
        addTrackButton.setVisible(false);
        editButton.setVisible(false);
        buttonBack.setVisible(false);

        // bottono da lasciare visibili
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
    }

}