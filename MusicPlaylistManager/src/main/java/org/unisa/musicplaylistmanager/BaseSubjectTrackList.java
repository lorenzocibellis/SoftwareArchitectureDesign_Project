package org.unisa.musicplaylistmanager;

import java.util.ArrayList;

public abstract class BaseSubjectTrackList {
    //Definizione attributi
    private ArrayList<BaseObserverPlaylist> playlistObserver;

    //METODI
    //Costruttore
    public BaseSubjectTrackList(){
        playlistObserver = new ArrayList<BaseObserverPlaylist>();
    }

    public void attach(BaseObserverPlaylist){
        return;
    }

    public void detach(BaseObserverPlaylist){
        return;
    }

    public void notify(){
        return
    }
}
