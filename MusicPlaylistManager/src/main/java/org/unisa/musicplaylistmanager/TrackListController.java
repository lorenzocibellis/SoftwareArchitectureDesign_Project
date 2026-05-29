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



    public void openTrackInfo(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackView.fxml"));
        Parent root = loader.load();

        TrackController controller = loader.getController();

        controller.setBack("TrackListView.fxml");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
}