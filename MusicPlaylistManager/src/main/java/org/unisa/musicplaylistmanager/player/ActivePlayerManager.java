package org.unisa.musicplaylistmanager.player;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

import java.io.IOException;

/**
 * Singleton responsabile esclusivamente del ciclo di vita del mini-player.
 *
 * Delega a NavigationManager per aggiungere/rimuovere l'overlay del player.
 *
 *
 *  - usa NavigationManager per aggiungere/rimuovere il mini-player dallo StackPane radice
 *  - usa PlayerController per inizializzare l'FXML del mini-player
 *  - usa Player (tramite PlayerController) per accedere alla traccia/playlist corrente
 *
 * @author gruppo10
 */
public class ActivePlayerManager implements PlayerManager {

    private static final ActivePlayerManager instance = new ActivePlayerManager();

    private BorderPane miniPlayerBar;
    private PlayerController playerController;

    private final String resourceRoot = "/org/unisa/musicplaylistmanager/";

    private ActivePlayerManager() {}

    public static ActivePlayerManager getInstance() {
        return instance;
    }

    /**
     * Carica il mini-player FXML, lo inizializza con la traccia e la playlist,
     * e lo aggiunge come overlay superiore nello StackPane radice.
     */
    @Override
    public void openPlayer(Track track, Playlist playlist) {
        // Termina eventuale riproduzione precedente
        if (playerController != null && playerController.getPlayer() != null) {
            playerController.getPlayer().terminate();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "MiniPlayerView.fxml"));
            BorderPane newBar = loader.load();
            playerController = loader.getController();
            playerController.init(track, playlist);

            // Rimuove il vecchio mini-player dallo StackPane (se presente)
            if (miniPlayerBar != null) {
                NavigationManager.getInstance().getRootLayout().getChildren().remove(miniPlayerBar);
            }

            miniPlayerBar = newBar;
            // Aggiunge il mini-player sopra il contenuto corrente
            NavigationManager.getInstance().getRootLayout().getChildren().add(miniPlayerBar);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interrompe la riproduzione e rimuove il mini-player dallo schermo.
     */
    @Override
    public void closePlayer() {
        if (playerController != null && playerController.getPlayer() != null) {
            playerController.getPlayer().terminate();
        }
        playerController = null;

        if (miniPlayerBar != null) {
            NavigationManager.getInstance().getRootLayout().getChildren().remove(miniPlayerBar);
            miniPlayerBar = null;
        }
    }

    /**
     * Restituisce la traccia attualmente in riproduzione, o null se il player è chiuso.
     */
    @Override
    public Track getCurrentTrack() {
        if (playerController != null && playerController.getPlayer() != null) {
            return playerController.getPlayer().getCurrentTrack();
        }
        return null;
    }

    /**
     * Restituisce la playlist attualmente in uso nel player, o null se il player è chiuso.
     */
    @Override
    public Playlist getCurrentPlaylist() {
        if (playerController != null && playerController.getPlayer() != null) {
            return playerController.getPlayer().getCurrentPlaylist();
        }
        return null;
    }

    /**
     * Indica se il mini-player è aperto e visibile.
     */
    @Override
    public boolean hasActivePlayer() {
        return miniPlayerBar != null;
    }
}
