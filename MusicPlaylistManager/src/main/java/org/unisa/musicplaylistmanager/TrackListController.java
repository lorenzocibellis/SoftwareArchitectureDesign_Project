package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

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
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.IOException;

public class TrackListController
{
    // recupero della radice della schermata
    @FXML
    private StackPane mainStackPane;

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
        
        // gestione del click su una traccia
        listView.setOnMouseClicked(event -> {
            Track selected = listView.getSelectionModel().getSelectedItem();
            
            // controllo di sicurezza se l'utente dovesse cliccare su uno spazio vuoto
            if (selected == null) return; 

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerView.fxml"));
   
                AnchorPane playerRoot = loader.load(); 

                PlayerController ctrl = loader.getController();
                
                // Passiamo momentaneamente sia la traccia che la playlist (castata)
                ctrl.setPlaylistContext(selected, (Playlist) trackList); 
                // ──────────────────────────────────────────────────────────────────────
                
                // Passiamo al controller il riferimento alla sua stessa radice (per l'animazione di chiusura)
                ctrl.setPlayerRoot(playerRoot);

                // LOGICA DELL'OVERLAY DEL PLAYER
                
                // Se c'è già un player aperto nello StackPane (l'utente ha cliccato un'altra canzone senza chiudere), 
                // rimuoviamo il vecchio player prima di aggiungere il nuovo per evitare sovrapposizioni multiple.
                if (mainStackPane.getChildren().size() > 1) {
                     mainStackPane.getChildren().remove(1);
                }

                // Aggiungiamo il Player allo StackPane (si posizionerà sopra la lista)
                mainStackPane.getChildren().add(playerRoot);
                // Ancoriamo in basso
                StackPane.setAlignment(playerRoot, Pos.BOTTOM_CENTER);
                // Posizioniamo il player "fuori" dallo schermo (in basso) prima dell'animazione
                playerRoot.setTranslateY(mainStackPane.getHeight());

                // Creiamo l'animazione per farlo salire dal centro
                TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.4), playerRoot);
                slideUp.setToY(0); 
                slideUp.play();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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