package org.unisa.musicplaylistmanager.player;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

/**
 * Contratto per il gestore del ciclo di vita del mini-player.
 *
 *
 * @author gruppo10
 */
public interface PlayerManager {

    /**
     * Apre il mini-player e avvia la riproduzione della traccia specificata.
     *
     * @param track    la traccia da riprodurre
     * @param playlist la playlist (o tracklist) di contesto
     */
    void openPlayer(Track track, Playlist playlist);

    /**
     * Chiude il mini-player e interrompe la riproduzione.
     */
    void closePlayer();

    /**
     * Restituisce la traccia attualmente in riproduzione, o null se il player è chiuso.
     */
    Track getCurrentTrack();

    /**
     * Restituisce la playlist attualmente in uso nel player, o null se il player è chiuso.
     *
     */
    Playlist getCurrentPlaylist();

    /**
     * Indica se il mini-player è aperto e visibile.
     */
    boolean hasActivePlayer();
}
