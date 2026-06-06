package org.unisa.musicplaylistmanager.iterator;

import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.strategy.Sequential;
import org.unisa.musicplaylistmanager.track.Track;

/**
 * @author gruppo10
 */
public class Iterator implements AbstractIterator {

    private int currentindext; // Puntatore all'interno dell'array iterationindex
    private int[] iterationindex; // Array che mappa l'ordine di riproduzione
    private TrackCollection trackCollection;
    
    // Variabile per ricordare la modalità corrente (es. se aggiungo una traccia durante lo shuffle, deve ricalcolare lo shuffle)
    private ExecutionStrategy currentStrategy; 

    /**
     * Costruttore
     * @param trackCollection L'elenco delle tracce in cui scorrere
     */
    public Iterator(TrackCollection trackCollection) {
        this.trackCollection = trackCollection;
        this.currentindext = 0;
        
        // Inizializza con la strategia sequenziale di default
        setStrategy(new Sequential());
    }

    /**
     * Metodo di sincronizzazione dinamica.
     * Controlla se la dimensione della playlist è cambiata (tracce aggiunte o rimosse in tempo reale).
     * Se sì, forza il ricalcolo dell'array di iterazione mantenendo la strategia attuale.
     */
    private void syncWithPlaylist() {
        if (iterationindex != null && trackCollection != null && iterationindex.length != trackCollection.getTracks().size()) {
            if (currentStrategy != null) {
                setStrategy(currentStrategy);
            }
        }
    }

    /**
     * Metodo per ottenere la traccia corrente
     * @return Track la traccia corrente
     */
    @Override
    public Track getCurrent() {
        syncWithPlaylist(); // Sincronizza la coda prima di restituire il brano!
        
        if (iterationindex == null || iterationindex.length == 0 || trackCollection.getTracks().isEmpty()) {
            return null;
        }
        int realIndex = iterationindex[currentindext];
        return trackCollection.getTracks().get(realIndex);
    }

    /**
     * Metodo per ottenere la traccia successiva secondo un meccanismmo di coda circolare
     * @return Track la traccia successiva
     */
    @Override
    public Track getNext() {
        syncWithPlaylist(); // Sincronizza la coda prima di fare skip!
        
        if (iterationindex == null || iterationindex.length == 0) return null;
        
        // Se siamo arrivati alla fine, ricomincia da capo (comportamento circolare)
        if (currentindext < iterationindex.length - 1) {
            currentindext++;
        } else {
            currentindext = 0; 
        }
        return getCurrent();
    }

    /**
     * Metodo per ottenere la traccia precedente secondo un meccanismmo di coda circolare
     * @return Track la traccia precedente
     */
    @Override
    public Track getPrevious() {
        syncWithPlaylist(); // Sincronizza la coda prima di fare back!
        
        if (iterationindex == null || iterationindex.length == 0) return null;
        
        // Se siamo all'inizio, va all'ultimo elemento (comportamento circolare)
        if (currentindext > 0) {
            currentindext--;
        } else {
            currentindext = iterationindex.length - 1;
        }
        return getCurrent();
    }

    /**
     * Metodo che imposta dinamicamente la strategia di riproduzione e genera il nuovo array di navigazione delle tracce
     * @param es La strategia di riproduzione
     */
    @Override
    public void setStrategy(ExecutionStrategy es) {
        if (trackCollection == null || trackCollection.getTracks().isEmpty()) return;
        
        this.currentStrategy = es; // Salva la strategia in memoria
        
        int size = trackCollection.getTracks().size();
        
        // Recuperiamo l'indice reale della traccia in corso per non interrompere l'ascolto
        int realCurrentIndex = 0;
        if (iterationindex != null && iterationindex.length > 0 && currentindext < iterationindex.length) {
            int oldIndex = iterationindex[currentindext];
            // Sicurezza: se la traccia non è stata eliminata, la teniamo come riferimento
            if (oldIndex < size) {
                realCurrentIndex = oldIndex;
            }
        }
        
        // Genera il nuovo ordine di riproduzione dinamico
        this.iterationindex = es.execute(size, realCurrentIndex);
        
        // Riposiziona il puntatore per far combaciare la riproduzione attuale col nuovo array
        this.currentindext = 0;
        for (int i = 0; i < this.iterationindex.length; i++) {
            if (this.iterationindex[i] == realCurrentIndex) {
                this.currentindext = i;
                break;
            }
        }
    }

    /**
     * Metodo di utilità per forzare l'iteratore su una specifica traccia iniziale.
     * @param track La traccia da cui iniziare
     */
    public void moveToTrack(Track track) {
        syncWithPlaylist();
        int realIndex = trackCollection.getTracks().indexOf(track);
        if (realIndex != -1 && iterationindex != null) {
            for (int i = 0; i < iterationindex.length; i++) {
                if (iterationindex[i] == realIndex) {
                    this.currentindext = i;
                    break;
                }
            }
        }
    }
}