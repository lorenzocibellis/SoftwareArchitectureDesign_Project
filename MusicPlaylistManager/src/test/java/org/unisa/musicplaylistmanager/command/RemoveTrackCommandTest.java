package org.unisa.musicplaylistmanager.command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.command.RemoveTrackCommand;
import org.unisa.musicplaylistmanager.observer.BaseObserver;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.time.Year;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Classe che implementa test per {@link RemoveTrackCommand}
 *
 */
class RemoveTrackCommandTest {

    private Track track1;
    private Track track2;
    private ArrayList<Track> listaTracce;
    private TrackList trackList;
    private ObservableList<Track> obsList;

    @BeforeEach
    void setUp() {
        trackList = TrackList.getTrackListPointer();

        // Svuotiamo il singleton prima del test
        ArrayList<Track> oldList = new ArrayList<>(trackList.getTracks());
        if (!oldList.isEmpty()) {
            trackList.removeAllTracks(oldList);
        }

        track1 = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Year.of(1975),
                "Rock",
                354,
                true,
                false,
                false
        );

        track2 = new Track(
                "Shape of You",
                "Ed Sheeran",
                Year.of(2017),
                "Pop",
                233,
                false,
                false,
                true
        );

        listaTracce = new ArrayList<>();
        listaTracce.add(track1);
        listaTracce.add(track2);

        obsList = FXCollections.observableArrayList();
    }

    /**
     *
     * Verifica il corretto funzionamento di execute().
     * Controlla che le tracce vengano rimosse correttamente sia dal modello dati
     * sia dalla lista osservabile.
     *
     */
    @Test
    void testExecuteStandard() {
        System.out.println("=== INIZIO: testExecuteStandard ===");
        // Popoliamo la TrackList e la lista osservabile con le tracce da rimuovere
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        obsList.addAll(track1, track2);

        RemoveTrackCommand command = new RemoveTrackCommand(listaTracce, trackList, obsList);

        // Verifichiamo lo stato iniziale
        assertTrue(obsList.contains(track1));
        assertTrue(obsList.contains(track2));

        // Esecuzione della rimozione
        command.execute();

        // Le tracce non devono più essere presenti nella lista osservabile
        assertFalse(obsList.contains(track1),
                "La lista osservabile non deve più contenere track1 dopo la rimozione.");
        assertFalse(obsList.contains(track2),
                "La lista osservabile non deve più contenere track2 dopo la rimozione.");

        // Le tracce non devono più essere presenti nel modello dati
        assertFalse(trackList.getTracks().contains(track1),
                "La TrackList non deve più contenere track1 dopo la rimozione.");
        assertFalse(trackList.getTracks().contains(track2),
                "La TrackList non deve più contenere track2 dopo la rimozione.");
        System.out.println("=== FINE: testExecuteStandard ===\n");
    }

    /**
     *
     * Verifica il corretto funzionamento di execute() quando la collezione è una Playlist.
     * Controlla che le tracce vengano rimosse anche da una TrackCollection generica.
     *
     */
    @Test
    void testExecuteConPlaylist() {
        System.out.println("=== INIZIO: testExecuteConPlaylist ===");
        Playlist playlist = new Playlist("TestPlaylist");
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        obsList.addAll(track1, track2);

        RemoveTrackCommand command = new RemoveTrackCommand(listaTracce, playlist, obsList);

        command.execute();

        assertFalse(obsList.contains(track1),
                "La lista osservabile non deve più contenere track1 dopo la rimozione dalla playlist.");
        assertFalse(obsList.contains(track2),
                "La lista osservabile non deve più contenere track2 dopo la rimozione dalla playlist.");
        assertFalse(playlist.getTracks().contains(track1),
                "La playlist non deve più contenere track1.");
        assertFalse(playlist.getTracks().contains(track2),
                "La playlist non deve più contenere track2.");
        System.out.println("=== FINE: testExecuteConPlaylist ===\n");
    }

    /**
     *
     * Verifica che execute() attivi la notifica agli observer quando la collezione è una TrackList.
     * Controlla che il branch di notifica per il pattern Observer venga percorso correttamente.
     *
     */
    @Test
    void testExecuteNotificaObserver() {
        System.out.println("=== INIZIO: testExecuteNotificaObserver ===");
        trackList.addTrack(track1);
        trackList.addTrack(track2);

        // Observer di test che conta le notifiche ricevute
        ArrayList<Track> tracceNotificate = new ArrayList<>();
        BaseObserver observer = tracceNotificate::add;
        trackList.attach(observer);

        RemoveTrackCommand command = new RemoveTrackCommand(listaTracce, trackList, obsList);
        command.execute();

        // Verifica che l'observer sia stato notificato per ogni traccia rimossa
        assertTrue(tracceNotificate.contains(track1),
                "L'observer deve essere notificato della rimozione di track1.");
        assertTrue(tracceNotificate.contains(track2),
                "L'observer deve essere notificato della rimozione di track2.");

        trackList.detach(observer);
        System.out.println("=== FINE: testExecuteNotificaObserver ===\n");
    }

    /**
     *
     * Verifica il corretto funzionamento di undo().
     * Controlla che l'annullamento della rimozione ripristini le tracce
     * sia nel modello dati sia nella lista osservabile.
     *
     */
    @Test
    void testUndoStandard() {
        System.out.println("=== INIZIO: testUndoStandard ===");
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        obsList.addAll(track1, track2);

        RemoveTrackCommand command = new RemoveTrackCommand(listaTracce, trackList, obsList);

        // Rimuoviamo le tracce
        command.execute();
        assertFalse(obsList.contains(track1));
        assertFalse(obsList.contains(track2));

        // Annulliamo la rimozione
        command.undo();

        // Le tracce devono essere state reinserite
        assertTrue(obsList.contains(track1),
                "La lista osservabile deve contenere nuovamente track1 dopo undo().");
        assertTrue(obsList.contains(track2),
                "La lista osservabile deve contenere nuovamente track2 dopo undo().");
        assertTrue(trackList.getTracks().contains(track1),
                "La TrackList deve contenere nuovamente track1 dopo undo().");
        assertTrue(trackList.getTracks().contains(track2),
                "La TrackList deve contenere nuovamente track2 dopo undo().");
        System.out.println("=== FINE: testUndoStandard ===\n");
    }

    /**
     *
     * Verifica il comportamento di execute() se la lista di tracce è nulla.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testExecuteConTracceNull() {
        RemoveTrackCommand command = new RemoveTrackCommand(null, trackList, obsList);

        assertThrows(IllegalArgumentException.class, () -> command.execute(),
                "Il metodo execute() deve sollevare IllegalArgumentException se la lista delle tracce è null.");
    }

    /**
     *
     * Verifica il comportamento di undo() se la lista di tracce è nulla.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testUndoConTracceNull() {
        RemoveTrackCommand command = new RemoveTrackCommand(null, trackList, obsList);

        assertThrows(IllegalArgumentException.class, () -> command.undo(),
                "Il metodo undo() deve sollevare IllegalArgumentException se la lista delle tracce è null.");
    }

    /**
     *
     * Verifica che execute() e undo() non lancino eccezioni bloccanti se i contenitori sono nulli.
     *
     */
    @Test
    void testTolleranzaContenitoriNull() {
        RemoveTrackCommand command = new RemoveTrackCommand(listaTracce, null, null);

        assertDoesNotThrow(() -> command.execute(),
                "Il comando deve poter eseguire execute() in sicurezza se i contenitori sono nulli.");

        assertDoesNotThrow(() -> command.undo(),
                "Il comando deve poter eseguire undo() in sicurezza se i contenitori sono nulli.");
    }
}
