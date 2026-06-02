package org.unisa.musicplaylistmanager.track;

import org.unisa.musicplaylistmanager.observer.BaseSubjectTrackList;
import org.unisa.musicplaylistmanager.observer.SubjectTrackList;
import org.unisa.musicplaylistmanager.playlist.Playlist;


/**
 * Rappresenta la libreria musicale principale (TrackList), contenente tutte le tracce
 * caricate nel sistema. Estende {@link Playlist} in modo da ereditare la logica
 * di base di gestione delle liste di tracce.
 *
 * Implementa il pattern Singleton per garantire un'unica istanza globale e utilizza
 * un {@link BaseSubjectTrackList} per notificare i cambiamenti (Pattern Observer).
 *
 * @author gruppo10
 */
public class TrackList extends Playlist {

    //definizione attributo subject per pattern Observer
    private BaseSubjectTrackList subjectTrackList;

    //definizione puntatore per pattern Singleton
    private static TrackList pnt = null;

    /**
     * Costruisce l'unica istanza della libreria principale.
     * Inizializza il subject per il pattern Observer e imposta il puntatore Singleton.
     */
    public TrackList(){
        super(null); // La TrackList principale non ha un nome specifico come le playlist utente
        subjectTrackList = new SubjectTrackList();
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
     * @return l'istanza globale della TrackList
     */
    public static TrackList getTrackListPointer(){
        return pnt;
    }

    /**
     * Restituisce il soggetto (Subject) utilizzato per implementare il pattern Observer.
     * 
     * @return l'oggetto {@link BaseSubjectTrackList} per gestire le notifiche
     */
    public BaseSubjectTrackList getSubjectTrackList(){
        return subjectTrackList;
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
}