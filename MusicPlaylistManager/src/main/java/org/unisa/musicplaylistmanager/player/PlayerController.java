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
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;
import org.unisa.musicplaylistmanager.state.Play;
import org.unisa.musicplaylistmanager.track.Track;

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

    private String iconsRoot = "/icons/";
    private Player player;

    /**
     * Metodo chiamato automaticamente da JavaFX dopo il caricamento del file FXML.
     * Inizializza i componenti visivi di base.
     * 
     * @param url l'URL utilizzato per risolvere percorsi relativi per l'oggetto radice
     * @param rb le risorse localizzate per l'oggetto radice
     */
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
     * Inizializza il player logico con la traccia e la playlist specificate.
     * Questo metodo deve essere chiamato esplicitamente da ActivePlayerManager 
     * subito dopo aver ottenuto questo controller.
     * 
     * @param initialTrack la traccia da riprodurre
     * @param playlist la playlist o tracklist contenente la traccia
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

    /**
     * Gestisce l'azione del pulsante centrale (Play/Pausa).
     * Invoca il cambio di stato nel player logico.
     */
    @FXML
    public void handleExecute() {
        if (player != null) {
            player.changeState();
        }
    }

    /**
     * Gestisce l'azione del pulsante "Successiva".
     * Attualmente non implementato
     */
    @FXML
    public void handleNext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gestisce l'azione del pulsante "Precedente".
     * Attualmente non implementato
     */
    @FXML
    public void handlePrevious() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gestisce l'azione del pulsante di chiusura (X) del mini-player.
     * Termina la riproduzione e richiede la chiusura del player al manager.
     */
    @FXML
    public void handleClose() {
        if (player != null) {
            player.terminate();
        }
        ActivePlayerManager.getInstance().closePlayer();
    }

    /**
     * Aggiorna gli elementi testuali e grafici della UI (titolo, autore, timer)
     * basandosi sui dati della traccia specificata.
     * 
     * @param track la traccia di cui visualizzare le informazioni
     */
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

    /**
     * Imposta l'immagine per un pulsante specificato.
     * 
     * @param targetButton il bottone a cui applicare l'icona
     * @param resourcePath il percorso della risorsa immagine
     * @param width la larghezza desiderata per l'icona
     * @param height l'altezza desiderata per l'icona
     */
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

    /**
     * Imposta l'immagine specifica per il pulsante centrale di esecuzione (Play/Pause).
     * 
     * @param resourcePath il percorso della risorsa immagine
     * @param width la larghezza desiderata per l'icona
     * @param height l'altezza desiderata per l'icona
     */
    public void setExecuteButtonImage(String resourcePath, double width, double height) {
        setButtonImage(this.executeButton, resourcePath, width, height);
    }

    /**
     * Formatta un ammontare totale di secondi in una stringa nel formato "minuti:secondi".
     * 
     * @param totalSeconds il numero totale di secondi da formattare
     * @return la stringa formattata
     */
    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d", m, s);
    }

    /**
     * Restituisce l'istanza del player logico associata a questo controller.
     * Utilizzato principalmente per scopi di testing.
     * 
     * @return il {@link Player} corrente
     */
    public Player getPlayer() {
        return this.player;
    }
}