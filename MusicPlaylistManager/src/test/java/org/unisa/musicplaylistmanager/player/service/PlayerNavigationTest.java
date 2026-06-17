package org.unisa.musicplaylistmanager.player.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.player.model.Player;
import org.unisa.musicplaylistmanager.collections.playlist.model.Playlist;
import org.unisa.musicplaylistmanager.player.state.PlayerState;
import org.unisa.musicplaylistmanager.player.strategy.Shuffle;
import org.unisa.musicplaylistmanager.track.model.Track;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la navigazione delle tracce all'interno del Player.
 * Verifica che il Player interagisca correttamente con l'Iteratore e con le strategie
 * (Sequential e Shuffle), testando casi limite e comportamenti circolari.
 */
public class PlayerNavigationTest {

    private Player player;
    private Playlist playlist;
    private Track track1, track2, track3, track4, track5;

    /**
     *Creiamo un finto stato (Stub) per isolare e testare esclusivamente la logica di navigazione.
     * Dato che il costruttore del Player richiede uno stato iniziale obbligatorio, 
     * passiamo questo Stub il cui metodo execute() è volutamente vuoto. 
     */
    private static class StubState implements PlayerState {
        @Override
        public void execute(Player player) { }
    }

    /**
     * Necessario perché il nextTrack() nel Timer interno del Player chiama Platform.runLater()
     * Se qui non accendessimo forzatamente il toolkit in background,
     * i test di JUnit mostrerebbero l'errore: "IllegalStateException: Toolkit not initialized".
     */
    @BeforeAll
    static void initJFX() {

        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit già inizializzato
        }
    }

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Test Playlist");
        track1 = new Track("Track 1", "Author", Year.of(2021), "Pop", 100, false, false, false);
        track2 = new Track("Track 2", "Author", Year.of(2021), "Pop", 100, false, false, false);
        track3 = new Track("Track 3", "Author", Year.of(2021), "Pop", 100, false, false, false);
        track4 = new Track("Track 4", "Author", Year.of(2021), "Pop", 100, false, false, false);
        track5 = new Track("Track 5", "Author", Year.of(2021), "Pop", 100, false, false, false);

        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        playlist.addTrack(track4);
        playlist.addTrack(track5);

        // Iniziamo dalla traccia 1 (indice 0)
        player = new Player(new StubState(), playlist, track1);
    }

    @AfterEach
    void tearDown() {
        if(player != null) {
            player.terminate();
        }
    }

    @Test
    @DisplayName("Navigazione sequenziale: nextTrack deve seguire l'ordine e ritornare all'inizio")
    void testSequentialNextTrackCircular() {
        assertEquals(track1, player.getCurrentTrack());
        
        player.nextTrack();
        assertEquals(track2, player.getCurrentTrack());
        
        player.nextTrack();
        assertEquals(track3, player.getCurrentTrack());
        
        player.nextTrack();
        assertEquals(track4, player.getCurrentTrack());
        
        player.nextTrack();
        assertEquals(track5, player.getCurrentTrack());

        // Caso limite: superamento della fine della playlist
        player.nextTrack();
        assertEquals(track1, player.getCurrentTrack(), "Deve ricominciare ciclicamente dalla prima traccia");
    }

    @Test
    @DisplayName("Navigazione sequenziale: previousTrack dal primo elemento torna all'ultimo")
    void testSequentialPreviousTrackCircular() {
        assertEquals(track1, player.getCurrentTrack());

        // Caso limite: tornare indietro dal primo elemento
        player.previousTrack();
        assertEquals(track5, player.getCurrentTrack(), "Deve tornare ciclicamente all'ultima traccia");
        
        player.previousTrack();
        assertEquals(track4, player.getCurrentTrack());
    }

    @Test
    @DisplayName("Cambio strategia a Shuffle a runtime preserva la traccia corrente e rimescola le altre")
    void testShuffleStrategyAtRuntime() {
        // Navighiamo alla terza traccia
        player.nextTrack();
        player.nextTrack();
        assertEquals(track3, player.getCurrentTrack());

        // Cambiamo strategia
        player.getIterator().setStrategy(new Shuffle());

        // Dopo il cambio, la traccia corrente DEVE rimanere track3
        assertEquals(track3, player.getCurrentTrack(), "La traccia in riproduzione non deve cambiare attivando lo Shuffle");

        // Navighiamo e verifichiamo che vengano riprodotte tutte le tracce restanti
        Set<Track> playedTracks = new HashSet<>();
        playedTracks.add(player.getCurrentTrack());

        for (int i = 0; i < 4; i++) {
            player.nextTrack();
            playedTracks.add(player.getCurrentTrack());
        }

        // Dopo 4 nextTrack (5 tracce totali), devo aver ascoltato tutte e 5 le tracce
        assertEquals(5, playedTracks.size(), "Lo shuffle deve garantire che tutte le tracce vengano riprodotte esattamente una volta prima di ripetersi");
    }

    // -----------------------------------------------------------------------
    // Riordino delle tracce durante la riproduzione (BUG goNext/goPrevious)
    //
    // L'iteratore segue la traccia corrente tramite la sua POSIZIONE nell'array.
    // Uno swap riordina l'ArrayList condiviso senza variarne la dimensione: l'iteratore
    // rileva la discrepanza e si riallinea automaticamente al brano in riproduzione,
    // così che next/previous restino coerenti col nuovo ordine, senza intervento esterno.
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Riordino in su: spostare la traccia corrente mantiene corretti current e next")
    void testReorderCurrentTrackUpKeepsNavigationConsistent() {
        // Riproduco track3 (posizione 2)
        player.nextTrack();
        player.nextTrack();
        assertEquals(track3, player.getCurrentTrack());

        // Sposto track3 in su (swap con track2) -> ordine: t1, t3, t2, t4, t5
        playlist.swap(2, 1);

        // La traccia corrente resta track3 (riallineamento automatico)
        assertEquals(track3, player.getCurrentTrack(),
                "La traccia in riproduzione non deve cambiare dopo lo spostamento");

        // Il successivo è ora track2 (che segue track3 nel nuovo ordine), non track4
        player.nextTrack();
        assertEquals(track2, player.getCurrentTrack(),
                "Il next deve seguire il nuovo ordine, non saltare una traccia");
    }

    @Test
    @DisplayName("Riordino in giù: spostare la traccia corrente mantiene corretti current e previous")
    void testReorderCurrentTrackDownKeepsNavigationConsistent() {
        // Riproduco track2 (posizione 1)
        player.nextTrack();
        assertEquals(track2, player.getCurrentTrack());

        // Sposto track2 in giù (swap con track3) -> ordine: t1, t3, t2, t4, t5
        playlist.swap(1, 2);

        assertEquals(track2, player.getCurrentTrack(),
                "La traccia in riproduzione non deve cambiare dopo lo spostamento");

        // Il precedente ora è track3 (che precede track2 nel nuovo ordine)
        player.previousTrack();
        assertEquals(track3, player.getCurrentTrack(),
                "Il previous deve seguire il nuovo ordine");
    }

    @Test
    @DisplayName("Riordino di tracce non in riproduzione: la navigazione resta invariata")
    void testReorderOtherTracksDoesNotAffectCurrent() {
        // Riproduco track5 (ultima posizione)
        for (int i = 0; i < 4; i++) player.nextTrack();
        assertEquals(track5, player.getCurrentTrack());

        // Sposto due tracce all'inizio, lontane da quella in riproduzione -> t2, t1, t3, t4, t5
        playlist.swap(0, 1);

        // track5 resta corrente e il next torna ciclicamente alla prima posizione (track2)
        assertEquals(track5, player.getCurrentTrack());
        player.nextTrack();
        assertEquals(track2, player.getCurrentTrack(),
                "Comportamento circolare corretto rispetto al nuovo ordine");
    }

    @Test
    @DisplayName("Riordino ripetuto: spostamenti multipli consecutivi restano coerenti")
    void testRepeatedReordersStayConsistent() {
        // Riproduco track3
        player.nextTrack();
        player.nextTrack();
        assertEquals(track3, player.getCurrentTrack());

        // Spostamenti consecutivi della traccia corrente verso l'alto
        playlist.swap(2, 1); // t1, t3, t2, t4, t5  (track3 in pos 1)
        assertEquals(track3, player.getCurrentTrack());
        playlist.swap(1, 0); // t3, t1, t2, t4, t5  (track3 in pos 0)
        assertEquals(track3, player.getCurrentTrack());

        // Dopo gli spostamenti, il next segue il nuovo ordine: track1
        player.nextTrack();
        assertEquals(track1, player.getCurrentTrack());
    }

    @Test
    @DisplayName("Gestione caso limite: playlist vuota")
    void testEmptyPlaylistNavigation() {
        Playlist emptyPlaylist = new Playlist("Empty");
        
        // Creazione del player simulando l'apertura con una playlist vuota
        Player p = new Player(new StubState(), emptyPlaylist, track1); 
        
        // Assicuriamoci che chiamando next o previous con playlist vuota non generi eccezioni
        assertDoesNotThrow(() -> p.nextTrack(), "Chiamare nextTrack su playlist vuota non deve sollevare eccezioni");
        assertDoesNotThrow(() -> p.previousTrack(), "Chiamare previousTrack su playlist vuota non deve sollevare eccezioni");
        assertNull(p.getCurrentTrack(), "Se la playlist è vuota, getCurrentTrack deve restituire null");
        
        p.terminate();
    }

    @Test
    @DisplayName("Gestione svuotamento improvviso: se la playlist si svuota mentre è in riproduzione")
    void testPlaylistEmptiedDuringPlayback() {
        // Riproduco track2 (posizione 1)
        player.nextTrack();
        assertEquals(track2, player.getCurrentTrack());

        // Svuotamento improvviso della playlist (simulando undo o rimozione globale)
        playlist.getTracks().clear();

        assertDoesNotThrow(() -> player.nextTrack(), "Chiamare nextTrack su playlist svuotata non deve generare IndexOutOfBoundsException");
        assertNull(player.getCurrentTrack(), "Dopo svuotamento, getCurrentTrack deve essere null");
    }
}
