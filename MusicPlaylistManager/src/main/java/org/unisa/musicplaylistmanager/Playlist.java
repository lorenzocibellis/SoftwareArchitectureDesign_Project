package org.unisa.musicplaylistmanager;

import java.util.LinkedHashSet;
import java.util.Set;

public class Playlist {

    private String name;
    private LinkedHashSet<Track> tracks;


    public Playlist(String name) {
        this.name = name;
        this.tracks = new LinkedHashSet<Track>();
    }


        // metodi getter
    public LinkedHashSet<Track> getTracks() {
        return this.tracks;
    }

    public String getName() {
        return name;
    }

    public Track getTrack() {
        throw new UnsupportedOperationException();
    }


    // metodi setter
    public void setName(String name) {
        this.name = name;
    }


    // aggiunge una traccia alla lista
    public void addTrack(Track track) {
        tracks.add(track);
    }


    // rimuove una traccia dalla lista
    public void removeTrack(Track track) {
        tracks.remove(track);

    }

    // restituisce l'indice di una traccia nella lista
    public int getIndex(Track track){
        throw new UnsupportedOperationException();
    }

    // restituisce la dimensione della lista
    public int getSize(){
        return tracks.size();
    }



    public void undo(){
        throw new UnsupportedOperationException();
    }

}
