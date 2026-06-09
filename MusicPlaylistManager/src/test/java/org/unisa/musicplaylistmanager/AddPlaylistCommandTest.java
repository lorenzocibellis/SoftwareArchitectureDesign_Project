package org.unisa.musicplaylistmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.command.AddPlaylistCommand;
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Classe di test per {@link AddPlaylistCommand}
 *
 */
class AddPlaylistCommandTest {

    //Attributi
    private Playlist playlist;
    private PlaylistList playlistList;
    private ObservableList<Playlist> obsList;

    @BeforeEach
    void setUp() {
        // Inizializzazione degli oggetti reali necessari ai test.
        playlist = new Playlist("PlaylistProva");
        playlistList = PlaylistList.getPlaylistListPointer();
        obsList = FXCollections.observableArrayList();
    }

    /**
     *
     * Verifica il corretto funzionamento di execute().
     * Controlla che la playlist venga inserita correttamente sia nella lista visiva (UI)
     * sia nel modello dati.
     *
     */
    @Test
    void testExecuteStandard() {
        System.out.println("=== INIZIO: testExecuteStandard ===");
        AddPlaylistCommand command = new AddPlaylistCommand(playlist, playlistList, obsList);

        // Esecuzione del comando
        command.execute();

        // Verifica che la playlist sia presente nella lista osservabile
        assertTrue(obsList.contains(playlist),
                "La lista osservabile deve contenere la playlist dopo il metodo execute().");
        System.out.println("=== FINE: testExecuteStandard ===\n");
    }

    /**
     *
     * Verifica il corretto funzionamento di undo().
     * Controlla che l'annullamento rimuova correttamente la playlist precedentemente aggiunta.
     *
     */
    @Test
    void testUndoStandard() {
        System.out.println("=== INIZIO: testUndoStandard ===");
        AddPlaylistCommand command = new AddPlaylistCommand(playlist, playlistList, obsList);

        // Simuliamo l'inserimento per impostare lo stato iniziale
        command.execute();
        assertTrue(obsList.contains(playlist));

        // Annullamento dell'inserimento (undo)
        command.undo();

        // La playlist deve essere stata rimossa dalla lista osservabile
        assertFalse(obsList.contains(playlist),
                "La lista osservabile non deve più contenere la playlist dopo il metodo undo().");
        System.out.println("=== FINE: testUndoStandard ===\n");
    }

    /**
     *
     * Verifica il comportamento di execute() se la lista delle playlist è nulla.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testExecuteConListaPlaylistsNull() {
        AddPlaylistCommand command = new AddPlaylistCommand(playlist, playlistList, obsList);

        // Forziamo lo stato limite impostando a null la lista delle playlist
        command.setPlaylists(null);

        assertThrows(IllegalArgumentException.class, () -> command.execute(),
                "Il metodo execute() deve sollevare IllegalArgumentException se la lista delle playlist è nulla.");
    }

    /**
     *
     * Verifica il comportamento di undo() se la lista delle playlist è nulla.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testUndoConListaPlaylistsNull() {
        AddPlaylistCommand command = new AddPlaylistCommand(playlist, playlistList, obsList);
        command.setPlaylists(null);

        assertThrows(IllegalArgumentException.class, () -> command.undo(),
                "Il metodo undo() deve sollevare IllegalArgumentException se la lista delle playlist è nulla.");
    }

    /**
     *
     * Verifica l'execute() se l'elemento all'indice 0 della lista è nullo.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testExecuteConPlaylistNull() {
        ArrayList<Playlist> listaElemNull = new ArrayList<>();
        listaElemNull.add(null); // Inseriamo un valore null

        AddPlaylistCommand command = new AddPlaylistCommand(playlist, playlistList, obsList);
        command.setPlaylists(listaElemNull);

        assertThrows(IllegalArgumentException.class, () -> command.execute(),
                "Il metodo execute() deve sollevare IllegalArgumentException se l'oggetto Playlist estratto è null.");
    }

    /**
     *
     * Verifica l'undo() se l'elemento all'indice 0 della lista è nullo.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testUndoConPlaylistNull() {
        ArrayList<Playlist> listaConElementoNull = new ArrayList<>();
        listaConElementoNull.add(null);

        AddPlaylistCommand command = new AddPlaylistCommand(playlist, playlistList, obsList);
        command.setPlaylists(listaConElementoNull);

        assertThrows(IllegalArgumentException.class, () -> command.undo(),
                "Il metodo undo() deve sollevare IllegalArgumentException se l'oggetto Playlist estratto è null.");
    }

    /**
     *
     * Tolleranza e robustezza ai riferimenti nulli facoltativi.
     * Questo test si assicura che il comando non lanci un'eccezione bloccante (NullPointerException) se la UI
     * o il modello non definiti al momento della creazione del coomando.
     *
     *
     */
    @Test
    void testTolleranzaModelloEInterfacciaNull() {
        // Passiamo riferimenti nulli per PlaylistList e ObservableList nel costruttore
        AddPlaylistCommand command = new AddPlaylistCommand(playlist, null, null);

        // Grazie alle tue protezioni condizionali 'if', execute e undo non devono lanciare eccezioni
        assertDoesNotThrow(() -> command.execute(),
                "Il comando deve gestire ed eseguire l'operazione anche se il modello o l'interfaccia sono nulli.");

        assertDoesNotThrow(() -> command.undo(),
                "Il comando deve poter effettuare l'undo anche se il modello o l'interfaccia sono nulli.");
    }
}