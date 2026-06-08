package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;

public class RemoveTrackCommand extends BaseTrackCommands{

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
            // rimuovo le tracce dalla collezione di tracce
            tc.removeAllTracks(ts);
            // controllo che la collezione sia la libreria principale (TrackList)
            if(tc.getClass() == TrackList.class){
                // se lo è:
                // uso il pattern Observer per aggiornare lo stato di tutti gli observer
                TrackList tl = (TrackList) tc;
                for(Track t: ts){
                    tl.notifyObservers(t);
                }
            }
        }
    }
}
