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
     * metodo che permette l'annullamento delle operazioni di aggiunta di una playlist
     *
     */
    @Override
    public void undo() {
        ArrayList<Playlist> playlists = getPlaylists();
        if(playlists == null)
            throw new IllegalArgumentException();
        Playlist playlist = playlists.get(0);
        if (playlist == null)
            throw new IllegalArgumentException();

        // Pattern Observer: detach observer della playlist prima di rimuoverla
        TrackList tl = TrackList.getTrackListPointer();
        tl.detach(playlist);

        PlaylistList playlistList = getPlaylistList();
        if (playlistList != null)
            getPlaylistList().deletePlaylist(playlist);

        ObservableList<Playlist> obsList = getObservableList();
        if (obsList != null)
            getObservableList().remove(playlist);
    }

    @Override
    public void execute() {

        // Controllo esistenza lista di playlist e playlist da aggiungere
        ArrayList<Playlist> playlists = getPlaylists();
        if(playlists == null)
            throw new IllegalArgumentException();
        Playlist playlist = playlists.get(0);
        if (playlist == null)
            throw new IllegalArgumentException();


        // Pattern Observer: attach observer della playlist prima di aggiungerla
        TrackList tl = TrackList.getTrackListPointer();
        tl.attach(playlist);

        PlaylistList playlistList = getPlaylistList();
        if (playlistList != null)
            getPlaylistList().addPlaylist(playlist);

        ObservableList<Playlist> obsList = getObservableList();
        if (obsList != null)
            getObservableList().add(playlist);
    }
}
