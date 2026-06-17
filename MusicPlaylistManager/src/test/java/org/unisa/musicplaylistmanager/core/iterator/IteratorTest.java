package org.unisa.musicplaylistmanager.core.iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.unisa.musicplaylistmanager.collections.playlist.model.Playlist;
import org.unisa.musicplaylistmanager.player.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.player.strategy.Loop;
import org.unisa.musicplaylistmanager.player.strategy.Sequential;
import org.unisa.musicplaylistmanager.track.model.Track;

import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Iterator}.
 * @author gruppo10
 */
class IteratorTest {
    
    private Playlist playlist;
    private Iterator iterator;
    private Track track1, track2, track3;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Test Playlist");

        track1 = new Track("Bohemian Rhapsody",  "Queen",        Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);
        track3 = new Track("Hotel California",   "Eagles",       Year.of(1977), "Rock", 391, false, false, true);

        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);

        iterator = new Iterator(playlist);
    }

    @Test
    @DisplayName("GetCurrent: dovrebbe restituire la traccia corretta")
    void testGetCurrent() {
        assertNotNull(iterator.getCurrent());
        assertEquals(track1, iterator.getCurrent());
    }

    @Test
    @DisplayName("GetNext: test comportamento circolare")
    void testGetNext_CircularBehavior() {
        assertEquals(track2, iterator.getNext());
        assertEquals(track3, iterator.getNext());
        assertEquals(track1, iterator.getNext());
    }

    @Test
    @DisplayName("GetPrevious: test comportamento circolare")
    void testGetPrevious_CircularBehavior() {
        assertEquals(track3, iterator.getPrevious());
        assertEquals(track2, iterator.getPrevious());
        assertEquals(track1, iterator.getPrevious());
    }

    @Test
    @DisplayName("EmptyPlaylist: gestisce playlist vuota")
    void testEmptyPlaylist() {
        Playlist emptyPlaylist = new Playlist("Vuota");
        Iterator emptyIterator = new Iterator(emptyPlaylist);
        assertNull(emptyIterator.getCurrent());
    }

    @Test
    @DisplayName("SetStrategy: applica correttamente una strategia")
    void testSetStrategy() {
        ExecutionStrategy reverseStrategy = (size, currentIndex) -> new int[]{2, 1, 0};
        iterator.setStrategy(reverseStrategy);
        // Dopo il cambio strategia, la traccia corrente deve essere preservata (track1)
        assertEquals(track1, iterator.getCurrent());
    }

    @Test
    @DisplayName("Constructor: oggetto non nullo")
    void testConstructorNotNull() {
        assertNotNull(iterator);
    }

    // -----------------------------------------------------------------------
    // getIdentifier
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getIdentifier: restituisce il nome della collezione")
    void testGetIdentifier() {
        assertEquals("Test Playlist", iterator.getIdentifier());
    }

    // -----------------------------------------------------------------------
    // moveToTrack
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("moveToTrack: posiziona l'iteratore sulla traccia indicata")
    void testMoveToTrack() {
        iterator.moveToTrack(track3);
        assertEquals(track3, iterator.getCurrent());

        // Da track3 il successivo (circolare) è track1
        assertEquals(track1, iterator.getNext());
    }

    @Test
    @DisplayName("moveToTrack: una traccia non presente non sposta l'iteratore")
    void testMoveToTrackNotPresent() {
        assertEquals(track1, iterator.getCurrent());

        Track estranea = new Track("Imagine", "John Lennon", Year.of(1971), "Pop", 183, false, false, false);
        iterator.moveToTrack(estranea);

        // L'iteratore deve restare sulla traccia corrente precedente
        assertEquals(track1, iterator.getCurrent());
    }

    // -----------------------------------------------------------------------
    // Sincronizzazione dinamica (syncWithPlaylist)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Resync: una traccia aggiunta a runtime diventa raggiungibile")
    void testDynamicResyncAfterAdd() {
        Track track4 = new Track("Imagine", "John Lennon", Year.of(1971), "Pop", 183, false, false, false);
        playlist.addTrack(track4);

        // La prima chiamata dopo la modifica forza il ricalcolo dell'ordine di iterazione
        assertEquals(track1, iterator.getCurrent(), "La traccia corrente deve essere preservata");

        java.util.Set<Track> visitate = new java.util.HashSet<>();
        visitate.add(iterator.getCurrent());
        for (int i = 0; i < 3; i++) {
            visitate.add(iterator.getNext());
        }

        assertTrue(visitate.contains(track4), "La nuova traccia deve essere raggiungibile dall'iteratore");
        assertEquals(4, visitate.size(), "Devono essere raggiungibili tutte e 4 le tracce");
    }

    @Test
    @DisplayName("Resync: una traccia rimossa a runtime non viene più restituita")
    void testDynamicResyncAfterRemove() {
        // Rimuoviamo una traccia diversa da quella corrente (track1)
        playlist.removeTrack(track3);

        assertEquals(track1, iterator.getCurrent());

        java.util.Set<Track> visitate = new java.util.HashSet<>();
        visitate.add(iterator.getCurrent());
        for (int i = 0; i < 3; i++) {
            visitate.add(iterator.getNext());
        }

        assertFalse(visitate.contains(track3), "La traccia rimossa non deve più comparire");
        assertEquals(2, visitate.size(), "Devono restare solo le 2 tracce ancora presenti");
    }

    // -----------------------------------------------------------------------
    // Strategie applicate tramite l'Iterator
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Strategy Loop: getNext restituisce sempre la traccia corrente")
    void testLoopStrategyIntegration() {
        iterator.moveToTrack(track2);
        iterator.setStrategy(new Loop());

        assertEquals(track2, iterator.getCurrent());
        assertEquals(track2, iterator.getNext());
        assertEquals(track2, iterator.getNext());
        assertEquals(track2, iterator.getPrevious());
    }

    @Test
    @DisplayName("Strategy Sequential: ripristina l'ordine naturale dopo un'altra strategia")
    void testSequentialStrategyRestoresOrder() {
        iterator.setStrategy(new Loop());
        assertEquals(track1, iterator.getNext()); // in loop resta su track1

        iterator.setStrategy(new Sequential());
        assertEquals(track1, iterator.getCurrent());
        assertEquals(track2, iterator.getNext());
        assertEquals(track3, iterator.getNext());
    }

    // -----------------------------------------------------------------------
    // Riordino della lista (swap) durante l'iterazione
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Riordino: spostare la traccia corrente la mantiene corrente e aggiorna il next")
    void testReorderCurrentTrackKeepsCurrentAndNext() {
        // Posiziono l'iteratore su track2 (indice 1)
        iterator.moveToTrack(track2);
        assertEquals(track2, iterator.getCurrent());

        // Sposto track2 in cima scambiandola con track1 -> ordine: track2, track1, track3
        playlist.swap(0, 1);

        // L'iteratore segue il brano: track2 resta corrente
        assertEquals(track2, iterator.getCurrent());
        // Il successivo è ora track1 (che segue track2 nel nuovo ordine)
        assertEquals(track1, iterator.getNext());
    }

    @Test
    @DisplayName("Riordino: scambiare due tracce non correnti non altera la corrente")
    void testReorderOtherTracksKeepsCurrent() {
        iterator.moveToTrack(track1);
        assertEquals(track1, iterator.getCurrent());

        // Scambio track2 e track3 (entrambe diverse dalla corrente) -> track1, track3, track2
        playlist.swap(1, 2);

        assertEquals(track1, iterator.getCurrent());
        assertEquals(track3, iterator.getNext());
        assertEquals(track2, iterator.getNext());
    }

    @Test
    @DisplayName("Riordino in Loop: spostare la traccia in loop continua a ripetere quella giusta")
    void testReorderUnderLoopStrategy() {
        iterator.moveToTrack(track2);
        iterator.setStrategy(new Loop());
        assertEquals(track2, iterator.getCurrent());

        // Sposto track2 -> ordine: track2, track1, track3
        playlist.swap(0, 1);

        // Il loop deve continuare a ripetere track2, non un altro brano
        assertEquals(track2, iterator.getCurrent());
        assertEquals(track2, iterator.getNext());
        assertEquals(track2, iterator.getNext());
    }

    @Test
    @DisplayName("setStrategy: su playlist vuota non causa errori")
    void testSetStrategyOnEmptyPlaylist() {
        Playlist vuota = new Playlist("Vuota");
        Iterator itVuoto = new Iterator(vuota);

        assertDoesNotThrow(() -> itVuoto.setStrategy(new Loop()));
        assertNull(itVuoto.getCurrent());
    }
}