package org.unisa.musicplaylistmanager;

import java.util.ArrayList;

public class PlaylistList {

    //Dichiarazione Attributi
    private ArrayList<Playlist> playlistList;

    //attributo per pattern Singleton
    private static PlaylistList pnt;

    //METODI
    //Costruttore
    public PlaylistList(){
        playlistList = new ArrayList<Playlist>();
        pnt = this;
    }
    //getter
    public ArrayList<Playlist> getPlaylists(){
        return this.playlistList;
    }
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

    //metodo per controllo esistenza di playlistList
    public static boolean exists(){
        return !(pnt == null);
    }

    //metodo per ottenimento puntatore a istanza di PlaylistList
    public static PlaylistList getPlaylistListPointer(){
        return pnt;
    }
}
