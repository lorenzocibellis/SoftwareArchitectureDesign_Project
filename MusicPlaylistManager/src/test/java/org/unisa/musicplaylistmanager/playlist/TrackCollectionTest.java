package org.unisa.musicplaylistmanager.playlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.core.iterator.AbstractIterator;
import org.unisa.musicplaylistmanager.track.model.Track;
import org.unisa.musicplaylistmanager.collections.TrackCollection;
import org.unisa.musicplaylistmanager.collections.playlist.model.Playlist;
import org.unisa.musicplaylistmanager.collections.tracklist.model.TrackList;

import java.time.Year;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe astratta {@link TrackCollection}.
 *
 * Poiché {@code TrackCollection} è astratta, la si collauda attraverso la sua
 * implementazione concreta canonica {@link Playlist}, concentrandosi sui metodi
 * ereditati che non sono già coperti da {@link PlaylistTest}
 * (swap, getIndex, removeAllTracks, updateTrack, nameProperty, createIterator, ...).
 *
 * @author gruppo10
 */
class TrackCollectionTest {

    private TrackCollection collection;
    private Track track1;
    private Track track2;
    private Track track3;

    @BeforeEach
    void setUp() {
        // Isola lo stato del Singleton TrackList per evitare che la propagazione
        // interna di updateTrack interferisca con eventuali tracce lasciate da altri test.
        if (TrackList.exists()) {
            TrackList.getTrackListPointer().getTracks().clear();
        }

        collection = new Playlist("Collezione di Test");

        track1 = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 354, true, false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);
        track3 = new Track("Hotel California", "Eagles", Year.of(1977), "Rock", 391, false, false, true);

        collection.addTrack(track1);
        collection.addTrack(track2);
        collection.addTrack(track3);
    }

    // -----------------------------------------------------------------------
    // Costruzione / nome
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getName: restituisce il nome impostato dal costruttore")
    void testGetName() {
        assertEquals("Collezione di Test", collection.getName());
    }

    @Test
    @DisplayName("nameProperty: riflette il nome ed è reattiva")
    void testNameProperty() {
        assertNotNull(collection.nameProperty());
        assertEquals("Collezione di Test", collection.nameProperty().get());

        ((Playlist) collection).setName("Nuovo Nome");
        assertEquals("Nuovo Nome", collection.nameProperty().get());
    }

    // -----------------------------------------------------------------------
    // getTracks / getSize
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getTracks: restituisce le tracce nell'ordine di inserimento")
    void testGetTracksOrder() {
        ArrayList<Track> tracks = collection.getTracks();
        assertEquals(3, tracks.size());
        assertSame(track1, tracks.get(0));
        assertSame(track2, tracks.get(1));
        assertSame(track3, tracks.get(2));
    }

    @Test
    @DisplayName("getSize: rispecchia il numero di tracce")
    void testGetSize() {
        assertEquals(3, collection.getSize());
        collection.removeTrack(track1);
        assertEquals(2, collection.getSize());
    }

    // -----------------------------------------------------------------------
    // getIndex
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getIndex: restituisce la posizione corretta di una traccia")
    void testGetIndexPresent() {
        assertEquals(0, collection.getIndex(track1));
        assertEquals(1, collection.getIndex(track2));
        assertEquals(2, collection.getIndex(track3));
    }

    @Test
    @DisplayName("getIndex: restituisce -1 per una traccia non presente")
    void testGetIndexAbsent() {
        Track estranea = new Track("Imagine", "John Lennon", Year.of(1971), "Pop", 183, false, false, false);
        assertEquals(-1, collection.getIndex(estranea));
    }

    // -----------------------------------------------------------------------
    // swap
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("swap: scambia due tracce a indici diversi")
    void testSwapDifferentIndices() {
        collection.swap(0, 2);

        assertSame(track3, collection.getTracks().get(0));
        assertSame(track2, collection.getTracks().get(1));
        assertSame(track1, collection.getTracks().get(2));
    }

    @Test
    @DisplayName("swap: scambio di indici adiacenti")
    void testSwapAdjacent() {
        collection.swap(0, 1);

        assertSame(track2, collection.getTracks().get(0));
        assertSame(track1, collection.getTracks().get(1));
        assertSame(track3, collection.getTracks().get(2));
    }

    @Test
    @DisplayName("swap: scambio di un indice con se stesso non modifica nulla")
    void testSwapSameIndex() {
        collection.swap(1, 1);

        assertSame(track1, collection.getTracks().get(0));
        assertSame(track2, collection.getTracks().get(1));
        assertSame(track3, collection.getTracks().get(2));
    }

    @Test
    @DisplayName("swap: indice fuori dai limiti lancia IndexOutOfBoundsException")
    void testSwapOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> collection.swap(0, 99));
    }

    // -----------------------------------------------------------------------
    // removeAllTracks
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("removeAllTracks: rimuove un sottoinsieme di tracce")
    void testRemoveAllTracksSubset() {
        ArrayList<Track> daRimuovere = new ArrayList<>();
        daRimuovere.add(track1);
        daRimuovere.add(track3);

        collection.removeAllTracks(daRimuovere);

        assertEquals(1, collection.getSize());
        assertTrue(collection.getTracks().contains(track2));
        assertFalse(collection.getTracks().contains(track1));
        assertFalse(collection.getTracks().contains(track3));
    }

    @Test
    @DisplayName("removeAllTracks: una lista vuota non modifica la collezione")
    void testRemoveAllTracksEmpty() {
        collection.removeAllTracks(new ArrayList<>());
        assertEquals(3, collection.getSize());
    }

    @Test
    @DisplayName("removeAllTracks: ignora tracce non presenti")
    void testRemoveAllTracksNotPresent() {
        Track estranea = new Track("Imagine", "John Lennon", Year.of(1971), "Pop", 183, false, false, false);
        ArrayList<Track> daRimuovere = new ArrayList<>();
        daRimuovere.add(estranea);

        collection.removeAllTracks(daRimuovere);
        assertEquals(3, collection.getSize());
    }

    // -----------------------------------------------------------------------
    // deleteAll
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteAll: svuota completamente la collezione")
    void testDeleteAll() {
        collection.deleteAll();
        assertEquals(0, collection.getSize());
        assertTrue(collection.getTracks().isEmpty());
    }

    // -----------------------------------------------------------------------
    // createIterator
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createIterator: restituisce un iteratore funzionante posizionato sulla prima traccia")
    void testCreateIterator() {
        AbstractIterator it = collection.createIterator();

        assertNotNull(it);
        assertEquals(track1, it.getCurrent());
        assertEquals("Collezione di Test", it.getIdentifier());
    }

    // -----------------------------------------------------------------------
    // updateTrack – aggiornamento valido
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateTrack: aggiorna tutti i campi preservando l'identità dell'oggetto")
    void testUpdateTrackKeepsReferenceAndUpdatesFields() {
        Track nuoviDati = new Track("Bohemian Rhapsody (Remaster)", "Queen Remastered",
                Year.of(2011), "Classic Rock", 360, false, true, true);

        collection.updateTrack(track1, nuoviDati);

        // L'oggetto in lista deve essere ancora track1 (stessa identità in memoria)
        assertSame(track1, collection.getTracks().get(0));
        // ...ma con i campi aggiornati
        assertEquals("Bohemian Rhapsody (Remaster)", track1.getTitle());
        assertEquals("Queen Remastered", track1.getAuthor());
        assertEquals(Year.of(2011), track1.getYear());
        assertEquals("Classic Rock", track1.getGenre());
        assertEquals(360, track1.getDuration());
        assertFalse(track1.isFavourite());
        assertTrue(track1.isExplicitContent());
        assertTrue(track1.isNewRelease());
    }

    @Test
    @DisplayName("updateTrack: sostituisce i tag personali con quelli nuovi")
    void testUpdateTrackReplacesPersonalTags() {
        track1.addPersonalTag("Vecchio Tag");

        Track nuoviDati = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 354, true, false, false);
        nuoviDati.addPersonalTag("Energica");
        nuoviDati.addPersonalTag("Anni 70");

        collection.updateTrack(track1, nuoviDati);

        assertEquals(2, track1.getPersonalTags().size());
        assertTrue(track1.getPersonalTags().contains("Energica"));
        assertTrue(track1.getPersonalTags().contains("Anni 70"));
        assertFalse(track1.getPersonalTags().contains("Vecchio Tag"));
    }

    // -----------------------------------------------------------------------
    // updateTrack – rifiuto duplicati
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateTrack: lancia eccezione se la modifica crea un duplicato di un'altra traccia")
    void testUpdateTrackRejectsDuplicate() {
        // Proviamo a trasformare track1 in un duplicato (per titolo/autore/anno) di track2
        Track nuoviDatiDuplicati = new Track("Stairway to Heaven", "Led Zeppelin",
                Year.of(1971), "Blues", 999, true, true, true);

        assertThrows(IllegalArgumentException.class,
                () -> collection.updateTrack(track1, nuoviDatiDuplicati));

        // track1 deve restare invariata dopo il rifiuto
        assertEquals("Bohemian Rhapsody", track1.getTitle());
        assertEquals("Queen", track1.getAuthor());
        assertEquals(Year.of(1975), track1.getYear());
    }

    @Test
    @DisplayName("updateTrack: consente di mantenere la stessa identità (nessun falso duplicato)")
    void testUpdateTrackSameIdentityAllowed() {
        // Aggiorniamo solo campi secondari mantenendo titolo/autore/anno (quindi equals invariato)
        Track nuoviDati = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Opera Rock", 354, false, true, true);

        assertDoesNotThrow(() -> collection.updateTrack(track1, nuoviDati));
        assertEquals("Opera Rock", track1.getGenre());
        assertTrue(track1.isExplicitContent());
    }
}
