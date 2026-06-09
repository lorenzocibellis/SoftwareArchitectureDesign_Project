package org.unisa.musicplaylistmanager.command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.command.DeletePlaylistCommand;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Classe di test per {@link DeletePlaylistCommand}
 *
 */
class DeletePlaylistCommandTest {

    //Attributi

    private Playlist playlist1;
    private Playlist playlist2;
    private ArrayList<Playlist> listaPlaylists;
    private PlaylistList playlistList;
    private ObservableList<Playlist> obsList;

    @BeforeEach
    void setUp() {
        // Inizializzazione degli oggetti necessari ai test
        playlist1 = new Playlist("p1");
        playlist2 = new Playlist("p2");

        listaPlaylists = new ArrayList<>();
        listaPlaylists.add(playlist1);
        listaPlaylists.add(playlist2);

        playlistList = PlaylistList.getPlaylistListPointer();
        obsList = FXCollections.observableArrayList();

        // Se sono presenti playlist, svuotiamo la lista di playlist
        // Usiamo una hard copy dell'arraylist
        ArrayList<Playlist> oldList = new ArrayList<>(playlistList.getPlaylists());
        if (!oldList.isEmpty()){
            playlistList.deletePlaylists(oldList);
        }

        // Popoliamo inizialmente le liste per simulare la presenza di playlist da eliminare
        playlistList.addPlaylist(playlist1);
        playlistList.addPlaylist(playlist2);
        obsList.addAll(playlist1, playlist2);
    }

    /**
     *
     * Verifica il corretto funzionamento di execute().
     * Controlla che le playlist passate vengano rimosse correttamente sia dal modello dati
     * sia dalla lista osservabile.
     *
     */
    @Test
    void testExecuteStandard() {
        System.out.println("=== INIZIO: testExecuteStandard ===");
        // creo il comando
        DeletePlaylistCommand command = new DeletePlaylistCommand(listaPlaylists, playlistList, obsList);

        // Verifichiamo lo stato iniziale
        assertTrue(obsList.contains(playlist1));
        assertTrue(obsList.contains(playlist2));

        // Esecuzione dell'eliminazione
        command.execute();

        // Le playlist non devono più essere presenti nella lista osservabile
        assertFalse(obsList.contains(playlist1),
                "La lista osservabile non deve più contenere playlist1 dopo l'eliminazione.");
        assertFalse(obsList.contains(playlist2),
                "La lista osservabile non deve più contenere playlist2 dopo l'eliminazione.");
        System.out.println("=== FINE: testExecuteStandard ===\n");

    }

    /**
     *
     * Verifica il corretto funzionamento di undo().
     * Controlla che l'annullamento dell'eliminazione ripristini e inserisca nuovamente
     * tutte le playlist eliminate all'interno delle rispettive liste.
     *
     */
    @Test
    void testUndoStandard() {
        System.out.println("=== INIZIO: testUndoStandard ===");
        DeletePlaylistCommand command = new DeletePlaylistCommand(listaPlaylists, playlistList, obsList);

        // Eliminiamo le playlist
        command.execute();
        assertFalse(obsList.contains(playlist1));
        assertFalse(obsList.contains(playlist2));

        // Esecuzione del ripristino (undo)
        command.undo();

        // Le playlist rimosse devono essere state correttamente reinserite
        assertTrue(obsList.contains(playlist1),
                "La lista osservabile deve contenere nuovamente playlist1 dopo il metodo undo().");
        assertTrue(obsList.contains(playlist2),
                "La lista osservabile deve contenere nuovamente playlist2 dopo il metodo undo().");
        System.out.println("=== FINE: testUndoStandard ===\n");
    }

    /**
     *
     * Verifica il comportamento di execute() se l'insieme di playlist è nullo.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testExecuteConInsiemePlaylistsNull() {
        DeletePlaylistCommand command = new DeletePlaylistCommand(null, playlistList, obsList);

        assertThrows(IllegalArgumentException.class, () -> command.execute(),
                "Il metodo execute() deve sollevare IllegalArgumentException se l'insieme di playlist da eliminare è null.");
    }

    /**
     *
     * Verifica il comportamento di undo() se l'insieme di playlist è nullo.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testUndoConInsiemePlaylistsNull() {
        DeletePlaylistCommand command = new DeletePlaylistCommand(null, playlistList, obsList);

        assertThrows(IllegalArgumentException.class, () -> command.undo(),
                "Il metodo undo() deve sollevare IllegalArgumentException se l'insieme di playlist da ripristinare è null.");
    }

    /**
     *
     * Tolleranza e robustezza ai riferimenti nulli facoltativi.
     * Questo test verifica che l'applicazione non lanci eccezioni bloccanti se i contenitori
     * dei dati o della UI sono null.
     *
     */
    @Test
    void testTolleranzaContenitoriNull() {
        // Passiamo un insieme valido di playlist, ma impostiamo a null i contenitori di destinazione
        DeletePlaylistCommand command = new DeletePlaylistCommand(listaPlaylists, null, null);

        // I metodi non devono sollevare eccezioni
        assertDoesNotThrow(() -> command.execute(),
                "Il comando deve eseguire l'operazione anche se la lista dati o l'interfaccia sono nulle.");

        assertDoesNotThrow(() -> command.undo(),
                "Il comando deve poter effettuare sicurezza anche se la lista dati o l'interfaccia sono nulle.");
    }
}