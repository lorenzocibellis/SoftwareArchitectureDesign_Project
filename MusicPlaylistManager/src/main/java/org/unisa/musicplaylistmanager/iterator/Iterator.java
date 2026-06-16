package org.unisa.musicplaylistmanager.iterator;

import org.unisa.musicplaylistmanager.track.list.TrackCollection;
import org.unisa.musicplaylistmanager.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.strategy.Sequential;
import org.unisa.musicplaylistmanager.track.Track;

import java.util.ArrayList;

/**
 * @author gruppo10
 */
public class Iterator implements AbstractIterator {

    private int currentindext; // Puntatore all'interno dell'array iterationindex
    private int[] iterationindex; // Array che mappa l'ordine di riproduzione
    private ArrayList<Track> tracks;
    private String name;

    // Riferimento al brano realmente puntato: serve a riallineare il puntatore quando
    // la lista viene riordinata (uno swap non cambia la dimensione ma sposta le tracce)
    private Track currentTrack;
    
    // Variabile per ricordare la modalità corrente (es. se aggiungo una traccia durante lo shuffle, deve ricalcolare lo shuffle)
    private ExecutionStrategy currentStrategy; 

    /**
     * Costruttore
     * @param trackCollection L'elenco delle tracce in cui scorrere
     */
    public Iterator(TrackCollection trackCollection) {
        this.tracks = trackCollection.getTracks();
        this.name = trackCollection.getName();
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
        if (iterationindex != null && tracks != null && iterationindex.length != tracks.size()) {
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

        if (iterationindex == null || iterationindex.length == 0 || tracks.isEmpty()) {
            return null;
        }
        reanchorIfReordered(); // Riallinea il puntatore se la lista è stata riordinata
        return trackAtPointer();
    }

    /**
     * Restituisce il brano nella posizione attualmente puntata e ne memorizza il
     * riferimento, così da poter rilevare eventuali riordini successivi.
     * @return il brano corrispondente al puntatore corrente
     */
    private Track trackAtPointer() {
        this.currentTrack = tracks.get(iterationindex[currentindext]);
        return this.currentTrack;
    }

    /**
     * Se la lista è stata riordinata (uno swap sposta le tracce senza variare la
     * dimensione), il puntatore continuerebbe a indicare una posizione che ora
     * contiene un brano diverso. Questo metodo rileva la discrepanza confrontando il
     * brano puntato con quello memorizzato e riposiziona il puntatore sul brano
     * realmente in riproduzione, rigenerando l'ordine di navigazione se necessario
     * (caso in cui il piano corrente non contiene la nuova posizione, es. Loop).
     */
    private void reanchorIfReordered() {
        if (currentTrack == null || iterationindex == null || iterationindex.length == 0
                || currentindext >= iterationindex.length) {
            return;
        }
        // Se la posizione puntata contiene ancora il brano corrente, nessun riordino
        if (tracks.get(iterationindex[currentindext]) == currentTrack) {
            return;
        }
        int realIndex = tracks.indexOf(currentTrack);
        if (realIndex == -1) {
            return; // brano non più presente: gestito dalla sincronizzazione per dimensione
        }
        // Riallineamento leggero: cerca la nuova posizione nel piano esistente
        for (int i = 0; i < iterationindex.length; i++) {
            if (iterationindex[i] == realIndex) {
                currentindext = i;
                return;
            }
        }
        // Il piano non contiene la nuova posizione (es. strategia Loop): rigeneralo
        if (currentStrategy != null) {
            this.iterationindex = currentStrategy.execute(tracks.size(), realIndex);
            this.currentindext = 0;
            for (int i = 0; i < iterationindex.length; i++) {
                if (iterationindex[i] == realIndex) {
                    currentindext = i;
                    break;
                }
            }
        }
    }

    /**
     * Metodo per ottenere la traccia successiva secondo un meccanismmo di coda circolare
     * @return Track la traccia successiva
     */
    @Override
    public Track getNext() {
        syncWithPlaylist(); // Sincronizza la coda prima di fare skip!

        if (iterationindex == null || iterationindex.length == 0) return null;

        reanchorIfReordered(); // Parte dalla posizione reale del brano corrente

        // Se siamo arrivati alla fine, ricomincia da capo (comportamento circolare)
        if (currentindext < iterationindex.length - 1) {
            currentindext++;
        } else {
            currentindext = 0;
        }
        return trackAtPointer();
    }

    /**
     * Metodo per ottenere la traccia precedente secondo un meccanismmo di coda circolare
     * @return Track la traccia precedente
     */
    @Override
    public Track getPrevious() {
        syncWithPlaylist(); // Sincronizza la coda prima di fare back!

        if (iterationindex == null || iterationindex.length == 0) return null;

        reanchorIfReordered(); // Parte dalla posizione reale del brano corrente

        // Se siamo all'inizio, va all'ultimo elemento (comportamento circolare)
        if (currentindext > 0) {
            currentindext--;
        } else {
            currentindext = iterationindex.length - 1;
        }
        return trackAtPointer();
    }

    /**
     * Metodo che imposta dinamicamente la strategia di riproduzione e genera il nuovo array di navigazione delle tracce
     * @param es La strategia di riproduzione
     */
    @Override
    public void setStrategy(ExecutionStrategy es) {
        if (tracks == null || tracks.isEmpty()) return;
        
        this.currentStrategy = es; // Salva la strategia in memoria
        
        int size = tracks.size();
        
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

        // Aggiorna il riferimento al brano puntato dopo il ricalcolo dell'ordine
        if (currentindext < iterationindex.length) {
            this.currentTrack = tracks.get(iterationindex[currentindext]);
        }
    }

    /**
     * Metodo di utilità per forzare l'iteratore su una specifica traccia iniziale.
     * @param track La traccia da cui iniziare
     */
    public void moveToTrack(Track track) {
        syncWithPlaylist();
        int realIndex = tracks.indexOf(track);
        if (realIndex != -1 && iterationindex != null) {
            for (int i = 0; i < iterationindex.length; i++) {
                if (iterationindex[i] == realIndex) {
                    this.currentindext = i;
                    this.currentTrack = track; // memorizza il brano puntato
                    break;
                }
            }
        }
    }

    /**
     *
     * Metodo che permette di ottenere l'identificatore di una TrackCollection (Il nome).
     *
     * @return L'identificatore della TrackCollection che l'iterator rappresenta.
     */
    public String getIdentifier(){
        return this.name;
    }
}