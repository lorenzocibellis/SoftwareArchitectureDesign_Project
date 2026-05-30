package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML private Button executeButton;
    @FXML private Button skipToNextButton;
    @FXML private Button skipToPreviousButton;
    @FXML private Slider songProgress;
    @FXML private Label duration;
    @FXML private Label counter;
    @FXML private Text trackTitle;
    @FXML private Text authorName;

    @FXML private Pane topPane;
    @FXML private Pane bottomPane;
    @FXML private Button expandButton;
    @FXML private Button closeButtonMin;

    private Player player;
    private AnchorPane playerRoot; 
    private boolean isMinimized = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // disabilita TEMPORANEAMENTE i tasti di skip 
        skipToNextButton.setDisable(true);
        skipToPreviousButton.setDisable(true);
        // Carichiamo le icone per i tasti di skip
        setButtonImage(skipToNextButton, "/icons/skipNextButton.png", 37, 37);
        setButtonImage(skipToPreviousButton, "/icons/skipPreviousButton.png", 37, 37);
    }

    // Collega l'interfaccia grafica del player per gestirne le animazioni (su e giù)
    public void setPlayerRoot(AnchorPane root) {
        this.playerRoot = root;
    }
    
 // metodo che inizializza il contesto di riproduzione per una nuova traccia e termina l'eventuale ruproduzione precedente
    public void setPlaylistContext(Track initialTrack, Playlist playlist) {
        updateTrackUI(initialTrack); // aggiorna la GUI in tempo reale
// Interrompe la riproduzione attiva, se presente, prima di procedere.
        if (player != null) {
            player.terminate();
        }
// istanzia un oggetto di tipo player
        player = new Player(new Play(), playlist, initialTrack);
// aggiorna in tempo reale i secondi di riproduzione nell'etichetta a sinsitra della traccia
        player.setOnTimeTick(seconds -> {
            Platform.runLater(() -> {
                songProgress.setValue(seconds);
                counter.setText(formatTime(seconds));
            });
        });
// se la traccia è in play, il tasto centrale diventa un tasto di pausa
        player.setOnPlayUIUpdate(() -> {
            Platform.runLater(() -> setExecuteButtonImage("/icons/pauseButton.png", 37, 37)); 
        });
// se la traccia è in pausa, il tasto centrale diventa un tasto di play
        player.setOnPauseUIUpdate(() -> {
            Platform.runLater(() -> setExecuteButtonImage("/icons/playButton.jpg", 24, 24)); 
        });
// aggiorna lo stato di riproduzione
        player.changeState(); 
        
    }
// listener del tasto centrale di play/pause, che esegue una determinata azione in base allo stato di riproduzione della traccia
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
// metodo che gestisce il passaggio alla riproduzione della traccia successiva
    @FXML
    public void handlePrevious() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
// funzione che gestisce il toggle della finestra del player, che in base a un tasto può estendersi
    //occupando tutta la schermata, o rimanere ancorata in basso
    @FXML
    public void toggleMinimize() {
        // Calcola in modo dinamico e matematico lo spazio esatto da nascondere
        double distanceToHide = topPane.getHeight(); 

        TranslateTransition slideRoot = new TranslateTransition(Duration.seconds(0.4), playerRoot);
        TranslateTransition slideBottom = new TranslateTransition(Duration.seconds(0.4), bottomPane);
// caso in cui il player occupa l'intera schermata e vogliamo abbassarlo
        if (!isMinimized) {
            slideRoot.setToY(distanceToHide);
            // Salendo, il bottomPane controbilancia e resta immobile
            slideBottom.setToY(-distanceToHide);
            
            expandButton.setVisible(true);
            closeButtonMin.setVisible(true);
            isMinimized = true;
        } else { // viceversa, se è abbassato e vogliamo espanderlo
            slideRoot.setToY(0.0);
            slideBottom.setToY(0.0);
            
            expandButton.setVisible(false);
            closeButtonMin.setVisible(false);
            isMinimized = false;
        }
        
        slideRoot.play();
        slideBottom.play();
    }
// metodo che gestisce la chiusura della finestra del player
    @FXML
    public void handleClose() {
        if (player != null) {
            player.terminate(); 
        }
        
        TranslateTransition slideDown = new TranslateTransition(Duration.seconds(0.4), playerRoot);
        // Scivola giù anche in schermi molto grandi
        slideDown.setToY(playerRoot.getHeight() + 200.0); 
        
        slideDown.setOnFinished(e -> {
            if (playerRoot.getParent() instanceof StackPane) {
                ((StackPane) playerRoot.getParent()).getChildren().remove(playerRoot);
            }
        });
        
        slideDown.play();
    }
// metodo di aggiornamento dell'interfaccia del player in tempo reale
    private void updateTrackUI(Track track) {
        if (track != null) {
            trackTitle.setText(track.getTitle());
            authorName.setText(track.getAuthor());

            songProgress.setMin(0);
            songProgress.setMax(track.getSeconds());
            songProgress.setValue(0);
            duration.setText(formatTime(track.getSeconds()));
            counter.setText("0:00");
        }
    }
// metodo che cambia l'icona del tasto centra in base allo stato della traccia
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
// overload del metodo precedente, che consente di adattare dinamicamente le dimensioni delle icone dei tasti
    public void setExecuteButtonImage(String resourcePath, double width, double height) {
        setButtonImage(this.executeButton, resourcePath, width, height);
    }
// metodo di formattazione della duratain secondi    
    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d", m, s);
    }
}