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

    @Override
    public void undo() {
        Track t = getTracks().get(0);
        if (t == null)
            throw new IllegalArgumentException();

        TrackCollection tc = getTrackCollection();
        if (tc != null){
            tc.removeTrack(t);
            if(tc.getClass() == TrackList.class) {
                TrackList tl = (TrackList) tc;
                tl.notifyObservers(t);
            }
        }
    }

    @Override
    public void execute() {

        Track t = getTracks().get(0);
        if (t == null)
            throw new IllegalArgumentException();

        TrackCollection tc = getTrackCollection();
        if (tc != null)
            tc.addTrack(t);

        ObservableList<Track> obs = getObservableList();
        if(obs != null)
            obs.add(t);


    }
}
