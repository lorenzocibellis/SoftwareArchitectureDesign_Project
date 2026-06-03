package org.unisa.musicplaylistmanager.service.player;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

/**
 * Interfaccia per il gestore del ciclo di vita del mini-player.
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
     * Restituisce la playlist (o la tracklist principale) attualmente in uso nel player,
     * ovvero il contesto da cui è stata avviata la traccia.
     *
     */
    Playlist getCurrentPlaylist();

    /**
     * Indica se il mini-player è aperto e visibile.
     */
    boolean hasActivePlayer();
}
