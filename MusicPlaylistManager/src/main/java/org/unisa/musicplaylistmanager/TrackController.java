package org.unisa.musicplaylistmanager;


import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

public class TrackController {


    @FXML
    private TextField anno;

    @FXML
    private RadioButton newRelease;

    @FXML
    private RadioButton contenutiEspliciti;

    @FXML
    private TextField titolo;

    @FXML
    private TextField genere;

    @FXML
    private TextField autore;

    
    //METODI

    @FXML
    public void initialize() {


        anno.setTextFormatter(
                new TextFormatter<>(change -> {

                    if (change.getControlNewText().matches("\\d{0,4}")) {
                        return change;
                    }

                    return null;
                })
        );
    }

}