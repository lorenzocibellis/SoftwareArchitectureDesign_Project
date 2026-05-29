package org.unisa.musicplaylistmanager;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXML;

public class TrackListController
{
    @FXML
    private Button button1;

    @FXML
    public void initialize() {
    }


    @FXML
    public void azione1(ActionEvent actionEvent) {

        System.out.println("azione_prova");
    }
}