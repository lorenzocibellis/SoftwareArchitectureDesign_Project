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

import java.io.File;
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

    /** Pulsante per avviare o mettere in pausa la riproduzione. */
    @FXML
    private Button executeButton;

    /** Pulsante per passare alla traccia successiva. */
    @FXML
    private Button skipToNextButton;

    /** Pulsante per tornare alla traccia precedente. */
    @FXML
    private Button skipToPreviousButton;

    /** Slider che mostra e permette di modificare la posizione corrente nella traccia. */
    @FXML
    private Slider songProgress;

    /** Etichetta che mostra la durata totale della traccia corrente. */
    @FXML
    private Label duration;

    /** Etichetta che mostra il tempo trascorso durante la riproduzione. */
    @FXML
    private Label counter;

    /** Testo che mostra il titolo della traccia in riproduzione. */
    @FXML
    private Text trackTitle;

    /** Testo che mostra il nome dell'autore della traccia in riproduzione. */
    @FXML
    private Text authorName;

    /** Testo che mostra il contesto di riproduzione (es. nome della playlist). */
    @FXML
    private Text contextLabel;

    /** Pulsante per chiudere il mini-player. */
    @FXML
    private Button closeButton;

    /** Pulsante per attivare/disattivare la modalità shuffle. */
    @FXML
    private Button shuffleButton;

    /** Pulsante per attivare/disattivare la modalità loop. */
    @FXML
    private Button loopButton;

    /** ImageView che mostra la copertina della traccia corrente. */
    @FXML
    private ImageView playerCoverImageView;

    /** Indica se la modalità shuffle è attualmente attiva. */
    private boolean isShuffleActive = false;

    /** Indica se la modalità loop è attualmente attiva. */
    private boolean isLoopActive = false;

    /** Stile CSS applicato ai pulsanti shuffle/loop quando sono inattivi. */
    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-cursor: hand; -fx-effect: none;";

    /** Stile CSS applicato ai pulsanti shuffle/loop quando sono attivi (con effetto glow blu). */
    private final String STYLE_ACTIVE = "-fx-background-color: transparent; -fx-cursor: hand; " +
                                        "-fx-effect: dropshadow(gaussian, #0096ff, 15, 0.5, 0, 0);";

    /** Percorso radice delle risorse icone utilizzate nel player. */
    private String iconsRoot = "/icons/";

    /** Istanza del player logico che gestisce la riproduzione audio. */
    private Player player;

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * Registra il listener sul rilascio del mouse sullo slider di avanzamento per consentire
     * all'utente di spostarsi manualmente nella traccia.
     *
     * @param url            l'URL utilizzato per risolvere i percorsi relativi dell'oggetto root, o {@code null}
     * @param rb             le risorse utilizzate per localizzare l'oggetto root, o {@code null}
     */
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
     * Crea una nuova istanza di {@link Player}, imposta i callback per gli eventi di cambio traccia,
     * tick del timer e aggiornamenti UI di play/pausa, e avvia la riproduzione.
     *
     * @param initialTrack    la traccia da riprodurre inizialmente
     * @param trackCollection la playlist o tracklist contenente la traccia
     */
    public void init(Track initialTrack, TrackCollection trackCollection) {
        if (contextLabel != null && trackCollection != null) {
            contextLabel.setText("Riproducendo da: " + trackCollection.getName());
        }
        updateTrackUI(initialTrack);
        if (player != null) {
            player.terminate();
        }
        player = new Player(new Play(), trackCollection, initialTrack);

        player.setOnTrackChanged(() -> {
            Platform.runLater(() -> {
                updateTrackUI(player.getCurrentTrack());
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

    /**
     * Gestisce il click sul pulsante play/pausa.
     * Delega al player logico la transizione di stato tra play e pausa.
     */
    @FXML
    public void handleExecute() {
        if (player != null) {
            player.changeState();
        }
    }

    /**
     * Gestisce il click sul pulsante di salto alla traccia successiva.
     * Non esegue alcuna azione se il player non è stato inizializzato.
     */
    @FXML
    public void handleNext() {
        if (player == null) return;
        player.nextTrack();
    }

    /**
     * Gestisce il click sul pulsante di ritorno alla traccia precedente.
     * Non esegue alcuna azione se il player non è stato inizializzato.
     */
    @FXML
    public void handlePrevious() {
        if (player != null) {
            player.previousTrack();
        }
    }

    /**
     * Gestisce il click sul pulsante di chiusura del mini-player.
     * Termina la riproduzione corrente e notifica l'{@link ActivePlayerManager} della chiusura.
     */
    @FXML
    public void handleClose() {
        if (player != null) {
            player.terminate();
        }
        ActivePlayerManager.getInstance().closePlayer();
    }

    /**
     * Aggiorna i componenti dell'interfaccia grafica con le informazioni della traccia fornita.
     * Effettua il binding delle proprietà titolo e autore, aggiorna lo slider di avanzamento
     * e gestisce la visualizzazione della copertina (o dell'icona di default se assente).
     *
     * @param track la traccia di cui visualizzare le informazioni; se {@code null} non viene eseguita alcuna operazione
     */
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

            if (track.getCoverImage() != null && !track.getCoverImage().trim().isEmpty()) {
                try {
                    File file = new File(track.getCoverImage());
                    if (file.exists()) {
                        playerCoverImageView.setImage(new Image(file.toURI().toString()));
                    } else {
                        setDefaultPlayerIcon();
                    }
                } catch (Exception e) {
                    setDefaultPlayerIcon();
                }
            } else {
                setDefaultPlayerIcon();
            }
        }
    }

    /**
     * Ripristina l'icona di default (nota musicale) se la traccia non ha una copertina.
     * In caso di errore nel caricamento dell'immagine, stampa un messaggio su {@code System.err}.
     */
    private void setDefaultPlayerIcon() {
        try {
            playerCoverImageView.setImage(new Image(getClass().getResourceAsStream(iconsRoot + "musical-note.png")));
        } catch (Exception e) {
            System.err.println("Immagine di default del player non trovata.");
        }
    }

    /**
     * Imposta l'immagine di un pulsante generico tramite una risorsa specificata dal percorso.
     * In caso di errore nel caricamento dell'immagine, stampa un messaggio su {@code System.err}.
     *
     * @param targetButton il pulsante su cui impostare l'immagine
     * @param resourcePath il percorso della risorsa immagine (relativo al classpath)
     * @param width        la larghezza desiderata per l'immagine, in pixel
     * @param height       l'altezza desiderata per l'immagine, in pixel
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
     * Imposta l'immagine del pulsante play/pausa ({@code executeButton}).
     * Delega a {@link #setButtonImage(Button, String, double, double)}.
     *
     * @param resourcePath il percorso della risorsa immagine (relativo al classpath)
     * @param width        la larghezza desiderata per l'immagine, in pixel
     * @param height       l'altezza desiderata per l'immagine, in pixel
     */
    public void setExecuteButtonImage(String resourcePath, double width, double height) {
        setButtonImage(this.executeButton, resourcePath, width, height);
    }

    /**
     * Formatta un numero di secondi totali nella rappresentazione {@code m:ss}.
     *
     * @param totalSeconds il numero totale di secondi da formattare
     * @return una stringa nel formato {@code m:ss} (es. {@code "3:05"})
     */
    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d", m, s);
    }

    /**
     * Restituisce l'istanza corrente del player logico.
     *
     * @return il {@link Player} associato a questo controller, o {@code null} se non ancora inizializzato
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gestisce il click sul pulsante shuffle.
     * Attiva la strategia {@link org.unisa.musicplaylistmanager.strategy.Shuffle} se lo shuffle era inattivo,
     * oppure ripristina la strategia {@link org.unisa.musicplaylistmanager.strategy.Sequential} se era attivo.
     * Disattiva automaticamente il loop se lo shuffle viene abilitato.
     * Aggiorna lo stile visivo dei pulsanti tramite {@link #updateStrategyUI()}.
     */
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

    /**
     * Gestisce il click sul pulsante loop.
     * Attiva la strategia {@link org.unisa.musicplaylistmanager.strategy.Loop} se il loop era inattivo,
     * oppure ripristina la strategia {@link org.unisa.musicplaylistmanager.strategy.Sequential} se era attivo.
     * Disattiva automaticamente lo shuffle se il loop viene abilitato.
     * Aggiorna lo stile visivo dei pulsanti tramite {@link #updateStrategyUI()}.
     */
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

    /**
     * Aggiorna lo stile visivo dei pulsanti shuffle e loop in base alle modalità attualmente attive.
     * Applica {@code STYLE_ACTIVE} al pulsante della modalità attiva e {@code STYLE_NORMAL} all'altro.
     */
    private void updateStrategyUI() {
        shuffleButton.setStyle(isShuffleActive ? STYLE_ACTIVE : STYLE_NORMAL);
        loopButton.setStyle(isLoopActive ? STYLE_ACTIVE : STYLE_NORMAL);
    }
}