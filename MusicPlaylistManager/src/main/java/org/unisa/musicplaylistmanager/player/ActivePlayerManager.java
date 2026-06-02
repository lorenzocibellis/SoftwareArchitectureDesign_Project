package org.unisa.musicplaylistmanager.player;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

import java.io.IOException;

/**
 * Singleton responsabile esclusivamente del ciclo di vita del mini-player.
 *
 * Delega a {@link NavigationManager} il compito di aggiungere o rimuovere l'overlay del player
 * dall'interfaccia utente.
 *
 * Funzionalità principali:
 *
 *  Usa {@code NavigationManager} per aggiungere/rimuovere il mini-player dallo StackPane radice.
 *  Usa {@link PlayerController} per caricare e inizializzare la vista FXML del mini-player.
 *  Usa {@link Player} (tramite {@code PlayerController}) per gestire lo stato della riproduzione e accedere alla traccia/playlist corrente.
 *
 *
 * @author gruppo10
 */
public class ActivePlayerManager implements PlayerManager {

    private static final ActivePlayerManager instance = new ActivePlayerManager();

    private BorderPane miniPlayerBar;
    private PlayerController playerController;

    private final String resourceRoot = "/org/unisa/musicplaylistmanager/";

    private ActivePlayerManager() {}

    /**
     * Restituisce l'istanza singleton di {@code ActivePlayerManager}.
     * 
     * @return l'unica istanza di questa classe
     */
    public static ActivePlayerManager getInstance() {
        return instance;
    }

    /**
     * Carica il mini-player FXML, lo inizializza con la traccia e la playlist (o tracklist),
     * e lo aggiunge come overlay superiore nello StackPane radice.
     * 
     * @param track la traccia da avviare
     * @param playlist la playlist da cui è stata avviata la traccia (o tracklist)
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
     * Restituisce la traccia attualmente in riproduzione.
     * 
     * @return la traccia corrente, o {@code null} se il player è chiuso
     */
    @Override
    public Track getCurrentTrack() {
        if (playerController != null && playerController.getPlayer() != null) {
            return playerController.getPlayer().getCurrentTrack();
        }
        return null;
    }

    /**
     * Restituisce la playlist (o la tracklist principale) attualmente in uso nel player.
     * 
     * @return la playlist/tracklist corrente, o {@code null} se il player è chiuso
     */
    @Override
    public Playlist getCurrentPlaylist() {
        if (playerController != null && playerController.getPlayer() != null) {
            return playerController.getPlayer().getCurrentPlaylist();
        }
        return null;
    }

    /**
     * Indica se il mini-player è attualmente aperto e visibile sullo schermo.
     * 
     * @return {@code true} se il player è attivo, {@code false} altrimenti
     */
    @Override
    public boolean hasActivePlayer() {
        return miniPlayerBar != null;
    }
}
