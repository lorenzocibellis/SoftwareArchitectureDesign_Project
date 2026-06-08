package org.unisa.musicplaylistmanager.command;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.track.Track;

import java.util.ArrayList;

/**
 *
 * Classe astratta che definisce i comportamenti base dei comandi che lavorano con le tracce.
 *
 */
public abstract class BaseTrackCommands extends BaseCommands{

    //ATTRIBUTI
    // Permette di lavorare con più tracce contemporaneamente
    private ArrayList<Track> tracks;
    // Indica la collezione da cui provengono le tracce
    private TrackCollection trackCollection;


    //METODI

    /**
     *
     * Metodo che permette di ottenere le tracce memorizzate.
     *
     * @return Lista di tracce con cui lavorare
     */
    protected ArrayList<Track> getTracks() {
        return tracks;
    }

    /**
     *
     * Metodo che permette di settare la lista di tracce con cui lavorare.
     *
     * @param tracks Tracce da memorizzare.
     */
    protected void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    /**
     *
     * Metodo che permette di ottenere la collezione di tracce a cui appartengono, o appartenevano, le tracce
     * memorizzate.
     *
     * @return La collezione di tracce a cui appartengono le tracce.
     */
    protected TrackCollection getTrackCollection() {
        return trackCollection;
    }

    /**
     *
     * Metodo che permette di settare la collezione di tracce a cui appartengono, o appartenevano, le tracce.
     *
     * @param trackCollection La collezione di tracce a cui appartengono le tracce.
     */
    protected void setTrackCollection(TrackCollection trackCollection) {
        this.trackCollection = trackCollection;
    }
}
