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

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.TrackCollection;
import org.unisa.musicplaylistmanager.state.PlayerState;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.iterator.Iterator;

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
    
    // playlist o tracklist in cui scorrere le tracce
    private Playlist playlist;
    
    // Iteratore incaricato della gestione dello scorrimento delle tracce
    private Iterator trackIterator;
    
    //  Variabili interne per la riproduzione 
    private int elapsedSeconds;
    private Timer timer;

    // Variabili di callbacks per la GUI 
    private IntConsumer onTimeTick;
    private Runnable onPlayUIUpdate;
    private Runnable onPauseUIUpdate;
    private Runnable onTrackChanged;

    /**
     * Costruttore della classe Player.
     * Costruttore della classe Player.
     * * @param defaultState    lo stato iniziale della riproduzione
     * @param trackCollection la collezione di tracce (o playlist) da cui è stata avviata la canzone
     * @param currentTrack    la traccia attualmente in riproduzione
     */
public Player(PlayerState defaultState, TrackCollection trackCollection, Track currentTrack) {
    // Manteniamo il controllo di validità per evitare NullPointerException
    if (trackCollection == null || currentTrack == null) {
        throw new IllegalArgumentException("Il player richiede una collezione di tracce e una traccia valide per essere inizializzato.");
    }

    this.defaultState = defaultState;
    this.currentState = defaultState;
    
    // ATTENZIONE: Assicurati che la variabile 'playlist' sia di tipo TrackCollection 
    // o casta correttamente se necessario.
    this.playlist = (Playlist) trackCollection; 
    this.elapsedSeconds = 0;
    
    // Inizializza l'iteratore concreto e lo sposta sulla traccia cliccata dall'utente
    this.trackIterator = new Iterator(this.playlist);
    this.trackIterator.moveToTrack(currentTrack);
}

    //  Registrazione eventi sulla GUI  in tempo reale
    
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
     * Restituisce la playlist attualmente in riproduzione.
     * @return la playlist corrente
     */
    public Playlist getCurrentPlaylist() {
        return this.playlist;
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
    public Iterator getIterator() {
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
        // creazione di un nuovo timer
        timer = new Timer();
        
        timer.scheduleAtFixedRate(new TimerTask() {
            // Thread per la riproduzione 
            @Override
            public void run() {
                Track current = getCurrentTrack();
                if (current != null && elapsedSeconds < current.getDuration()) {
                    elapsedSeconds++;
                    if (onTimeTick != null) onTimeTick.accept(elapsedSeconds);
                } else {
                    // La canzone è finita: passa automaticamente alla successiva in JavaFX Thread
                    javafx.application.Platform.runLater(() -> nextTrack());
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
        if (onPauseUIUpdate != null) onPauseUIUpdate.run();
    }

    /**
     * Avanza alla traccia successiva definita dall'iteratore e ne avvia la riproduzione.
     */
    public void nextTrack() {
        stopPlayback();
        this.elapsedSeconds = 0;
        this.trackIterator.getNext();
        if (onTrackChanged != null) onTrackChanged.run();
        startPlayback();
    }

    /**
     * Regredisce alla traccia precedente definita dall'iteratore e ne avvia la riproduzione.
     */
    public void previousTrack() {
        stopPlayback();
        this.elapsedSeconds = 0;
        this.trackIterator.getPrevious();
        if (onTrackChanged != null) onTrackChanged.run();
        startPlayback();
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