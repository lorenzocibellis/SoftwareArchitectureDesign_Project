package org.unisa.musicplaylistmanager.player.service;

import javafx.fxml.FXMLLoader;
import org.unisa.musicplaylistmanager.player.model.Player;
import org.unisa.musicplaylistmanager.player.controller.PlayerController;
import org.unisa.musicplaylistmanager.collections.TrackCollection;
import org.unisa.musicplaylistmanager.core.navigation.NavigationManager;
import org.unisa.musicplaylistmanager.track.model.Track;

import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.AnchorPane;
import org.unisa.musicplaylistmanager.collections.playlist.model.PlaylistList;
import org.unisa.musicplaylistmanager.collections.playlist.model.Playlist;
import org.unisa.musicplaylistmanager.collections.tracklist.model.TrackList;

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

    private AnchorPane miniPlayerBar;
    private PlayerController playerController;
    
    // Proprietà osservabile che indica se il mini-player è attualmente attivo/visibile
    private final BooleanProperty playerActive = new SimpleBooleanProperty(false);

    // Proprietà osservabile che contiene la traccia attualmente in riproduzione nel player
    private final ObjectProperty<Track> currentTrack = new SimpleObjectProperty<>(null);

    private final String resourceRoot = "/org/unisa/musicplaylistmanager/player/";

    /**
     * Costruttore privato di ActivePlayerManager.
     */
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
     * @param trackCollection la playlist da cui è stata avviata la traccia (o tracklist)
     */
    @Override
    public void openPlayer(Track track, TrackCollection trackCollection) {
        // Termina eventuale riproduzione precedente
        if (playerController != null && playerController.getPlayer() != null) {
            playerController.getPlayer().terminate();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "MiniPlayerView.fxml"));
            javafx.scene.layout.AnchorPane newBar = loader.load();
            playerController = loader.getController();
            playerController.init(track, trackCollection);

            // Rimuove il vecchio mini-player dallo StackPane (se presente)
            if (miniPlayerBar != null) {
                NavigationManager.getInstance().getRootLayout().getChildren().remove(miniPlayerBar);
            }

            miniPlayerBar = newBar;
            // Aggiunge il mini-player sopra il contenuto corrente
            NavigationManager.getInstance().getRootLayout().getChildren().add(miniPlayerBar);
            playerActive.set(true);
            currentTrack.set(track);

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
        playerActive.set(false);
        currentTrack.set(null);
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
    * Restituisce l'identificatore della collezione (Playlist o TrackList) attualmente in uso nel player.
    *
     * @return l'identificatore della collezione corrente, o {@code null} se il player è chiuso
    */
    @Override
    public String getCurrentPlaylistIdentifier() {
        if (playerController != null && playerController.getPlayer() != null) {
            return playerController.getPlayer().getCurrentPlaylistIdentifier();
        }
        return null;
    }

    /**
     * Attiva o disattiva la modalità casuale (shuffle) sul player corrente.
     */
    public void toggleShuffle() {
        if (playerController != null) {
            playerController.handleShuffle();
        }
    }

    /**
     * Valida lo stato del player, verificando che la traccia e la playlist in riproduzione
     * esistano ancora globalmente (es. dopo un undo). Se non esistono, chiude il player.
     */
    public void validatePlayerState() {
        if (!playerActive.get()) return;
        
        Track playingTrack = currentTrack.get();
        if (playingTrack == null) {
            // Se la traccia corrente è diventata nulla ma il player è attivo, 
            // significa che l'iteratore è rimasto senza tracce (es. playlist svuotata da undo).
            closePlayer();
            return;
        }

        String identifier = getCurrentPlaylistIdentifier();
        
        if (TrackList.TRACKLIST_NAME.equals(identifier)) {
            // Riproduzione dalla libreria principale
            if (!TrackList.getTrackListPointer().getTracks().contains(playingTrack)) {
                closePlayer();
            }
        } else if (identifier != null) {
            // Riproduzione da una playlist 
            PlaylistList playlistList = PlaylistList.getPlaylistListPointer();
            Playlist p = playlistList.getPlaylists().stream()
                .filter(pl -> pl.getName().equals(identifier))
                .findFirst().orElse(null);
                
            if (p == null || !p.getTracks().contains(playingTrack)) {
                closePlayer();
            }
        }
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
    
    /**
     * Restituisce l'altezza del player.
     * 
     * @return l'altezza del player
     */
    public double getPlayerHeight() {
        if (hasActivePlayer()) {
            double height = miniPlayerBar.getPrefHeight();
            return height > 0 ? height : miniPlayerBar.getHeight();
        }
        return 0.0;
    }

    /**
     * Ritorna la proprietà di sola lettura per osservare lo stato di attivazione/visibilità del player.
     * I controller delle liste usano questa proprietà per aggiornare dinamicamente il padding inferiore delle ListView.
     * 
     * @return la proprietà osservabile dello stato del player
     */
    public ReadOnlyBooleanProperty playerActiveProperty() {
        return playerActive;
    }

    /**
     * Imposta la traccia corrente attualmente in riproduzione nel player.
     * Questo metodo viene chiamato dal PlayerController ad ogni cambio traccia o avvio.
     *
     * @param track la traccia attualmente attiva
     */
    public void setCurrentTrack(Track track) {
        this.currentTrack.set(track);
    }

    /**
     * Ritorna la proprietà osservabile in sola lettura contenente la traccia attualmente riprodotta.
     * I controller della lista si collegano a questa proprietà per aggiornare la grafica della canzone e mostrarla
     * attualmente in riproduzione
     * 
     * @return la proprietà della traccia corrente
     */
    public ReadOnlyObjectProperty<Track> currentTrackProperty() {
        return currentTrack;
    }
}
