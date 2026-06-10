package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveTrackCommand extends BaseTrackCommands{

    //ATTRIBUTI
    // HashMap utile a implementare l'undo dell'eliminazione di una traccia anche dalle playlist
    // che implementano l'observer
    private Map<Track, List<Playlist>> playlistMap = new HashMap<>();

    /**
     *
     * Costruttore
     *
     * @param ts Lista di tracce da rimuovere.
     * @param tc Lista di tracce da cui sono rimosse le tracce.
     * @param o Lista osservabile di tracce da aggiornare.
     *
     */
    public RemoveTrackCommand(ArrayList<Track> ts, TrackCollection tc, ObservableList<Track> o){
        setTracks(ts);
        setTrackCollection(tc);
        setObservableList(o);
    }

    /**
     *
     * Metodo che permette l'annullamento dell'eliminazione di tracce da una collezione di tracce,
     * aggiornando al contempo la lista visuale di playlist.
     *
     * @throws IllegalArgumentException nel caso in cui la lista di tracce eliminate non esista.
     *
     */
    @Override
    public void undo() {
        ArrayList<Track> ts = getTracks();
        if(ts == null)
            throw new IllegalArgumentException();

        TrackCollection tc = getTrackCollection();
        ObservableList<Track> obs = getObservableList();

        for(Track t: ts){
            if(tc != null)
                tc.addTrack(t);


            if (obs != null)
                obs.add(t);

            // riaggiungo la traccia a tutte le playlist in cui era stata eliminata
            for(Playlist p: playlistMap.get(t)){
                p.addTrack(t);
            }
        }
    }

    /**
     *
     * Metodo che permette l'eliminazione di un'insieme di tracce da una collezione di tracce, aggiornando al contempo la lista
     * visuale di playlist.
     *
     * @throws IllegalArgumentException nel caso in cui la lista di tracce eliminate non esista.
     *
     */
    @Override
    public void execute() {
        // ottengo il riferimento alla lista di tracce da eliminare
        ArrayList<Track> ts = getTracks();
        // controllo che il riferimento non sia null
        if(ts == null)
            // nel caso lo sia lancio un'eccezione
            throw new IllegalArgumentException();

        // ottengo i riferimenti alla collezione e alla lista osservabile da cui elimianare le tracce
        TrackCollection tc = getTrackCollection();
        ObservableList<Track> obs = getObservableList();

        // se il riferimento alla lista osservabile non è null, rimuovo le tracce da essa
        if(obs != null)
            obs.removeAll(ts);

        // controllo che il riferimento alla collezione di tracce non sia null
        if(tc != null){

            // controllo che la TrackCollection sia una TrackList
            if(tc instanceof TrackList){
                // Se lo è, inizio a popolare l'hashMap
                ArrayList<Playlist> temp = PlaylistList.getPlaylistListPointer().getPlaylists();
                for(Track t: ts){
                    // creo l'array di playlist che contengono una certa traccia
                    ArrayList<Playlist> playlistWTrack = new ArrayList<>();
                    for(Playlist p: temp){
                        // controllo che la traccia sia contenuta in una certa playlist
                        if(p.getTracks().contains(t))
                            // se è contenuta, aggiungo la playlist alla lista di playlist che contengono la data traccia
                            playlistWTrack.add(p);
                    }
                    // memorizzo la lista di playlist che contengono una data traccia con la traccia stessa come chiave
                    playlistMap.put(t, playlistWTrack);
                }
            }

            // rimuovo le tracce dalla collezione di tracce
            tc.removeAllTracks(ts);
        }
    }
}
