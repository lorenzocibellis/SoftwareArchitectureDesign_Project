package org.unisa.musicplaylistmanager.command;

import org.unisa.musicplaylistmanager.playlist.PlaylistList;

public abstract class BasePlaylistCommands extends BaseCommands{
    //ATTRIBUTI
    //Permette di lavorare con la lista di Playlist
    protected PlaylistList playlistList;

    protected PlaylistList getPlaylistList() {
        return playlistList;
    }

    protected void setPlaylistList(PlaylistList playlistList) {
        this.playlistList = playlistList;
    }
}
