package org.unisa.musicplaylistmanager.player;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.state.Play;
import org.unisa.musicplaylistmanager.track.Track;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML
    private Button executeButton;
    @FXML
    private Button skipToNextButton;
    @FXML
    private Button skipToPreviousButton;
    @FXML
    private Slider songProgress;
    @FXML
    private Label duration;
    @FXML
    private Label counter;
    @FXML
    private Text trackTitle;
    @FXML
    private Text authorName;
    @FXML
    private Button closeButton;

    private String iconsRoot = "/icons/";
    private Player player;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // disabilita TEMPORANEAMENTE i tasti di skip
        skipToNextButton.setDisable(true);
        skipToPreviousButton.setDisable(true);
        // Carichiamo le icone per i tasti di skip
        setButtonImage(skipToNextButton, iconsRoot + "skipNextButton.png", 40, 40);
        setButtonImage(skipToPreviousButton, iconsRoot + "skipPreviousButton.png", 40, 40);
    }

    /**
     * Inizializza il player con la traccia e la playlist.
     * Chiamato dopo il caricamento dell'FXML da ActivePlayerManager.
     */
    public void init(Track initialTrack, Playlist playlist) {
        updateTrackUI(initialTrack);
        if (player != null) {
            player.terminate();
        }
        player = new Player(new Play(), playlist, initialTrack);

        player.setOnTimeTick(seconds -> {
            Platform.runLater(() -> {
                songProgress.setValue(seconds);
                counter.setText(formatTime(seconds));
            });
        });

        player.setOnPlayUIUpdate(() -> {
            Platform.runLater(() -> setExecuteButtonImage(iconsRoot + "pauseButton.png", 45, 45));
        });

        player.setOnPauseUIUpdate(() -> {
            Platform.runLater(() -> setExecuteButtonImage(iconsRoot + "playButton.jpg", 45, 45));
        });

        player.changeState();
    }

    // listener del tasto centrale di play/pause
    @FXML
    public void handleExecute() {
        if (player != null) {
            player.changeState();
        }
    }

    // metodo che gestisce il passaggio alla riproduzione della traccia successiva
    @FXML
    public void handleNext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // metodo che gestisce il passaggio alla riproduzione della traccia precedente
    @FXML
    public void handlePrevious() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // metodo che gestisce la chiusura del player
    @FXML
    public void handleClose() {
        if (player != null) {
            player.terminate();
        }
        ActivePlayerManager.getInstance().closePlayer();
    }

    // metodo di aggiornamento dell'interfaccia del player in tempo reale
    private void updateTrackUI(Track track) {
        if (track != null) {
            trackTitle.textProperty().unbind();
            authorName.textProperty().unbind();

            trackTitle.textProperty().bind(track.titleProperty());
            authorName.textProperty().bind(track.authorProperty());

            // Setup iniziale
            songProgress.setMin(0);
            songProgress.setMax(track.getDuration());
            songProgress.setValue(0);
            duration.setText(formatTime(track.getDuration()));
            counter.setText("0:00");
        }
    }

    // metodo che cambia l'icona di un bottone
    public void setButtonImage(Button targetButton, String resourcePath, double width, double height) {
        try {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(resourcePath)));
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            targetButton.setGraphic(iv);
        } catch (Exception e) {
            System.err.println("Immagine tasto non trovata: " + resourcePath);
        }
    }

    public void setExecuteButtonImage(String resourcePath, double width, double height) {
        setButtonImage(this.executeButton, resourcePath, width, height);
    }

    // metodo di formattazione della durata in secondi
    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d", m, s);
    }

    //  METODO AGGIUNTO PER I TEST JUNIT
    public Player getPlayer() {
        return this.player;
    }
}