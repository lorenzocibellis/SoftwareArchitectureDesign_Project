package org.unisa.musicplaylistmanager.command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.command.RemoveTrackCommand;
import org.unisa.musicplaylistmanager.observer.BaseObserver;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;
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

        // Scollega e rimuove tutte le playlist prima di svuotare la TrackList,
        // per evitare che la notifica observer interferisca con il cleanup
        PlaylistList playlistList = PlaylistList.getPlaylistListPointer();
        ArrayList<Playlist> existingPlaylists = new ArrayList<>(playlistList.getPlaylists());
        for (Playlist p : existingPlaylists) {
            trackList.detach(p);
            playlistList.deletePlaylist(p);
        }

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

    /**
     *
     * Verifica che undo() ripristini la traccia nella playlist che la conteneva prima
     * dell'eliminazione. La rimozione dalla TrackList notifica le playlist (Observer),
     * che rimuovono la traccia da sé stesse; l'undo deve invertire anche questo effetto.
     *
     */
    @Test
    void testUndoRipristinaTracceNellePlaylistObserver() {
        System.out.println("=== INIZIO: testUndoRipristinaTracceNellePlaylistObserver ===");
        trackList.addTrack(track1);
        trackList.addTrack(track2);
        obsList.addAll(track1, track2);

        Playlist playlist = new Playlist("PlaylistTest");
        TrackList.getTrackListPointer().attach(playlist);
        PlaylistList.getPlaylistListPointer().addPlaylist(playlist);
        playlist.addTrack(track1);

        RemoveTrackCommand command = new RemoveTrackCommand(listaTracce, trackList, obsList);

        // execute() rimuove da TrackList → l'Observer notifica la playlist → playlist rimuove track1
        command.execute();
        assertFalse(playlist.getTracks().contains(track1),
                "Dopo execute(), track1 deve essere stata rimossa dalla playlist via Observer.");
        assertFalse(playlist.getTracks().contains(track2),
                "track2 non era nella playlist, non vi deve essere neanche dopo execute().");

        // undo() deve ripristinare track1 nella playlist
        command.undo();
        assertTrue(playlist.getTracks().contains(track1),
                "Dopo undo(), track1 deve essere ripristinata nella playlist.");
        assertFalse(playlist.getTracks().contains(track2),
                "Dopo undo(), track2 non deve essere nella playlist poiché non vi era mai stata.");
        System.out.println("=== FINE: testUndoRipristinaTracceNellePlaylistObserver ===\n");
    }

    /**
     *
     * Verifica che undo() ripristini una traccia in più playlist simultaneamente,
     * quando la traccia era presente in ciascuna di esse prima della rimozione.
     *
     */
    @Test
    void testUndoRipristinaTracceInPiuPlaylist() {
        System.out.println("=== INIZIO: testUndoRipristinaTracceInPiuPlaylist ===");
        trackList.addTrack(track1);
        obsList.add(track1);

        PlaylistList playlistList = PlaylistList.getPlaylistListPointer();
        Playlist playlist1 = new Playlist("Playlist1");
        Playlist playlist2 = new Playlist("Playlist2");
        TrackList.getTrackListPointer().attach(playlist1);
        TrackList.getTrackListPointer().attach(playlist2);
        playlistList.addPlaylist(playlist1);
        playlistList.addPlaylist(playlist2);
        playlist1.addTrack(track1);
        playlist2.addTrack(track1);

        ArrayList<Track> singleTrack = new ArrayList<>();
        singleTrack.add(track1);
        RemoveTrackCommand command = new RemoveTrackCommand(singleTrack, trackList, obsList);

        command.execute();
        assertFalse(playlist1.getTracks().contains(track1),
                "Dopo execute(), track1 non deve essere in playlist1.");
        assertFalse(playlist2.getTracks().contains(track1),
                "Dopo execute(), track1 non deve essere in playlist2.");

        command.undo();
        assertTrue(playlist1.getTracks().contains(track1),
                "Dopo undo(), track1 deve essere ripristinata nella playlist1.");
        assertTrue(playlist2.getTracks().contains(track1),
                "Dopo undo(), track1 deve essere ripristinata nella playlist2.");
        System.out.println("=== FINE: testUndoRipristinaTracceInPiuPlaylist ===\n");
    }

    /**
     *
     * Verifica che undo() non generi eccezioni quando la traccia rimossa non era
     * presente in nessuna playlist.
     *
     */
    @Test
    void testUndoConTracciaInNessunaPlaylist() {
        System.out.println("=== INIZIO: testUndoConTracciaInNessunaPlaylist ===");
        trackList.addTrack(track1);
        obsList.add(track1);

        Playlist playlist = new Playlist("PlaylistVuota");
        TrackList.getTrackListPointer().attach(playlist);
        PlaylistList.getPlaylistListPointer().addPlaylist(playlist);

        ArrayList<Track> singleTrack = new ArrayList<>();
        singleTrack.add(track1);
        RemoveTrackCommand command = new RemoveTrackCommand(singleTrack, trackList, obsList);

        command.execute();

        assertDoesNotThrow(() -> command.undo(),
                "undo() non deve lanciare eccezioni quando la traccia non era in nessuna playlist.");
        assertFalse(playlist.getTracks().contains(track1),
                "track1 non deve essere nella playlist, poiché non vi era prima della rimozione.");
        System.out.println("=== FINE: testUndoConTracciaInNessunaPlaylist ===\n");
    }
}
