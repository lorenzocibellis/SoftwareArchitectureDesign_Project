package org.unisa.musicplaylistmanager.playlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.track.model.Track;

import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;
import org.unisa.musicplaylistmanager.collections.playlist.model.Playlist;

class PlaylistTest {
    private Playlist playlist;
    private Track track1;
    private Track track2;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Test Playlist");
        track1 = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 354, true, false, false);
        track2 = new Track("Radio Ga Ga", "Queen", Year.of(1984), "Rock", 343, false, false, false);
    }

    @Test
    @DisplayName("update: rimuove la traccia passata (Observer pattern)")
    void testUpdateRemovesTrack() {
        // Aggiungi la traccia alla playlist
        playlist.addTrack(track1);
        assertTrue(playlist.getTracks().contains(track1));
        // Simula la notifica che arriva dalla TrackList (l'update dell'Observer)
        playlist.update(track1);
        // La playlist deve aver rimosso la traccia
        assertFalse(playlist.getTracks().contains(track1));
    }

    @Test
    @DisplayName("addTrack: aggiunta valida")
    void testAddTrackValid() {
        playlist.addTrack(track1);
        assertEquals(1, playlist.getSize());
        assertTrue(playlist.getTracks().contains(track1));
    }

    @Test
    @DisplayName("addTrack: eccezione se la traccia è duplicata")
    void testAddTrackDuplicate() {
        playlist.addTrack(track1);
        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(track1), "L'aggiunta di un duplicato deve lanciare eccezione");
    }

    @Test
    @DisplayName("addTrack: eccezione se la traccia è null")
    void testAddTrackNull() {
        assertThrows(IllegalArgumentException.class, () -> playlist.addTrack(null), "L'aggiunta di null deve lanciare eccezione");
    }

    @Test
    @DisplayName("removeTrack: rimuove la traccia indicata")
    void testRemoveTrack() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.removeTrack(track1);
        
        assertFalse(playlist.getTracks().contains(track1));
        assertTrue(playlist.getTracks().contains(track2));
        assertEquals(1, playlist.getSize());
    }

    @Test
    @DisplayName("deleteAll: svuota completamente la playlist")
    void testDeleteAll() {
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.deleteAll();
        
        assertEquals(0, playlist.getSize());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    @DisplayName("setName e getName: cambiano il nome")
    void testSetAndGetName() {
        playlist.setName("Nuovo Nome");
        assertEquals("Nuovo Nome", playlist.getName());
    }

    @Test
    @DisplayName("equals e hashCode: confrontano per nome della playlist")
    void testEqualsAndHashCode() {
        Playlist playlist2 = new Playlist("Test Playlist");
        Playlist playlist3 = new Playlist("Altra Playlist");

        assertEquals(playlist, playlist2);
        assertEquals(playlist.hashCode(), playlist2.hashCode());
        assertNotEquals(playlist, playlist3);
    }

    @Test
    @DisplayName("toString: restituisce il nome della playlist")
    void testToString() {
        assertEquals("Test Playlist", playlist.toString());
    }

    // -----------------------------------------------------------------------
    // Observer: update() casi limite
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("update: una traccia non presente non causa errori e non modifica la playlist")
    void testUpdateTrackNotPresent() {
        playlist.addTrack(track1);

        // track2 non è nella playlist: l'update (rimozione) deve essere un no-op sicuro
        assertDoesNotThrow(() -> playlist.update(track2));
        assertEquals(1, playlist.getSize());
        assertTrue(playlist.getTracks().contains(track1));
    }

    @Test
    @DisplayName("equals: diverso da null e da oggetti di tipo diverso")
    void testEqualsNullAndDifferentType() {
        assertNotEquals(null, playlist);
        assertNotEquals("Test Playlist", playlist);
    }

    // -----------------------------------------------------------------------
    // MostPlayed: conteggio riproduzioni della playlist
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getNumOfPlay: il conteggio iniziale è zero")
    void testInitialNumOfPlay() {
        assertEquals(0, playlist.getNumOfPlay());
    }

    @Test
    @DisplayName("incrementNumOfPlay: incrementa il conteggio di riproduzioni")
    void testIncrementNumOfPlay() {
        playlist.incrementNumOfPlay();
        playlist.incrementNumOfPlay();
        playlist.incrementNumOfPlay();
        assertEquals(3, playlist.getNumOfPlay());
    }

    @Test
    @DisplayName("playCountProperty: è reattiva e rispecchia il numero di ascolti")
    void testPlayCountProperty() {
        assertNotNull(playlist.playCountProperty());
        assertEquals(0, playlist.playCountProperty().get());

        playlist.incrementNumOfPlay();
        assertEquals(1, playlist.playCountProperty().get());
        assertEquals(playlist.getNumOfPlay(), playlist.playCountProperty().get());
    }

}