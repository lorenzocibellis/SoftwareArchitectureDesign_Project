package org.unisa.musicplaylistmanager.track;

import org.unisa.musicplaylistmanager.observer.BaseSubjectTrackList;
import org.unisa.musicplaylistmanager.observer.SubjectTrackList;
import org.unisa.musicplaylistmanager.playlist.Playlist;


// estensione della classe Playlist così da ereditarne metodi ed attributi
public class TrackList extends Playlist {

    //definizione attributo subject per pattern Observer
    private BaseSubjectTrackList subjectTrackList;

    //definizione puntatore per pattern Singleton
    private static TrackList pnt = null;

    //METODI
    //Costruttore
    public TrackList(){

        // questa classe non ha nome, pertanto la inizializziamo con null
        super(null);
        // inizializziamo il subject per l'observer
        subjectTrackList = new SubjectTrackList();
        // inizializziamo il puntatore a questo oggetto per pattern singleton
        pnt = this;
    }

    //verifica esistenza di un obj TrackList
    public static boolean exists(){
        return !(pnt == null);
    }

    //ottenimento obj TrackList
    public static TrackList getTrackListPointer(){
        return pnt;
    }

    //ottenimento obj Subject del pattern Observer
    public BaseSubjectTrackList getSubjectTrackList(){
        return subjectTrackList;
    }
}
