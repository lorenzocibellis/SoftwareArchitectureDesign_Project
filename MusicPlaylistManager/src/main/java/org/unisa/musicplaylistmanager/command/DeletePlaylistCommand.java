package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;

/**
 *
 * Classe che implementa il comando di rimozione di una playlist, e le eventuali operazioni per permettere
 * l'annullamento di tale operazione.
 *
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
     * Metodo che permette l'annullamento dell'eliminazione di una lista playlist dalla lista delle playlist,
     * facendo tornare l'applicazione allo stato precedente all'eliminazione.
     *
     * @throws IllegalArgumentException nel caso in cui la lista di playlist eliminate non esista.
     *
     */
    @Override
    public void undo() {

        // ottengo la lista di playlist su cui è stata effettuta l'operazione di eliminazione
        ArrayList<Playlist> ps = getPlaylists();
        // controllo che il riferimento non sia nullo
        if (ps == null)
            // se lo è lancio un'eccezione
            throw new IllegalArgumentException();

        // ottengo il riferimento alla lista di playlist e alla lista osservabile di playlist
        PlaylistList pl = getPlaylistList();
        ObservableList<Playlist> obsList = getObservableList();

        // ottengo il riferimento alla lista principale di tracce
        TrackList tl = TrackList.getTrackListPointer();

        // per ogni playlist eliminata:
        for(Playlist p:ps) {
            // Pattern Observer: attach observer di ogni playlist prima ri-aggiungerla
            tl.attach(p);

            // se il riferimento alla lista di playlist non è null, aggiungo la playlist alla lista
            if (pl != null) {
                pl.addPlaylist(p);
            }

            // se il riferimento alla lista osservabile non è null, aggiungo la playlist alla lista
            if (obsList != null)
                obsList.add(p);
        }
    }

    /**
     *
     * Metodo che permette l'eliminazione di una lista di playlist dalla lista delle playlist, aggiornando al contempo la lista
     * visuale di playlist.
     *
     * @throws IllegalArgumentException nel caso in cui la lista di playlist eliminate non esista.
     *
     */
    @Override
    public void execute() {

        // ottengo il riferimento alla lista di playlist da eliminare
        ArrayList<Playlist> ps = getPlaylists();

        // se il riferimento è null:
        if (ps == null)
            // lancio un'eccezione
            throw new IllegalArgumentException();

        // ottengo i riferimenti alla lista di playlist e alla lista osservabile
        PlaylistList pl = getPlaylistList();
        ObservableList<Playlist> obsList = getObservableList();

        // ottengo il riferimento alla lista di tracce principale
        TrackList tl = TrackList.getTrackListPointer();

        // per ogni playlist da eliminare
        for (Playlist p : ps)
            // Pattern Observer: detach observer di ogni playlist prima di eliminarla
            tl.detach(p);

        // se il riferimento alla lista di playlist non è null, elimino le playlist dalla lista
        if (pl != null)
            pl.deletePlaylists(ps);

        // se il riferimento alla lista osservabile non è null, elimino le playlist dalla lista osservabile
        if (obsList != null)
            obsList.removeAll(ps);
    }
}
