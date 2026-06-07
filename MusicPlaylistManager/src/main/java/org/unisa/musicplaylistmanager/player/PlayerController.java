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
import org.unisa.musicplaylistmanager.playlist.TrackCollection; 
import org.unisa.musicplaylistmanager.state.Play;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller JavaFX per la vista del mini-player (MiniPlayerView.fxml).
 * Gestisce l'aggiornamento dell'interfaccia utente (titolo, autore, timer, barra di progresso)
 * e riceve gli eventi dell'utente dai pulsanti (play/pausa, skip, chiusura).
 *
 * @author gruppo10
 */
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
    @FXML 
    private Button shuffleButton;
    @FXML 
    private Button loopButton;

    // Variabili per tracciare la modalità attiva
    private boolean isShuffleActive = false;
    private boolean isLoopActive = false;

    // Stili CSS per accendere e spegnere i tasti
    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-cursor: hand; -fx-effect: none;";
    private final String STYLE_ACTIVE = "-fx-background-color: transparent; -fx-cursor: hand; " +
                                        "-fx-effect: dropshadow(gaussian, #0096ff, 15, 0.5, 0, 0);";
    private String iconsRoot = "/icons/";
    private Player player;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        songProgress.setOnMouseReleased(event -> {
            if (player != null) {
                player.seekTo((int) songProgress.getValue());
            }
        });
    }

    /**
     * Inizializza il player logico con la traccia e la collezione specificate.
     * @param initialTrack la traccia da riprodurre
     * @param trackCollection la playlist o tracklist contenente la traccia
     */
    public void init(Track initialTrack, TrackCollection trackCollection) {
        updateTrackUI(initialTrack);
        if (player != null) {
            player.terminate();
        }
        player = new Player(new Play(), trackCollection, initialTrack);

        player.setOnTrackChanged(() -> {

            Platform.runLater(() -> {
                // Aggiorna l'interfaccia grafica del mini-player
                updateTrackUI(player.getCurrentTrack());
                
                // Aggiorna la proprietà globale in ActivePlayerManager con la nuova traccia.
                // Questo scatena il listener sulle ListView di traccia e playlist, per evidenziare la traccia
                // in ascolto
                ActivePlayerManager.getInstance().setCurrentTrack(player.getCurrentTrack());
            });
        });

        player.setOnTimeTick(seconds -> {
            Platform.runLater(() -> {
                if (!songProgress.isPressed()) {
                    songProgress.setValue(seconds);
                }
                counter.setText(formatTime(seconds));
            });
        });

        player.setOnPlayUIUpdate(() -> {
            Platform.runLater(() -> setExecuteButtonImage(iconsRoot + "pauseButton.png", 45, 45));
        });

        player.setOnPauseUIUpdate(() -> {
            Platform.runLater(() -> setExecuteButtonImage(iconsRoot + "playButton.jpg", 38, 38));
        });

        player.changeState();
    }

    @FXML
    public void handleExecute() {
        if (player != null) {
            player.changeState();
        }
    }

    @FXML
    public void handleNext() {
        if (player != null) {
            player.nextTrack();
        }
    }

    @FXML
    public void handlePrevious() {
        if (player != null) {
            player.previousTrack();
        }
    }

    @FXML
    public void handleClose() {
        if (player != null) {
            player.terminate();
        }
        ActivePlayerManager.getInstance().closePlayer();
    }

    private void updateTrackUI(Track track) {
        if (track != null) {
            trackTitle.textProperty().unbind();
            authorName.textProperty().unbind();

            trackTitle.textProperty().bind(track.titleProperty());
            authorName.textProperty().bind(track.authorProperty());

            songProgress.setMin(0);
            songProgress.setMax(track.getDuration());
            songProgress.setValue(0);
            duration.setText(formatTime(track.getDuration()));
            counter.setText("0:00");
        }
    }

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

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d", m, s);
    }

    public Player getPlayer() {
        return this.player;
    }

    @FXML
    public void handleShuffle() {
        if (player == null) return;
        isShuffleActive = !isShuffleActive;
        if (isShuffleActive) {
            isLoopActive = false;
            player.getIterator().setStrategy(new org.unisa.musicplaylistmanager.strategy.Shuffle());
        } else {
            player.getIterator().setStrategy(new org.unisa.musicplaylistmanager.strategy.Sequential());
        }
        updateStrategyUI();
    }

    @FXML
    public void handleLoop() {
        if (player == null) return;
        isLoopActive = !isLoopActive;
        if (isLoopActive) {
            isShuffleActive = false;
            player.getIterator().setStrategy(new org.unisa.musicplaylistmanager.strategy.Loop());
        } else {
            player.getIterator().setStrategy(new org.unisa.musicplaylistmanager.strategy.Sequential());
        }
        updateStrategyUI();
    }

    private void updateStrategyUI() {
        shuffleButton.setStyle(isShuffleActive ? STYLE_ACTIVE : STYLE_NORMAL);
        loopButton.setStyle(isLoopActive ? STYLE_ACTIVE : STYLE_NORMAL);
    }
}