package org.unisa.musicplaylistmanager.playlist;

import java.util.ArrayList;

/**
 * Rappresenta la collezione globale di tutte le playlist dell'applicazione.
 * Implementa un pattern Singleton per garantire un unico punto di accesso
 * centralizzato alla lista delle playlist.
 *
 * @author gruppo10
 */
public class PlaylistList {

    //Dichiarazione Attributi
    private ArrayList<Playlist> playlistList;

    //attributo per pattern Singleton
    private static PlaylistList pnt;

    //METODI
    /**
     * Costruisce la collezione vuota di playlist e inizializza il puntatore Singleton.
     * Viene richiamato una sola volta durante l'avvio dell'applicazione.
     */
    private PlaylistList(){
        playlistList = new ArrayList<Playlist>();
        pnt = this;
    }

    /**
     * Restituisce la lista di tutte le playlist salvate.
     * 
     * @return un {@link ArrayList} contenente le playlist
     */
    public ArrayList<Playlist> getPlaylists(){
        return this.playlistList;
    }

    /**
     * Aggiunge una nuova playlist alla collezione.
     * 
     * @param p la playlist da aggiungere
     * @throws IllegalArgumentException se la playlist è nulla o è già presente
     */
    public void addPlaylist(Playlist p){
        if (p != null && !playlistList.contains(p)) {
            playlistList.add(p);
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Elimina una singola playlist dalla collezione.
     * Prima di rimuoverla, svuota la playlist chiamando il suo metodo {@code deleteAll()}.
     * 
     * @param p la playlist da eliminare
     */
    public void deletePlaylist(Playlist p){
        if (p != null) {
            p.deleteAll();
            playlistList.remove(p);
        }
    }

    /**
     * Elimina più playlist contemporaneamente dalla collezione.
     * Svuota ciascuna playlist prima di rimuoverla.
     * 
     * @param pl un {@link ArrayList} di playlist da eliminare
     */
    public void deletePlaylists(ArrayList<Playlist> pl){
        for(int i = 0; i < pl.size(); i++){
            Playlist p = pl.get(i);
            if (p != null){
                p.deleteAll();
                playlistList.remove(p);
            }
        }
    }

    /**
     * Verifica se l'istanza Singleton della lista di playlist è stata creata.
     * 
     * @return {@code true} se l'istanza esiste, {@code false} altrimenti
     */
    public static boolean exists(){
        return !(pnt == null);
    }

    /**
     * Restituisce il puntatore all'istanza Singleton di {@code PlaylistList}.
     * 
     * @return l'istanza di questa classe se esiste, altrimenti la crea e la restituisce.
     */
    public static PlaylistList getPlaylistListPointer(){
        if (exists()) return pnt;
        return new PlaylistList();
    }
}
