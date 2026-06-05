package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;

/**
 *
 * Classe che implementa il comando di aggiunta di una playlist, e le eventuali operazioni per permettere
 * l'annullamento di tali operazioni
 *
 */
public class AddPlaylistCommand extends BasePlaylistCommands{

    /**
     * Costruttore
     *
     * @param p Playlist da aggiungere alla lista di Playlist
     */
    public AddPlaylistCommand(Playlist p, PlaylistList pl, ObservableList<Playlist> o){
        setPlaylist(p);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute() {
        Playlist p = getPlaylist();
        getPlaylistList().addPlaylist(p);
        getObservableList().add(p);
    }
}
