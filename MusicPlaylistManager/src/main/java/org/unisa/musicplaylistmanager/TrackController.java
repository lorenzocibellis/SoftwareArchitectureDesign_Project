package org.unisa.musicplaylistmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;

public class TrackController
{
    @FXML
    private TextField anno;
    @FXML
    private RadioButton newRelease;
    @FXML
    private RadioButton contenutiEspliciti;
    @FXML
    private TextField traccia;
    @FXML
    private TextField genere;
    @FXML
    private TextField autore;

    //Variabili locali
    String backScene = "";

    //METODI

    @FXML
    public void initialize() {
    }

    //Permette di settare il comportamento del pulsante back, in modo da tornare alla finestra definita dalla stringa passata
    public void setBack(String back){
        this.backScene = back;
    }

    public void goBack(ActionEvent event) throws IOException {
        if(backScene == null || backScene.isEmpty()) throw new IllegalArgumentException();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Carica dinamicamente il file memorizzato nella variabile!
        Parent root = FXMLLoader.load(getClass().getResource(backScene));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}