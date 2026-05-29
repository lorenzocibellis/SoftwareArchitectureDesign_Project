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


    @Deprecated
    public void azione1(ActionEvent actionEvent) throws IOException {

        // 1. Get the Stage from the button that triggered the event
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // 2. Load the new FXML file
        Parent newRoot = FXMLLoader.load(getClass().getResource("TrackView.fxml"));

        // 3. Create a new Scene and set it to the stage
        Scene newScene = new Scene(newRoot);
        stage.setScene(newScene);

        // 4. Keep the stage visible
        stage.show();
    }
    
}