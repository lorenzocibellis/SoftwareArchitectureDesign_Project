package org.unisa.musicplaylistmanager.service.player;

import org.unisa.musicplaylistmanager.track.list.TrackCollection;
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
     * @param trackCollection la playlist (o tracklist) di contesto
     */
    void openPlayer(Track track, TrackCollection trackCollection);

    /**
     * Chiude il mini-player e interrompe la riproduzione.
     */
    void closePlayer();

    /**
     * Restituisce la traccia attualmente in riproduzione, o null se il player è chiuso.
     */
    Track getCurrentTrack();

    /**
     * Restituisce l'identificatore della playlist (o della tracklist principale) attualmente in uso nel player,
     * ovvero il contesto da cui è stata avviata la traccia.
     *
     */
    String getCurrentPlaylistIdentifier();

    /**
     * Indica se il mini-player è aperto e visibile.
     */
    boolean hasActivePlayer();
}
