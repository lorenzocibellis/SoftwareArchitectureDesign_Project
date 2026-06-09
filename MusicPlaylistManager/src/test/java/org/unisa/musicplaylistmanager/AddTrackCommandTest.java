package org.unisa.musicplaylistmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.command.AddTrackCommand;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.time.Year;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Classe che implementa test per {@link AddTrackCommand}
 *
 */
class AddTrackCommandTest {

    private Track track1;
    private Track track2;
    private ArrayList<Track> listaTracce;

    // Utilizziamo TrackList direttamente per poter coprire il branch di notifica in undo()
    private TrackList trackList;
    private ObservableList<Track> obsList;

    @BeforeEach
    void setUp() {
        // Otteniamo il puntatore della libreria principale
        trackList = TrackList.getTrackListPointer();

        // Svuotiamo il singleton prima del test
        ArrayList<Track> oldList = new ArrayList<>(trackList.getTracks());
        if (!oldList.isEmpty()){
            trackList.removeAllTracks(oldList);
        }

        // Inizializzazione degli oggetti per i test
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
     * (Costruttore 1): Verifica l'aggiunta di una traccia.
     *
     */
    @Test
    void testExecuteConSingolaTraccia() {
        System.out.println("=== INIZIO: testExecuteConSingolaTraccia ===");
        // Utilizziamo il primo costruttore che accetta un singolo oggetto Track
        AddTrackCommand command = new AddTrackCommand(track1, trackList, obsList);

        command.execute();

        assertTrue(obsList.contains(track1),
                "La lista osservabile deve contenere la singola traccia.");
        System.out.println("=== FINE: testExecuteConSingolaTraccia ===\n");
    }

    /**
     *
     * (Costruttore 2): Verifica l'aggiunta di una lista di tracce.
     */
    @Test
    void testExecuteConListaTracce() {
        System.out.println("=== INIZIO: testExecuteConListaTracce ===");
        // Utilizziamo il secondo costruttore che accetta un ArrayList di Track
        AddTrackCommand command = new AddTrackCommand(listaTracce, trackList, obsList);

        command.execute();

        assertTrue(obsList.contains(track1), "La lista osservabile deve contenere track1.");
        assertTrue(obsList.contains(track2), "La lista osservabile deve contenere track2.");
        assertEquals(2, obsList.size(), "La lista osservabile deve contenere 2 tracce.");
        System.out.println("=== FINE: testExecuteConListaTracce ===\n");
    }

    /**
     *
     * Verifica l'annullamento dell'aggiunta.
     *
     */
    @Test
    void testUndoStandard() {
        System.out.println("=== INIZIO: testUndoStandard ===");
        AddTrackCommand command = new AddTrackCommand(listaTracce, trackList, obsList);

        // Prepariamo lo stato: eseguiamo l'aggiunta
        command.execute();
        assertEquals(2, obsList.size());

        // Azione: Annulliamo l'aggiunta
        command.undo();

        // Verifica
        assertTrue(obsList.isEmpty(),
                "La lista osservabile deve essere vuota dopo il ripristino.");
        System.out.println("=== FINE: testUndoStandard ===\n");
    }

    /**
     *
     * Verifica execute() con riferimento alle tracce nullo.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testExecuteConTracceNull() {
        // Inietto il null direttamente dalla porta principale (il costruttore pubblico)
        AddTrackCommand command = new AddTrackCommand((ArrayList<Track>) null, trackList, obsList);

        assertThrows(IllegalArgumentException.class, () -> command.execute());
    }


    /**
     *
     * Verifica undo() con riferimento alle tracce nullo.
     * Lancio previsto: IllegalArgumentException.
     *
     */
    @Test
    void testUndoConTracceNull() {
        AddTrackCommand command = new AddTrackCommand((ArrayList<Track>) null, trackList, obsList);

        assertThrows(IllegalArgumentException.class, () -> command.undo(),
                "Il metodo undo() deve sollevare IllegalArgumentException se la lista delle tracce è nulla.");
    }

    /**
     *
     * Verifica che execute() e undo() non lancino eccezioni bloccanti se le destinazioni (UI o modello) sono nulle.
     *
     */
    @Test
    void testTolleranzaContenitoriNull() {
        // Costruiamo il comando con tracce valide ma senza referenze al modello e alla UI
        AddTrackCommand command = new AddTrackCommand(listaTracce, null, null);

        assertDoesNotThrow(() -> command.execute(),
                "Il comando deve poter eseguire execute() in sicurezza saltando l'aggiunta se i contenitori sono nulli.");

        assertDoesNotThrow(() -> command.undo(),
                "Il comando deve poter eseguire undo() in sicurezza saltando la rimozione se i contenitori sono nulli.");
    }
}