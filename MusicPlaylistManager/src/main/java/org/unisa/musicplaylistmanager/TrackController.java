package org.unisa.musicplaylistmanager;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gruppo10
 */
public class TrackController {

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

    private TrackList trackList;
    private ObservableList<Track> observableList;
    private boolean isReadOnly = false;

    // Riferimento alla traccia che stiamo visualizzando/modificando
    private Track currentTrack;

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

    // Gestisci la visibilità dei bottoni in base alla modalità corrente, se i bottoni non
    // vengono mostrati, viene tolto anche il loro spazio occupato nel layout
    private void manageButtonVisibility() {
        editButton.managedProperty().bind(editButton.visibleProperty());
        saveButton.managedProperty().bind(saveButton.visibleProperty());
        cancelButton.managedProperty().bind(cancelButton.visibleProperty());
        addTrackButton.managedProperty().bind(addTrackButton.visibleProperty());
        buttonBack.managedProperty().bind(buttonBack.visibleProperty());
    }

    /**
     * Metodo chiamato dal TrackListController per mostrare i dettagli in sola lettura.
     */
    public void setTrackDetails(Track track) {
        this.currentTrack = track;

        // Popola i campi con i dati attuali
        populateFieldsFromTrack(track);

        // Imposta la modalità Info (blocca i campi, mostra Modifica/Chiudi)
        setInfoMode();
    }


    // ripopola i text input con i dati della traccia di cui si sono visualizzati i dettagli
    private void populateFieldsFromTrack(Track track) {
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
        setFieldsEditable(false);


        addTrackButton.setVisible(false);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);

        editButton.setVisible(true);
        buttonBack.setVisible(true);
        buttonBack.setText("Chiudi");
    }

    // Imposta i bottoni visibili per la modalità Modifica
    private void setEditMode() {
        setFieldsEditable(true);


        addTrackButton.setVisible(false);
        editButton.setVisible(false);
        buttonBack.setVisible(false);

        saveButton.setVisible(true);
        cancelButton.setVisible(true);
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

    @FXML
    public void saveChanges(ActionEvent event) {
        List<String> errors = inputValidation();

        if (!errors.isEmpty()) {
            String errorContent = String.join("\n", errors);
            showError("Errore di Validazione", "Per favore, correggi i seguenti errori:", errorContent);
            return;
        }

        try {
            // Aggiorna l'oggetto traccia esistente con i nuovi valori
            int minutes = Integer.parseInt(minutesInput.getText());
            int seconds = Integer.parseInt(secondsInput.getText());
            int totalSeconds = (minutes * 60) + seconds;
            Year year = Year.of(Integer.parseInt(yearInput.getText()));

            currentTrack.setTitle(titleInput.getText());
            currentTrack.setAuthor(authorInput.getText());
            currentTrack.setYear(year);
            currentTrack.setGenre(genreInput.getText());
            currentTrack.setDuration(totalSeconds);
            currentTrack.setFavourite(favouriteRadio.isSelected());
            currentTrack.setExplicit(explicitContentRadio.isSelected());
            currentTrack.setNewRelease(newReleaseRadio.isSelected());

            // Forza l'aggiornamento della visualizzazione nella ListView (che è legata all'observableList)

            if (observableList != null) {
                int index = observableList.indexOf(currentTrack);
                if (index != -1) {
                    observableList.set(index, currentTrack);
                }
            }

            // Torna alla schermata Info per mostrare le modifiche
            setInfoMode();

        } catch (Exception e) {
            showError("Errore Inaspettato", "Si è verificato un errore durante il salvataggio.", e.getMessage());
        }
    }

    /**
     * Valida tutti i campi di input e restituisce una lista di messaggi di errore.
     */
    public List<String> inputValidation() {
        List<String> errors = new ArrayList<>();

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

        return errors;
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setTrackList(TrackList tl) {
        trackList = tl;
    }

    public void setObservable(ObservableList<Track> ol) {
        this.observableList = ol;
    }

    private void add(Track t) {
        trackList.addTrack(t);
        observableList.add(t);
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void addTrack(ActionEvent actionEvent) {
        List<String> errors = inputValidation();

        if (!errors.isEmpty()) {
            String errorContent = String.join("\n", errors);
            showError("Errore di Validazione", "Per favore, correggi i seguenti errori:", errorContent);
            return;
        }

        try {
            Track track = getTrack();
            this.add(track);
            goBack(actionEvent);
        } catch (Exception e) {
            showError("Errore Inaspettato", "Si è verificato un errore durante la creazione.", e.getMessage());
        }
    }

    private Track getTrack() {
        int minutes = Integer.parseInt(minutesInput.getText());
        int seconds = Integer.parseInt(secondsInput.getText());
        int totalSeconds = (minutes * 60) + seconds;
        Year year = Year.of(Integer.parseInt(yearInput.getText()));

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
}