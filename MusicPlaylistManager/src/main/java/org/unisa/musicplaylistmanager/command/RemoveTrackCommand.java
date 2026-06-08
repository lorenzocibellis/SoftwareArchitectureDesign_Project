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

    @Override
    public void execute() {

        ArrayList<Track> ts = getTracks();
        if(ts == null)
            throw new IllegalArgumentException();

        TrackCollection tc = getTrackCollection();
        ObservableList<Track> obs = getObservableList();

        if(obs != null)
            obs.removeAll(ts);

        if(tc != null){
            tc.removeAllTracks(ts);
            if(tc.getClass() == TrackList.class){
                TrackList tl = (TrackList) tc;
                for(Track t: ts){
                    tl.notifyObservers(t);
                }
            }
        }
    }
}
