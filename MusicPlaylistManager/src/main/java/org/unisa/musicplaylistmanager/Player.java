package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

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

    // Costruttore: Accetta TEMPORANEAMENTE anche la Track 
    public Player(PlayerState defaultState, Playlist playlist, Track currentTrack) {
        this.defaultState = defaultState;
        this.currentState = defaultState; // Inizializza lo stato corrente con quello di default
        
        this.playlist = playlist;
        this.currentTrack = currentTrack;
        this.elapsedSeconds = 0;
    }

    //  Registrazione eventi sulla GUI  in tempo reale
    
    public void setOnTimeTick(IntConsumer listener) { this.onTimeTick = listener; }
    public void setOnPlayUIUpdate(Runnable listener) { this.onPlayUIUpdate = listener; }
    public void setOnPauseUIUpdate(Runnable listener) { this.onPauseUIUpdate = listener; }

    // Metodi di gestione dello State Pattern
    
    public void changeState() {
        currentState.execute(this);
    }

    public void setState(PlayerState state) {
        this.currentState = state;
    }

    // Logica di Riproduzione della Traccia
    
    public void startPlayback() {
        // pulizia risorse timer
        if (timer != null) timer.cancel();
        // creazione di un nuovo timer
        timer = new Timer();
        
        timer.scheduleAtFixedRate(new TimerTask() {
            // Thread per la riproduzione 
            @Override
            public void run() {
                if (currentTrack != null && elapsedSeconds < currentTrack.getSeconds()) {
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

    // Funzione per interrompere la riproduzione della traccia
    public void stopPlayback() {
        // pulizia risorse timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (onPauseUIUpdate != null) onPauseUIUpdate.run();
    }

    // Funzione per interrompere bruscamente la riproduzione (come quando si chiude la finestra del player)
    public void terminate() {
        stopPlayback();
    }
}