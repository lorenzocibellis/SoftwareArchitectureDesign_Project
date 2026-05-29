package org.unisa.musicplaylistmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class TrackListController
{

    //definizione strutture dati "visibili"
    @FXML
    private ListView<Track> listView;
    private ObservableList<Track> trackListObservable;
    private TrackList trackList;


    @FXML
    public void initialize() {

        if (trackList == null) trackList = new TrackList();
        trackListObservable = FXCollections.observableArrayList(trackList.getTracks());

        listView.setItems(trackListObservable);
    }

    public void addNewTrack(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TrackView.fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle("Add track");


        Scene scene = new Scene(root);
        TrackController controller = loader.getController();
        controller.setTrackList(trackList);
        controller.setObservable(trackListObservable);
        stage.setScene(scene);

        stage.show();



    }

}