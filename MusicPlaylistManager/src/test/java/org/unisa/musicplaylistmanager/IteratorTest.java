package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// Importazione specifica delle classi
import org.unisa.musicplaylistmanager.iterator.Iterator; 
import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.strategy.ExecutionStrategy;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;

class IteratorTest {

    private Playlist playlist;
    private Iterator iterator;
    private Track track1, track2, track3;

    @BeforeEach
    void setUp() {
        // 1. Correzione: Inserito parametro richiesto ("NomePlaylist")
        playlist = new Playlist("La Mia Playlist");
        
        // 2. Correzione: Inseriti gli 8 parametri richiesti dal costruttore di Track
        // Adatta questi valori ai tuoi dati reali
        track1 = new Track("Titolo1", "Artista1", Year.of(2026), "Album1", 180, false, false, false);
        track2 = new Track("Titolo2", "Artista2", Year.of(2026), "Album2", 200, false, false, false);
        track3 = new Track("Titolo3", "Artista3", Year.of(2026), "Album3", 220, false, false, false);

        // Aggiunta alla playlist (assumendo il metodo add o che getTracks sia accessibile)
        playlist.getTracks().add(track1);
        playlist.getTracks().add(track2);
        playlist.getTracks().add(track3);

        iterator = new Iterator(playlist);
    }

    @Test
    void testGetCurrent() {
        assertNotNull(iterator.getCurrent());
        assertEquals(track1, iterator.getCurrent());
    }

    @Test
    void testGetNext_CircularBehavior() {
        assertEquals(track2, iterator.getNext());
        assertEquals(track3, iterator.getNext());
        assertEquals(track1, iterator.getNext());
    }

    @Test
    void testGetPrevious_CircularBehavior() {
        assertEquals(track3, iterator.getPrevious());
        assertEquals(track2, iterator.getPrevious());
        assertEquals(track1, iterator.getPrevious());
    }

    @Test
    void testEmptyPlaylist() {
        Playlist emptyPlaylist = new Playlist("Vuota");
        Iterator emptyIterator = new Iterator(emptyPlaylist);

        assertNull(emptyIterator.getCurrent());
    }

    @Test
    void testSetStrategy() {
        ExecutionStrategy reverseStrategy = (size, currentIndex) -> new int[]{2, 1, 0};

        iterator.setStrategy(reverseStrategy);
        assertEquals(track1, iterator.getCurrent());
    }
}