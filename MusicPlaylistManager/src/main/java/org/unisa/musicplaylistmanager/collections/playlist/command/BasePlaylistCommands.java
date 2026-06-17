package org.unisa.musicplaylistmanager.collections.playlist.command;

import org.unisa.musicplaylistmanager.core.command.BaseCommands;
import org.unisa.musicplaylistmanager.collections.playlist.model.Playlist;
import org.unisa.musicplaylistmanager.collections.playlist.model.PlaylistList;

import java.util.ArrayList;

/**
 *
 * Classe astratta che definisce i comportamenti base dei comandi che lavorano con le playlist.
 *
 */
public abstract class BasePlaylistCommands extends BaseCommands {
    // ATTRIBUTI
    // Permette di lavorare con la lista che contiene le Playlist
    private PlaylistList playlistList;
    // Permette di lavorare con più playlist contemporaneamente
    private ArrayList<Playlist> playlists;

    /**
     *
     * Metodo che permette di ottenere la lista che contiene le playlist.
     *
     * @return La lista contenente le playlist.
     */
    protected PlaylistList getPlaylistList() {
        return playlistList;
    }

    /**
     *
     * Metodo che permette di settare la lista contenente le playlist.
     *
     * @param playlistList Lista di playlist da settare.
     */
    protected void setPlaylistList(PlaylistList playlistList) {
        this.playlistList = playlistList;
    }

    /**
     *
     * Metodo che permette di ottenere la lista di playlist.
     *
     * @return La lista di playlist con cui lavorare.
     *
     */
    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     *
     * Metodo che permette di settare la lista di playlist.
     *
     * @param playlists Lista di playlist da settare.
     */
    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
}
