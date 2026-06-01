package org.unisa.musicplaylistmanager.observer;

/**
 * @author gruppo10
 */

import org.unisa.musicplaylistmanager.track.Track;

import java.util.ArrayList;

public abstract class BaseSubjectTrackList {
    //Definizione attributi
    private ArrayList<BaseObserverPlaylist> playlistObserver;

    //METODI
    /**
     * Costruttore
     *
     */
    public BaseSubjectTrackList(){
        playlistObserver = new ArrayList<BaseObserverPlaylist>();
    }

    /**
     * Metodo usato per aggiungere un observer alla lista di observers.
     *
     * @param observer Observer da aggiungere alla lista di osservatori.
     *
     */
    public void attach(BaseObserverPlaylist observer){
        if (observer != null) // controllo esistenza dell'osservatore
            playlistObserver.add(observer);
    }

    /**
     * Metodo usato per togliere un observer dalla lista di observers.
     *
     * @param observer Observer da togliere dalla lista di osservatori.
     *
     */
    public void detach(BaseObserverPlaylist observer){
        playlistObserver.remove(observer);
    }

    /**
     * Metodo usato per notificare agli observers dell'eliminazione di una traccia.
     *
     * @param track Observer da aggiungere alla lista di osservatori.
     *
     */
    public void notifyObserver(Track track){
        for(int i = 0; i < playlistObserver.size(); i++){
            // aggiornamento degli osservatori
            playlistObserver.get(i).update(track);
        }
    }
}
