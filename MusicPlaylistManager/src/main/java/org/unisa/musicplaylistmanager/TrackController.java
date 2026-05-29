package org.unisa.musicplaylistmanager;


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
    private RadioButton favouriteRadio;
    @FXML
    private RadioButton newReleaseRadio;
    @FXML
    private RadioButton explicitContentRadio;


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
        }
    }

    // funzione per mostrare un emssaggio di errore con contenuto personalizzato
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Input non valido");
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    public void goBack(ActionEvent event) throws IOException {


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }

    @FXML
    public void addTrack(ActionEvent actionEvent) {

        inputValidation();

        Year year = Year.of(Integer.parseInt(yearInput.getText()));

        try {
            Track track = new Track(
                    titleInput.getText(),
                    authorInput.getText(),
                    year,
                    genreInput.getText(),
                    0,
                    false,
                    explicitContentRadio.isSelected(),
                    newReleaseRadio.isSelected()
            );

            System.out.println("Track valida: " + track.getTitle());


        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }


        // crea traccia

    }
}