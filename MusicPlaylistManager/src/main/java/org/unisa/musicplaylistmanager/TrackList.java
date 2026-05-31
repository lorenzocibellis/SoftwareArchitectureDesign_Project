package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

public class TrackList extends Playlist{

    //definizione subject per pattern Observer
    private BaseSubjectTrackList subjectTrackList;

    //METODI
    //Costruttore
    public TrackList(){
        super(null);
    }

    @Override
    public void removeTrack(Track track){
        this.getTracks().remove(track);
    }
}
