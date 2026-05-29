package org.unisa.musicplaylistmanager;


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

            System.out.println("Track valida: " + track);
            this.add(track);
            goBack(actionEvent);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}