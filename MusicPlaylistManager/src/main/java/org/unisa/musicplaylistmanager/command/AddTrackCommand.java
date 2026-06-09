package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;
import java.util.ArrayList;

/**
 *
 * Classe che implementa il comando di aggiunta di una o più tracce, e le eventuali operazioni per permettere
 * l'annullamento di tale operazione.
 *
 */
public class AddTrackCommand extends BaseTrackCommands{

    /**
     *
     * Costruttore.
     *
     * @param t Traccia da aggiungere.
     * @param tc Collezione di tracce a cui aggiungere la traccia.
     * @param o Lista osservabile a cui aggiungere la traccia.
     */
    public AddTrackCommand(Track t, TrackCollection tc, ObservableList<Track> o){
        if(t == null)
            throw new IllegalArgumentException();
        ArrayList<Track> ts = new ArrayList<>();
        ts.add(t);
        setTracks(ts);
        setTrackCollection(tc);
        setObservableList(o);
    }

    /**
     *
     * Costruttore.
     *
     * @param ts Lista di tracce da aggiungere.
     * @param tc Collezione di tracce a cui aggiungere le tracce.
     * @param o Lista osservabile a cui aggiungere la traccia.
     */
    public AddTrackCommand(ArrayList<Track> ts, TrackCollection tc, ObservableList<Track> o){
        setTracks(ts);
        setTrackCollection(tc);
        setObservableList(o);
    }

    /**
     *
     * Metodo che permette di annullare l'operazione di aggiunta di una traccia.
     *
     * @throws IllegalArgumentException nel caso la lista di tracce aggiungente non esista.
     *
     */
    @Override
    public void undo() {
        // ottengo il riferimento alla lista di tracce da rimuovere
        ArrayList<Track> ts = getTracks();

        // controllo che il riferimento non sia null
        if (ts == null)
            // nel caso lo sia lancio un'eccezione
            throw new IllegalArgumentException();

        // ottengo il riferimento alla collezione di tracce da cui eliminare le tracce
        TrackCollection tc = getTrackCollection();

        // controllo che il riferimento alla collezione non sia null
        if (tc != null) {
            // rimuove le tracce dalla collezione
            tc.removeAllTracks(ts);
        }

        // ottengo il riferimento alla lista osservabile
        ObservableList<Track> obs = getObservableList();
        // se il riferimento non è nullo, rimuovo le tracce dalla lista osservabile
        if(obs != null)
            obs.removeAll(ts);
    }


    /**
     *
     * Metodo che permette di aggiungere una lista di tracce.
     *
     * @throws IllegalArgumentException nel caso la lista di tracce da aggiungere non esista.
     *
     */
    @Override
    public void execute() {
        // ottengo il riferimento alla lista di tracce da aggiungere
        ArrayList<Track> ts = getTracks();
        // controllo che il riferimento non sia null
        if (ts == null)
            // se lo è lancio un'eccezione
            throw new IllegalArgumentException();

        // ottengo i riferimenti alla collezione di tracce e alla lista osservabile
        TrackCollection tc = getTrackCollection();
        ObservableList<Track> obs = getObservableList();

        // per ogni traccia da aggiungere
        for(Track t: ts) {

            // se il riferimento alla collezione di tracce non è null, aggiungo la traccia alla collezione
            if (tc != null)
                tc.addTrack(t);

            // se il riferimento alla lista osservabile non è null, aggiungo la traccia alla lista
            if (obs != null)
                obs.add(t);
        }
    }
}
