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
    // Aggiunto flag per la modalità sola lettura per le info della traccia
    private boolean isReadOnly = false;

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
            // Rimuove tutto ciò che non è un numero
            String filtered = newValue.replaceAll("[^\\d]", "");
            // Limita la lunghezza a 4 cifre
            if (filtered.length() > 4) {
                filtered = filtered.substring(0, 4);
            }
            // Aggiorna il campo di testo solo se è cambiato, per evitare loop infiniti
            if (!yearInput.getText().equals(filtered)) {
                yearInput.setText(filtered);
            }
        });
    }

    /**
     * Metodo chiamato dal TrackListController per mostrare i dettagli in sola lettura.
     * @param track La traccia da mostrare.
     */
    public void setTrackDetails(Track track) {
        this.isReadOnly = true;

        // Popola i campi con i dati della traccia
        titleInput.setText(track.getTitle());
        authorInput.setText(track.getAuthor());
        yearInput.setText(String.valueOf(track.getYear().getValue()));
        genreInput.setText(track.getGenre());

        int minutes = track.getDuration() / 60;
        int seconds = track.getDuration() % 60;
        minutesInput.setText(String.valueOf(minutes));
        secondsInput.setText(String.format("%02d", seconds)); // Usa %02d per formattare "05" invece di "5"

        favouriteRadio.setSelected(track.isFavourite());
        explicitContentRadio.setSelected(track.isExplicitContent());
        newReleaseRadio.setSelected(track.isNewRelease());

        // Disabilita le modifiche per tutti i campi
        titleInput.setEditable(false);
        authorInput.setEditable(false);
        yearInput.setEditable(false);
        genreInput.setEditable(false);
        minutesInput.setEditable(false);
        secondsInput.setEditable(false);

        // Disabilita l'interazione con i radio button
        favouriteRadio.setDisable(true);
        explicitContentRadio.setDisable(true);
        newReleaseRadio.setDisable(true);

        // Nasconde il bottone "conferma"
        addTrackButton.setVisible(false);
        
        // Cambia il testo del bottone annulla in "chiudi"
        buttonBack.setText("Chiudi");
    }

    /**
     * Valida tutti i campi di input e restituisce una lista di messaggi di errore.
     * ritorna una lista di stringhe, dove ogni stringa è un errore di validazione. La lista è vuota se non ci sono errori.
     */
    public List<String> inputValidation() {
        List<String> errors = new ArrayList<>();

        // Validazione del titolo e dell'autore (non devono essere vuoti)
        if (titleInput.getText() == null || titleInput.getText().trim().isEmpty()) {
            errors.add("Il titolo non può essere vuoto.");
        }
        if (authorInput.getText() == null || authorInput.getText().trim().isEmpty()) {
            errors.add("L'autore non può essere vuoto.");
        }
        if (genreInput.getText() == null || genreInput.getText().trim().isEmpty()) {
            errors.add("Il genere non può essere vuoto.");
        }

        // Validazione dell'anno
        String yearText = yearInput.getText() == null ? "" : yearInput.getText().trim();
        if (!yearText.matches("\\d{4}")) {
            errors.add("L'anno deve contenere esattamente 4 cifre numeriche.");
        } else {
            // Se il formato è corretto, controlliamo il valore
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

        // Validazione della durata
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
                // Già gestito
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

    // aggiunge la traccia alla tracklist e alla lista osservabile
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

            System.out.println("Track valida: " + track);
            this.add(track);
            goBack(actionEvent);

        } catch (Exception e) {
            showError("Errore Inaspettato", "Si è verificato un errore durante la creazione della traccia.", e.getMessage());
        }
    }

    // prende gli attributi dai text input e ritorna la traccia
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