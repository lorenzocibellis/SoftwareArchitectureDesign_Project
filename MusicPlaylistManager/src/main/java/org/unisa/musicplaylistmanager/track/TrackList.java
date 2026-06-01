package org.unisa.musicplaylistmanager.track;

import org.unisa.musicplaylistmanager.observer.BaseSubjectTrackList;
import org.unisa.musicplaylistmanager.observer.SubjectTrackList;
import org.unisa.musicplaylistmanager.playlist.Playlist;

/**
 * @author gruppo10
 */

public class TrackList extends Playlist {

    //definizione subject per pattern Observer
    private BaseSubjectTrackList subjectTrackList;

    //definizione puntatore per pattern Singleton
    private static TrackList pnt = null;

    //METODI
    //Costruttore
    public TrackList(){
        super(null);
        subjectTrackList = new SubjectTrackList();
        pnt = this;
    }

    public static boolean exists(){
        return !(pnt == null);
    }

    public static TrackList getTrackListPointer(){
        return pnt;
    }

    public BaseSubjectTrackList getSubjectTrackList(){
        return subjectTrackList;
    }
}
