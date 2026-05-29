package org.unisa.musicplaylistmanager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import java.time.Year;

public class TrackController {

    @FXML private TextField anno;
    @FXML private RadioButton newRelease;
    @FXML private RadioButton contenutiEspliciti;
    @FXML private TextField titolo;
    @FXML private TextField genere;
    @FXML private TextField autore;
    @FXML private Button confirmButton;

    @FXML
    public void initialize() {}
    
    // funzione per confermare o meno la creazione di una nuova traccia
    @FXML
    public void confirm() {
        // Parsing dell'anno: se non è un numero intero a 4 cifre, segnaliamo l'input non valido
        String yearText = anno.getText() == null ? "" : anno.getText().trim();
        if (!yearText.matches("\\d{4}")) {
            showError("L'anno deve contenere esattamente 4 cifre numeriche.");
            return;
        }

        Year year = Year.of(Integer.parseInt(yearText));

        try {
            Track track = new Track(
                titolo.getText(),
                autore.getText(),
                year,
                genere.getText(),
                0,                                  
                false,                            
                contenutiEspliciti.isSelected(),
                newRelease.isSelected()
            );

            System.out.println("Track valida: " + track.getTitle());
            
            // QUI VA AGGIUNTA LA TRACK ALLA TRACKLIST

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
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
}