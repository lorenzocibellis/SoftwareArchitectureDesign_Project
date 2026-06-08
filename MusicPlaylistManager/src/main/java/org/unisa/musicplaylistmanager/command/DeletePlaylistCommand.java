package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;

/**
 * Classe che implementa il comando di rimozione di una playlist, e le eventuali operazioni per permettere
 * l'annullamento di tale operazione
 */
public class DeletePlaylistCommand extends BasePlaylistCommands{
    //METODI

    /**
     *
     * Costruttore
     *
     * @param ps Lista di Playlist da rimuovere dalla lista di Playlist.
     * @param pl Lista di playlist da cui elimianare le playlist.
     * @param o Lista osservabile di playlist da aggiornare.
     *
     */
    public DeletePlaylistCommand(ArrayList<Playlist> ps, PlaylistList pl, ObservableList<Playlist> o){
        setPlaylists(ps);
        setPlaylistList(pl);
        setObservableList(o);
    }

    /**
     * Metodo che permette l'annullamento dell'eliminazione di una playlist dalla lista delle playlist,
     * facendo tornare l'applicazione allo stato precedente all'eliminazione.
     */
    @Override
    public void undo() {

        ArrayList<Playlist> ps = getPlaylists();
        if (ps == null)
            throw new IllegalArgumentException();

        PlaylistList pl = getPlaylistList();
        ObservableList<Playlist> obsList = getObservableList();

        TrackList tl = TrackList.getTrackListPointer();
        for(Playlist p:ps) {
            // Pattern Observer: attach observer di ogni playlist prima ri-aggiungerla
            tl.attach(p);

            if (pl != null) {
                pl.addPlaylist(p);
            }
            if (obsList != null)
                obsList.add(p);

        }
    }

    /**
     *
     * Metodo che permette l'eliminazione di una playlist dalla lista delle playlist, aggiornando al contempo la lista
     * visuale di playlist.
     *
     */
    @Override
    public void execute() {
        ArrayList<Playlist> ps = getPlaylists();
        if (ps == null)
            throw new IllegalArgumentException();

        // Pattern Observer: detach observer di ogni playlist prima di eliminarla
        if(TrackList.exists()) {
            TrackList tl = TrackList.getTrackListPointer();
            for (Playlist p : ps)
               tl.detach(p);
        }

        PlaylistList pl = getPlaylistList();
        if (pl != null)
            pl.deletePlaylists(ps);

        ObservableList<Playlist> obsList = getObservableList();
        if (obsList != null)
            obsList.removeAll(ps);
    }
}
