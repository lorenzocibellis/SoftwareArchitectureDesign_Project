package org.unisa.musicplaylistmanager.track;

import org.unisa.musicplaylistmanager.observer.BaseObserver;
import org.unisa.musicplaylistmanager.observer.BaseSubject;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;

import java.util.ArrayList;


/**
 * Rappresenta la libreria musicale principale (TrackList), contenente tutte le tracce
 * caricate nel sistema. Estende {@link Playlist} in modo da ereditare la logica
 * di base di gestione delle liste di tracce.
 *
 * Implementa il pattern Singleton per garantire un'unica istanza globale e utilizza
 * un {@link BaseSubject} per notificare i cambiamenti (Pattern Observer).
 *
 * @author gruppo10
 */
public class TrackList extends TrackCollection implements BaseSubject{

    //definizione lista di observers per pattern Observer
    private ArrayList<BaseObserver> observers;

    //definizione puntatore per pattern Singleton
    private static TrackList pnt = null;

    /**
     * Costruisce l'unica istanza della libreria principale.
     * Inizializza il subject per il pattern Observer e imposta il puntatore Singleton.
     */
    private TrackList(){
        super(null);
        observers = new ArrayList<>();
        pnt = this;
    }

    /**
     * Verifica se l'istanza Singleton della TrackList è già stata inizializzata.
     * 
     * @return {@code true} se esiste, {@code false} altrimenti
     */
    public static boolean exists(){
        return !(pnt == null);
    }

    /**
     * Restituisce il puntatore all'istanza Singleton di {@code TrackList}.
     * 
     * @return l'istanza globale della TrackList se esiste, altrimenti crea un'istanza e la restituisce
     */
    public static TrackList getTrackListPointer(){
        if (exists()) return pnt;
        return new TrackList();
    }

    @Override
    public void removeTrack(Track track){
        super.removeTrack(track);
        notifyObservers(track);
    }

    @Override
    public void removeAllTracks(ArrayList<Track> tracks){
        super.removeAllTracks(tracks);
        for(Track t: tracks){
            notifyObservers(t);
        }
    }

    /**
     * Aggiorna i dati di una traccia esistente chiamando l'implementazione base di
     * {@link Playlist#updateTrack(Track, Track)}.
     * 
     * @param existingTrack la traccia esistente da modificare
     * @param newDataTrack i nuovi dati da applicare
     */
    @Override
    public void updateTrack(Track existingTrack, Track newDataTrack) {
        super.updateTrack(existingTrack, newDataTrack);
    }



    @Override
    public void attach(BaseObserver observer) {
        if(observer != null && !observers.contains(observer))
            observers.add(observer);
    }

    @Override
    public void detach(BaseObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Track track) {
        for(BaseObserver o: observers)
            o.update(track);
    }
}