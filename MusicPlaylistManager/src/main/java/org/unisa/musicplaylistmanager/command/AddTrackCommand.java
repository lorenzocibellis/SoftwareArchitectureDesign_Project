package org.unisa.musicplaylistmanager.command;

import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddTrackCommand extends BaseTrackCommands{


    public AddTrackCommand(Track t, TrackCollection tc, ObservableList<Track> o){
        ArrayList<Track> ts = new ArrayList<>();
        ts.add(t);
        setTracks(ts);
        setTrackCollection(tc);
        setObservableList(o);
    }

    public AddTrackCommand(ArrayList<Track> ts, TrackCollection tc, ObservableList<Track> o){
        setTracks(ts);
        setTrackCollection(tc);
        setObservableList(o);
    }

    @Override
    public void undo() {
        ArrayList<Track> ts = getTracks();
        if (ts == null)
            throw new IllegalArgumentException();

        TrackCollection tc = getTrackCollection();
        if (tc != null) {
            tc.removeAllTracks(ts);
            if (tc.getClass() == TrackList.class) {
                for(Track t: ts){
                TrackList tl = (TrackList) tc;
                tl.notifyObservers(t);
            }
        }
    }


        ObservableList<Track> obs = getObservableList();
        if(obs != null)
            obs.removeAll(ts);
    }

    @Override
    public void execute() {
        ArrayList<Track> ts = getTracks();
        if (ts == null)
            throw new IllegalArgumentException();

        TrackCollection tc = getTrackCollection();
        ObservableList<Track> obs = getObservableList();

        for(Track t: ts) {
            if (tc != null)
                tc.addTrack(t);

            if (obs != null)
                obs.add(t);
        }


    }
}
