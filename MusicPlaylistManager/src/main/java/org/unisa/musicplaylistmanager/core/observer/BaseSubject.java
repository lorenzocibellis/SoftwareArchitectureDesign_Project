package org.unisa.musicplaylistmanager.core.observer;

/**
 * @author gruppo10
 */

import org.unisa.musicplaylistmanager.track.model.Track;


public interface BaseSubject {

    /**
     * Metodo usato per aggiungere un observer alla lista di observers.
     *
     * @param observer Observer da aggiungere alla lista di osservatori.
     *
     */
    public void attach(BaseObserver observer);

    /**
     * Metodo usato per togliere un observer dalla lista di observers.
     *
     * @param observer Observer da togliere dalla lista di osservatori.
     *
     */
    public void detach(BaseObserver observer);

    /**
     * Metodo usato per notificare agli observers dell'eliminazione di una traccia.
     *
     * @param track Observer da aggiungere alla lista di osservatori.
     *
     */
    public void notifyObservers(Track track);
}
