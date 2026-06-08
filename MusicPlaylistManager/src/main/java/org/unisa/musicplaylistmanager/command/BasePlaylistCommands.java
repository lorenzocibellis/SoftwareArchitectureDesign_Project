package org.unisa.musicplaylistmanager.command;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;

import java.util.ArrayList;

public abstract class BasePlaylistCommands extends BaseCommands{
    //ATTRIBUTI
    //Permettono di lavorare con la lista di Playlist
    private PlaylistList playlistList;
    private ArrayList<Playlist> playlists;

    protected PlaylistList getPlaylistList() {
        return playlistList;
    }

    protected void setPlaylistList(PlaylistList playlistList) {
        this.playlistList = playlistList;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
}
