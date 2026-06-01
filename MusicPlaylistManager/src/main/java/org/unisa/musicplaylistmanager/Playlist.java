package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

import java.util.ArrayList;

public class Playlist {

    private String name;
    private ArrayList<Track> tracks;


    public Playlist(String name) {
        this.name = name;
        this.tracks = new ArrayList<Track>();
    }


        // metodi getter
    public ArrayList<Track> getTracks() {
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
        for(int i = 0; i < tracks.size(); i++){
            if (tracks.get(i).equals(track)) throw new IllegalArgumentException("La traccia esiste già");
        }
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

    //metodi per uguaglianza playlist
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if ((o == null) ||  o.getClass() != this.getClass()) return false;
        Playlist p = (Playlist) o;
        return p.getName().equals(this.getName());
    }

    @Override
    public int hashCode(){
        return this.getName().hashCode();
    }

    //metodo per stampa playlist
    @Override
    public String toString(){
        return this.getName();
    }
}
