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

    public void attach(BaseObserverPlaylist observer){
        if (observer != null)
            playlistObserver.add(observer);
    }

    public void detach(BaseObserverPlaylist observer){
        playlistObserver.remove(observer);
    }

    //metodo per notificare eventi alle playlist (eliminazione traccia)
    public void notifyObserver(Track track){
        for(int i = 0; i < playlistObserver.size(); i++){
            playlistObserver.get(i).update(track);
        }
    }
}
