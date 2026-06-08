package org.unisa.musicplaylistmanager.command;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.track.Track;

import java.util.ArrayList;

public abstract class BaseTrackCommands extends BaseCommands{

    //ATTRIBUTI
    private ArrayList<Track> tracks;
    private TrackCollection trackCollection;


    //METODI
    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public TrackCollection getTrackCollection() {
        return trackCollection;
    }

    public void setTrackCollection(TrackCollection trackCollection) {
        this.trackCollection = trackCollection;
    }
}
