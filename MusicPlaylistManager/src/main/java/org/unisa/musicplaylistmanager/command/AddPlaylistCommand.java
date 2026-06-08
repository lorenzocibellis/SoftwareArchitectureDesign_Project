package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;

/**
 *
 * Classe che implementa il comando di aggiunta di una playlist, e le eventuali operazioni per permettere
 * l'annullamento di tale operazione
 *
 */
public class AddPlaylistCommand extends BasePlaylistCommands{

    /**
     * Costruttore
     *
     * @param p Playlist da aggiungere alla lista di Playlist.
     * @param pl Lista di playlist a cui aggiungere la playlist.
     * @param o Lista osservabile di playlist da aggiornare.
     *
     */
    public AddPlaylistCommand(Playlist p, PlaylistList pl, ObservableList<Playlist> o){
        setPlaylists(new ArrayList<Playlist>());
        getPlaylists().add(p);
        setPlaylistList(pl);
        setObservableList(o);
    }

    /**
     *
     * metodo che permette l'annullamento dell'operazione di aggiunta di una playlist
     *
     * @throws IllegalArgumentException nel caso la playlist di cui annullare l'aggiunta non esista
     *
     */
    @Override
    public void undo() {
        // ottengo il riferimento alla lista di playlist
        ArrayList<Playlist> playlists = getPlaylists();
        // controllo se il riferimento è null
        if(playlists == null)
            // se lo è lancio un'eccezione
            throw new IllegalArgumentException();

        // ottengo la playlist di cui annullare l'aggiunta
        Playlist playlist = playlists.get(0);
        // controllo che il riferimento non sia null
        if (playlist == null)
            // se lo è lancio un'eccezione
            throw new IllegalArgumentException();

        // Pattern Observer: detach observer della playlist prima di rimuoverla
        TrackList tl = TrackList.getTrackListPointer();
        tl.detach(playlist);


        // ottengo il riferimento alla lista di playlist
        PlaylistList playlistList = getPlaylistList();
        // se il riferimento alla lista di playlist non è null, rimuovo la playlist dalla lista
        if (playlistList != null)
            getPlaylistList().deletePlaylist(playlist);

        ObservableList<Playlist> obsList = getObservableList();
        // se il riferimento alla lista osservabile non è null, rimuovo la playlist dalla lista
        if (obsList != null)
            getObservableList().remove(playlist);
    }


    /**
     *
     * Metodo che permette l'aggiunta di una playlist aalla lista delle playlist, aggiornando al contempo la lista
     * visuale di playlist.
     *
     * @throws IllegalArgumentException nel caso in cui la playlist da aggiungere non esista.
     *
     */
    @Override
    public void execute() {

        // Controllo dell'esistenza della lista di playlist da aggiungere
        ArrayList<Playlist> playlists = getPlaylists();

        // controllo che il riferimento non sia null
        if(playlists == null)
            // se lo è lancio un'eccezione
            throw new IllegalArgumentException();

        // ottengo la playlist da aggiungere
        Playlist playlist = playlists.get(0);
        // controllo che il riferimento non sia null
        if (playlist == null)
            // se lo è lancio un'eccezione
            throw new IllegalArgumentException();


        // Pattern Observer: attach observer della playlist prima di aggiungerla
        TrackList tl = TrackList.getTrackListPointer();
        tl.attach(playlist);

        // ottengo il riferimento alla lista di playlist
        PlaylistList playlistList = getPlaylistList();
        // se il riferimento alla lista di playlist non è null, aggiungo la playlist alla lista
        if (playlistList != null)
            getPlaylistList().addPlaylist(playlist);

        // ottengo il riferimento alla lista osservabile
        ObservableList<Playlist> obsList = getObservableList();
        // se il riferimento alla lista osservabile non è null, aggiungo la playlist alla lista osservabile
        if (obsList != null)
            getObservableList().add(playlist);
    }
}
