package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Year;

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
    private TextField durationInput; 
    @FXML
    private RadioButton favouriteRadio;
    @FXML
    private RadioButton newReleaseRadio;
    @FXML
    private RadioButton explicitContentRadio;

    private TrackList trackList;
    private ObservableList observableList;

    //METODI

    @FXML
    public void initialize() {

    }

    @FXML
    public void inputValidation() {
        // Parsing dell'anno: se non è un numero intero a 4 cifre, segnaliamo l'input non valido
        String yearText = yearInput.getText() == null ? "" : yearInput.getText().trim();
        if (!yearText.matches("\\d{4}")) {
            showError("L'anno deve contenere esattamente 4 cifre numeriche.");
            throw new IllegalArgumentException("Anno non valido."); // Blocca il flusso se l'anno è errato
        }

        // Validazione della durata: formato m:ss con secondi < 60
        String durationText = durationInput.getText() == null ? "" : durationInput.getText().trim();
        if (!durationText.matches("\\d+:[0-5]\\d")) {
            showError("La durata deve essere nel formato m:ss (es. 3:45) con secondi < 60.");
            throw new IllegalArgumentException("Durata non valida."); // Blocca il flusso se la durata è errata
        }
    }

    // funzione per mostrare un messaggio di errore con contenuto personalizzato
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Input non valido");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setTrackList(TrackList tl){
        trackList = tl;
    }

    public void setObservable(ObservableList ol){
        this.observableList = ol;
    }

    private void add(Track t){
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

        try {
            // Eseguiamo la validazione completa
            inputValidation();

            // Calcolo della durata in secondi partendo dal formato m:ss
            String[] parts = durationInput.getText().split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            int totalSeconds = (minutes * 60) + seconds;

            Year year = Year.of(Integer.parseInt(yearInput.getText()));

            Track track = new Track(
                    titleInput.getText(),
                    authorInput.getText(),
                    year,
                    genreInput.getText(),
                    totalSeconds, // Usiamo la durata convertita in secondi
                    favouriteRadio.isSelected(),
                    explicitContentRadio.isSelected(),
                    newReleaseRadio.isSelected()
            );

            System.out.println("Track valida: " + track);
            this.add(track);
            goBack(actionEvent);
            
        } catch (IllegalArgumentException e) {
            // L'errore è già stato mostrato dal metodo showError dentro inputValidation o qui sotto
        } catch (Exception e) {
            showError("Errore durante la creazione: " + e.getMessage());
        }
    }
}