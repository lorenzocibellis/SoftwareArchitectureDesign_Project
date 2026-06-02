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
import org.unisa.musicplaylistmanager.state.PlayerState;
import org.unisa.musicplaylistmanager.track.Track;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.IntConsumer;

public class Player {
    // variabili dello State Pattern  
    private PlayerState currentState;
    private PlayerState defaultState; // Aggiunto per fedeltà all'UML!
    
    // playlist o tracklist in cui scorrere le tracce
    private Playlist playlist;
    
    //  Dati della singola traccia in riproduzione 
    private Track currentTrack;
    
    //  Variabili interne per la riproduzione 
    private int elapsedSeconds;
    private Timer timer;

    // Variabili di callbacks per la GUI 
    private IntConsumer onTimeTick;
    private Runnable onPlayUIUpdate;
    private Runnable onPauseUIUpdate;

    /**
     * Costruttore della classe Player.
     * 
     * @param defaultState  lo stato iniziale della riproduzione
     * @param playlist      la playlist (o tracklist) da dove è stata avviata la canzone
     * @param currentTrack  la traccia attualmente in riproduzione
     */
    public Player(PlayerState defaultState, Playlist playlist, Track currentTrack) {
        this.defaultState = defaultState;
        this.currentState = defaultState; // Inizializza lo stato corrente con quello di default
        
        this.playlist = playlist;
        this.currentTrack = currentTrack;
        this.elapsedSeconds = 0;
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
        return this.currentTrack;
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
     * 
     * @return lo stato attualmente attivo
     */
    public PlayerState getCurrentState() {
        return this.currentState;
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
                if (currentTrack != null && elapsedSeconds < currentTrack.getDuration()) {
                    elapsedSeconds++;
                    if (onTimeTick != null) onTimeTick.accept(elapsedSeconds);
                } else {
                    // La canzone è finita
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
        if (onPauseUIUpdate != null) onPauseUIUpdate.run();
    }

    /**
     * Interrompe la riproduzione (ad esempio alla chiusura
     * del player o al termine della traccia).
     */
    public void terminate() {
        stopPlayback();
    }
}