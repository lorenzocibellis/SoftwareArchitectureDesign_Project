package org.unisa.musicplaylistmanager.playlist;

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
        if (p != null && !playlistList.contains(p)) {
            playlistList.add(p);
            return;
        }
        throw new IllegalArgumentException();
    }

    //metodo per eliminazione playlist
    public void deletePlaylist(Playlist p){
        if (p != null) {
            p.deleteAll();
            playlistList.remove(p);
        }
    }

    //Metodo per eliminazione di più playlist in contemporanea
    public void deletePlaylists(ArrayList<Playlist> pl){
        for(int i = 0; i < pl.size(); i++){
            Playlist p = pl.get(i);
            if (p != null){
                p.deleteAll();
                playlistList.remove(p);
            }
        }
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
