package org.unisa.musicplaylistmanager;

import java.util.ArrayList;

public class PlaylistList {

    //Dichiarazione Attributi
    private ArrayList<Playlist> playlistList;

    //metodo per aggiunta playlist
    public void addPlaylist(Playlist p){
        if (p != null && !playlistList.contains(p))
            playlistList.add(p);
    }

    //metodo per eliminazione playlist
    public void deletePlaylist(Playlist p){
        if (p != null)
            playlistList.remove(p);
    }
}
