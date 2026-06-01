package org.unisa.musicplaylistmanager;

import java.util.ArrayList;

public class PlaylistList {

    //Dichiarazione Attributi
    private ArrayList<Playlist> playlistList;

    public void addPlaylist(Playlist playlist){
        if (playlist)
        playlistList.add(playlist)
    }
}
