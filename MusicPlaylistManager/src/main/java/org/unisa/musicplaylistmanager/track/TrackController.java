package org.unisa.musicplaylistmanager.track;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.command.AddTrackCommand;
import org.unisa.musicplaylistmanager.command.BaseTrackCommands;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.tag.PersonalTagManager;

import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la finestra di visualizzazione, modifica e creazione
 * di una traccia musicale. Gestisce l'input utente, la validazione dei dati
 * e l'aggiornamento dinamico dell'interfaccia.
 *
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
    @FXML
    private Label favouritePreview;
    @FXML
    private Label explicitPreview;
    @FXML
    private Label newReleasePreview;
    
    // Nuovi controlli per il FileChooser della copertina
    @FXML
    private TextField coverPathInput;
    @FXML
    private Button browseCoverButton;
    @FXML
    private FlowPane personalTagsPane;

    private List<RadioButton> personalTags = new ArrayList<>();

    // definizione attributi

    // lista di tracce su cui lavorare
    private TrackCollection trackList;

    //struttura dati della UI da aggiornare
    private ObservableList<Track> observableList;

    // flag utilitario per UI dinamica
    private boolean isReadOnly = false;

    // Riferimento alla traccia che stiamo visualizzando/modificando
    private Track currentTrack;


    /**
     * Metodo chiamato automaticamente da JavaFX.
     * Configura i listener sui campi di testo per forzare l'input numerico
     * su minuti, secondi e anno.
     */
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

        // Listener per mostrare/nascondere l'anteprima dei tag badge
        bindTagPreview(favouriteRadio, favouritePreview);
        bindTagPreview(explicitContentRadio, explicitPreview);
        bindTagPreview(newReleaseRadio, newReleasePreview);
        
        populatePersonalTags();
    }

    /**
     * Genera dinamicamente l'interfaccia utente per la selezione dei tag personali.
     * Recupera la lista globale dei tag da {@link org.unisa.musicplaylistmanager.tag.PersonalTagManager}.
     * Se non ci sono tag, mostra un messaggio informativo. Altrimenti, crea una serie di bottoni 
     * selezionabili (RadioButton + Label) per permettere all'utente di associarli alla traccia.
     */
    private void populatePersonalTags() {
        personalTagsPane.getChildren().clear();
        personalTags.clear();

        List<String> tags = PersonalTagManager.getInstance().getPersonalTags();
        
        if (tags.isEmpty()) {
            Label placeholder = new Label("Nessun tag personale creato. Aggiungili dalla schermata libreria!");
            placeholder.getStyleClass().add("tags-placeholder");
            personalTagsPane.getChildren().add(placeholder);
            return;
        }

        for (String tag : tags) {
            HBox card = new HBox();
            card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            card.setSpacing(6.0);
            card.getStyleClass().add("tag-toggle-card");
            card.setPadding(new javafx.geometry.Insets(6.0, 10.0, 6.0, 10.0));

            RadioButton rb = new RadioButton("");
            rb.setUserData(tag);
            
            Label preview = new Label(tag);
            preview.getStyleClass().addAll("tag-badge", "tag-personal");

            card.setOnMouseClicked(e -> {
                if (!rb.isDisabled()) {
                    rb.setSelected(!rb.isSelected());
                }
            });

            card.getChildren().addAll(rb, preview);
            personalTags.add(rb);
            personalTagsPane.getChildren().add(card);
        }
    }

    /**
     * Collega un RadioButton alla visibilità del suo tag badge di anteprima.
     *
     * @param radio il RadioButton da osservare
     * @param preview il Label badge da mostrare/nascondere
     */
    private void bindTagPreview(RadioButton radio, Label preview) {
        if (radio != null && preview != null) {
            preview.setVisible(radio.isSelected());
            preview.setManaged(radio.isSelected());
            radio.selectedProperty().addListener((obs, oldVal, newVal) -> {
                preview.setVisible(newVal);
                preview.setManaged(newVal);
            });
        }
    }

    /**
     * Apre il FileChooser di sistema per selezionare un'immagine dal computer
     * dell'utente e ne salva il percorso nel campo di testo dedicato.
     * * @param event l'evento generato dal click
     */
    @FXML
public void browseCover(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleziona Copertina");
    
    // Imposta la cartella iniziale predefinita
    File defaultDir = new File("src/main/resources/covers");
    
    // Controllo di sicurezza: imposta la directory solo se esiste ed è effettivamente una cartella
    if (defaultDir.exists() && defaultDir.isDirectory()) {
        fileChooser.setInitialDirectory(defaultDir);
    }
    
    // Filtri per far selezionare solo file immagine validi
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Immagini (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"),
        new FileChooser.ExtensionFilter("Tutti i file", "*.*")
    );

    // Apre la finestra di dialogo
    Stage stage = (Stage) browseCoverButton.getScene().getWindow();
    File selectedFile = fileChooser.showOpenDialog(stage);

    if (selectedFile != null) {
        // Mostra il path assoluto nel TextField
        coverPathInput.setText(selectedFile.getAbsolutePath());
    }
}

    /**
     * Abilita la modalità di modifica, sbloccando i campi testuali
     * (tranne quelli della durata) e aggiornando i pulsanti visibili.
     * * @param event l'evento generato dal click
     */
    @FXML
    public void editTrack(ActionEvent event) {
        setEditMode();
    }

    /**
     * Annulla le modifiche in corso e ripristina i valori originali
     * della traccia, tornando alla modalità di sola lettura.
     * * @param event l'evento generato dal click
     */
    @FXML
    public void cancelChanges(ActionEvent event) {
        // Ripristina i valori originali e torna in modalità Info
        populateFieldsFromTrack(currentTrack);
        setInfoMode();
    }

    /**
     * Salva le modifiche apportate alla traccia. Esegue la validazione dell'input,
     * aggiorna il modello e, se l'operazione ha successo senza creare duplicati,
     * aggiorna la UI tornando alla modalità informativa.
     * * @param event l'evento generato dal click
     */
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

            String selectedCover = coverPathInput.getText();
            if (selectedCover == null || selectedCover.trim().isEmpty()) {
                selectedCover = null;
            }

            // Creiamo un oggetto traccia temporaneo (usato solo per trasportare i nuovi dati)
            Track updatedData = new Track(
                    titleInput.getText(),
                    authorInput.getText(),
                    year,
                    genreInput.getText(),
                    totalSeconds,
                    favouriteRadio.isSelected(),
                    explicitContentRadio.isSelected(),
                    newReleaseRadio.isSelected(),
                    selectedCover
            );
            
            for (RadioButton cb : personalTags) {
                if (cb.isSelected()) {
                    updatedData.addPersonalTag(cb.getUserData().toString());
                }
            }

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

            if (currentTrack != null) {
                currentTrack.setCoverImage(selectedCover);
            }

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

    /**
     * Aggiunge una nuova traccia alla libreria. Esegue la validazione,
     * crea la traccia, l'aggiunge al sistema e chiude la finestra.
     * * @param actionEvent l'evento generato dal click
     */
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

            //aggiungi la traccia alla lista di tracce usando pattern Command
            BaseTrackCommands command = new AddTrackCommand(track, trackList,  observableList);
            CommandInvoker.getCommandInvokerPointer().setCommand(command);

            // chiudi il popUp
            goBack(actionEvent);
        } catch (Exception e) { //nel caso di errori durante la creazione e l'aggiunta della traccia
            showError("Errore Inaspettato", "Si è verificato un errore durante la creazione.", e.getMessage());
        }
    }

    /**
     * Chiude la finestra corrente.
     * * @param event l'evento generato dal click
     * @throws IOException in caso di problemi di chiusura
     */
    @FXML
    public void goBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    // Dichiarazione metodi pubblici

    /**
     * Imposta il riferimento alla Playlist (o TrackList) in cui si sta
     * salvando o modificando la traccia.
     * * @param tc l'oggetto TrackList
     */
    public void setTrackList(TrackCollection tc) {
        trackList = tc;
    }

    /**
     * Imposta la lista osservabile per l'aggiornamento in tempo reale della UI.
     * * @param ol la lista osservabile delle tracce
     */
    public void setObservable(ObservableList<Track> ol) {
        this.observableList = ol;
    }


    /**
     * Imposta i dati della traccia da visualizzare e configura la finestra
     * in modalità di sola lettura (InfoMode).
     * * @param track la traccia di cui mostrare i dettagli
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
     * Valida tutti i campi di input della finestra.
     * * @return una lista di stringhe contenente i messaggi d'errore (vuota se tutto è valido)
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
        
        // Verifica che la durata complessiva sia maggiore di 0
        try {
            if (minutesText.matches("\\d+") && secondsText.matches("\\d+")) {
                int m = Integer.parseInt(minutesText);
                int s = Integer.parseInt(secondsText);
                if (m == 0 && s == 0) {
                    errors.add("La durata della traccia deve essere maggiore di 0.");
                }
            }
        } catch (NumberFormatException e) {
        }

        // ritorna gli errori trovati durante la validazione
        return errors;
    }

    // Metodi utilitari

    /**
     * Mostra una finestra di avviso in caso di errore.
     * * @param title il titolo della finestra di errore
     * @param header l'intestazione dell'errore
     * @param content il dettaglio dell'errore
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Crea un oggetto {@link Track} utilizzando i dati attualmente inseriti 
     * nei campi di input.
     * * @return la nuova traccia generata
     */
    private Track getTrack() {
        int minutes = Integer.parseInt(minutesInput.getText());
        int seconds = Integer.parseInt(secondsInput.getText());
        int totalSeconds = (minutes * 60) + seconds;
        Year year = Year.of(Integer.parseInt(yearInput.getText()));

        String selectedCover = coverPathInput.getText();
        if (selectedCover == null || selectedCover.trim().isEmpty()) {
            selectedCover = null;
        }

        // crea e ritorna un oggetto di tipo Track usando gli input delle zone testuali
        Track track = new Track(
                titleInput.getText(),
                authorInput.getText(),
                year,
                genreInput.getText(),
                totalSeconds,
                favouriteRadio.isSelected(),
                explicitContentRadio.isSelected(),
                newReleaseRadio.isSelected(),
                selectedCover
        );
        for (RadioButton cb : personalTags) {
            if (cb.isSelected()) {
                track.addPersonalTag(cb.getUserData().toString());
            }
        }
        return track;
    }

    /**
     * Gestisce dinamicamente lo spazio occupato dai pulsanti. 
     * Se un pulsante è invisibile, non occuperà spazio nel layout.
     */
    private void manageButtonVisibility() {
        editButton.managedProperty().bind(editButton.visibleProperty());
        saveButton.managedProperty().bind(saveButton.visibleProperty());
        cancelButton.managedProperty().bind(cancelButton.visibleProperty());
        addTrackButton.managedProperty().bind(addTrackButton.visibleProperty());
        buttonBack.managedProperty().bind(buttonBack.visibleProperty());
    }

    /**
     * Riempie i campi di testo con i dati di una specifica traccia.
     * * @param track la traccia di cui leggere i dati
     */
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

        if (track.getCoverImage() != null) {
            coverPathInput.setText(track.getCoverImage());
        } else {
            coverPathInput.clear();
        }

        // controlla la lista dei tag personali salvata nella traccia.
        // se la traccia possiede il tag associato a quel bottone, lo imposta come selezionato,
        // altrimenti lo lascia vuoto.
        List<String> trackTags = track.getPersonalTags();
        for (RadioButton cb : personalTags) {
            cb.setSelected(trackTags != null && trackTags.contains(cb.getUserData().toString()));
        }
    }

    /**
     * Abilita o disabilita la modifica dei campi di testo, impostando
     * di conseguenza la variabile di stato {@code isReadOnly}.
     * * @param editable {@code true} se i campi devono essere modificabili
     */
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
        
        for (RadioButton cb : personalTags) {
            cb.setDisable(!editable);
        }
        
        // Il campo di testo per il path resta non modificabile a mano per sicurezza
        // ma abilitiamo/disabilitiamo il pulsante per aprirlo
        browseCoverButton.setDisable(!editable);
    }


    /**
     * Imposta la modalità "Info": campi bloccati (sola lettura), pulsanti di 
     * salvataggio nascosti e pulsanti di modifica/chiusura visibili.
     */
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

        // Rimuove il focus dai TextField così nessun campo è evidenziato
        Platform.runLater(() -> editButton.requestFocus());
    }

    /**
     * Imposta la modalità "Modifica": campi abilitati (eccetto la durata), 
     * pulsanti di salvataggio visibili e focus automatico sul campo titolo.
     */
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

        // Mette il focus sul campo titolo e posiziona il cursore alla fine del testo
        Platform.runLater(() -> {
            titleInput.requestFocus();
            titleInput.positionCaret(titleInput.getText().length());
        });
    }
}