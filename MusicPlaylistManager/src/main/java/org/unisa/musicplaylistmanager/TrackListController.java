package org.unisa.musicplaylistmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;

public class TrackListController
{

    @FXML
    public void initialize() {
    }



    public void addNewTrack(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackView.fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle("Add track");


        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.show();



    }

}