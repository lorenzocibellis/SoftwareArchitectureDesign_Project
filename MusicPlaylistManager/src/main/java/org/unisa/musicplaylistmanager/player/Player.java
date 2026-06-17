package org.unisa.musicplaylistmanager.player;

/**
 * Classe centrale per la gestione della riproduzione musicale.
 *
 * Questa classe utilizza lo State Pattern per gestire gli stati interni di 
 * riproduzione (Play e Pause). Implementa un timer per simulare
 * l'avanzamento della traccia in tempo reale e supporta l'aggiornamento
 * dell'interfaccia utente tramite l'esecuzione di callback registrate.
 *
 * @author gruppo10
 */

import org.unisa.musicplaylistmanager.iterator.AbstractIterator;
import org.unisa.musicplaylistmanager.iterator.IterableCollection;
import org.unisa.musicplaylistmanager.state.PlayerState;
import org.unisa.musicplaylistmanager.track.Track;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.IntConsumer;

/**
 *
 * @author filom
 */
public class Player {
    // variabili dello State Pattern  
    private PlayerState currentState;
    private PlayerState defaultState; // Aggiunto per fedeltà all'UML!

    
    // Iteratore incaricato della gestione dello scorrimento delle tracce
    private AbstractIterator trackIterator;
    
    //  Variabili interne per la riproduzione 
    private int elapsedSeconds;
    private Timer timer;

    // Variabili di callbacks per la GUI 
    private IntConsumer onTimeTick;
    private Runnable onPlayUIUpdate;
    private Runnable onPauseUIUpdate;
    private Runnable onTrackChanged;

    // FLAG per distinguere play nuovo vs resume da pausa (IMPORTANTE PER BUSINESS RULE)
    private boolean resumed = false;

    /**
     * Costruttore della classe Player.
     * * @param defaultState    lo stato iniziale della riproduzione
     * @param defaultState
     * @param iter la collezione di tracce (o playlist) da cui è stata avviata la canzone
     * @param currentTrack    la traccia attualmente in riproduzione
     */
    public Player(PlayerState defaultState, IterableCollection iter, Track currentTrack) {
        // Manteniamo il controllo di validità per evitare NullPointerException
        if (iter == null || currentTrack == null) {
            throw new IllegalArgumentException("Il player richiede una collezione di tracce e una traccia valide per essere inizializzato.");
        }

        this.defaultState = defaultState;
        this.currentState = defaultState;
        
        // Assegnazione della collezione
        this.elapsedSeconds = 0;
        
        // Inizializza l'iteratore passando la collezione base
        this.trackIterator = iter.createIterator();
        this.trackIterator.moveToTrack(currentTrack);
    }
    
    /**
     * Imposta il callback per l'aggiornamento in tempo reale del timer.
     * @param listener il listener da eseguire a ogni tick del timer passando i secondi trascorsi
     */
    public void setOnTimeTick(IntConsumer listener) { this.onTimeTick = listener; }

    /**
     * Imposta il callback per l'aggiornamento dell'interfaccia utente (UI) quando il player entra in Play.
     * @param listener il listener da eseguire per aggiornare la UI in stato di riproduzione
     */
    public void setOnPlayUIUpdate(Runnable listener) { this.onPlayUIUpdate = listener; }

    /**
     * Imposta il callback per l'aggiornamento dell'interfaccia utente (UI) quando il player entra in Pausa.
     * @param listener il listener da eseguire per aggiornare la UI in stato di pausa
     */
    public void setOnPauseUIUpdate(Runnable listener) { this.onPauseUIUpdate = listener; }

    /**
     * Imposta il callback per notificare la UI quando viene cambiata la traccia corrente.
     * @param listener il listener da eseguire al cambio della traccia
     */
    public void setOnTrackChanged(Runnable listener) { this.onTrackChanged = listener; }

    // Metodi di gestione dello State Pattern
    
    /**
     * Cambia lo stato corrente chiamando il metodo execute() dello stato attivo.
     */
    public void changeState() {
        currentState.execute(this);
    }

    /**
     * Imposta un nuovo stato per il player.
     * @param state il nuovo stato (es. Play o Pause)
     */
    public void setState(PlayerState state) {
        this.currentState = state;
    }

    /**
     * Restituisce la traccia attualmente in riproduzione.
     * @return la traccia corrente
     */
    public Track getCurrentTrack() {
        return this.trackIterator.getCurrent();
    }

    /**
    * Restituisce l'identificatore della collezione (Playlist o TrackList) attualmente in riproduzione.
    * @return l'identificatore della TrackCollection corrente
    */
    public String getCurrentPlaylistIdentifier() {
        return this.trackIterator.getIdentifier();
    }

    /**
     * Restituisce lo stato corrente del player.
     * @return lo stato attualmente attivo
     */
    public PlayerState getCurrentState() {
        return this.currentState;
    }

    /**
     * Restituisce l'iteratore associato al player per consentire il cambio di Strategy.
     * @return l'iteratore corrente
     */
    public AbstractIterator getIterator() {
        return this.trackIterator;
    }

    // Logica di Riproduzione della Traccia
    
    /**
     * Avvia o riprende la riproduzione della traccia.
     * Crea e avvia il timer interno che aggiorna periodicamente il tempo trascorso
     * e notifica la GUI dei cambiamenti.
     */
    public void startPlayback() {
        // pulizia risorse timer
        if (timer != null) timer.cancel();

        timer = new Timer();

        // incrementa SOLO se NON è un resume da pausa
        if (!resumed) {
            registerPlay();
        }

        // dopo il primo start, qualsiasi play successivo è resume
        resumed = false;

        timer.scheduleAtFixedRate(new TimerTask() {
            // Thread per la riproduzione 
            @Override
            public void run() {
                Track current = getCurrentTrack();
                if (current != null) {
                    if (elapsedSeconds < current.getDuration()) {
                        elapsedSeconds++;
                        if (onTimeTick != null) onTimeTick.accept(elapsedSeconds);
                    } else {
                        // La canzone è finita: passa alla successiva
                        javafx.application.Platform.runLater(() -> nextTrack());
                    }
                } else {
                    // Se la traccia è nulla, fermiamo il timer invece di skippare
                    stopPlayback();
                }
            }
        }, 1000, 1000);

        if (onPlayUIUpdate != null) onPlayUIUpdate.run();
    }

    /**
     * Ferma la riproduzione in corso, bloccando l'avanzamento del tempo e aggiornando la GUI.
     */
    public void stopPlayback() {
        // pulizia risorse timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        // ✔ IMPORTANTISSIMO: segnala che il prossimo play è un RESUME
        resumed = true;

        if (onPauseUIUpdate != null) onPauseUIUpdate.run();
    }
    
    /**
    * Metodo che registra un riproduzione per la traccia attualmente selezionata.
    * Recupera la traccia corrente tramite {@link #getCurrentTrack()} e, 
    * se valida, incrementa il suo contatore di riproduzioni.
    */
    private void registerPlay() {
        Track current = getCurrentTrack();
        if (current != null) {
            current.incrementNumOfPlay();
        }
    }

    /**
     * Avanza alla traccia successiva definita dall'iteratore e ne avvia la riproduzione.
     */
    public void nextTrack() {
        stopPlayback();
        this.elapsedSeconds = 0;
        Track next = this.trackIterator.getNext();

        // nuova traccia = NON resume
        resumed = false;
    
        if (onTrackChanged != null) onTrackChanged.run();
        
        if (next != null) {
            startPlayback();
        }
    }

    /**
     * Regredisce alla traccia precedente definita dall'iteratore e ne avvia la riproduzione.
     */
    public void previousTrack() {
        stopPlayback();
        this.elapsedSeconds = 0;
        Track prev = this.trackIterator.getPrevious();

        // nuova traccia = NON resume
        resumed = false;

        if (onTrackChanged != null) onTrackChanged.run();
        
        if (prev != null) {
            startPlayback();
        }
    }

    /**
     * Consente di riposizionare la riproduzione al secondo specificato.
     * * @param seconds il nuovo secondo a cui spostarsi
     * @param seconds
     */
    public void seekTo(int seconds) {
        Track current = getCurrentTrack();
        if (current != null) {
            this.elapsedSeconds = Math.max(0, Math.min(seconds, current.getDuration()));
            if (onTimeTick != null) {
                onTimeTick.accept(this.elapsedSeconds);
            }
        }
    }

    /**
     * Interrompe la riproduzione (ad esempio alla chiusura
     * del player o al termine della traccia).
     */
    public void terminate() {
        stopPlayback();
    }
}