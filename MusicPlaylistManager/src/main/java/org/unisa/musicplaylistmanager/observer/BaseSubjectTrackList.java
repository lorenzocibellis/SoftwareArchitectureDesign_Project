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
    //Costruttore
    public BaseSubjectTrackList(){
        playlistObserver = new ArrayList<BaseObserverPlaylist>();
    }

    // aggiunta di un osservatore alla lista
    public void attach(BaseObserverPlaylist observer){
        if (observer != null) // controllo esistenza dell'osservatore
            playlistObserver.add(observer);
    }

    // staccamento di un osservatore dalla lista
    public void detach(BaseObserverPlaylist observer){
        playlistObserver.remove(observer);
    }

    //metodo per notificare eventi alle playlist (eliminazione traccia)
    public void notifyObserver(Track track){
        for(int i = 0; i < playlistObserver.size(); i++){
            // aggiornamento degli osservatori
            playlistObserver.get(i).update(track);
        }
    }
}
