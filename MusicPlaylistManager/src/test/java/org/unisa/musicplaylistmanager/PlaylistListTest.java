package org.unisa.musicplaylistmanager; 

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;

class PlaylistListTest {

    private PlaylistList playlistList;

    @BeforeEach
    void setUp() {
        // Inizializza la lista prima di ogni test
        playlistList = PlaylistList.getPlaylistListPointer();
        // Svuotiamo sempre per pulizia tra i test
        playlistList.getPlaylists().clear();
    }

    @Test
    @DisplayName("addPlaylist: aggiunta di una playlist valida")
    void testAddPlaylist() {
        Playlist p = new Playlist("La mia Playlist");
        playlistList.addPlaylist(p);
        
        assertTrue(playlistList.getPlaylists().contains(p), "La playlist dovrebbe essere presente");
    }

    @Test
    @DisplayName("addPlaylist: lancia eccezione se la playlist è duplicata")
    void testAddDuplicatePlaylistThrowsException() {
        Playlist p = new Playlist("Test");
        playlistList.addPlaylist(p);
        
        // Verifica l'eccezione in caso di duplicato (stesso nome)
        Playlist duplicate = new Playlist("Test");
        assertThrows(IllegalArgumentException.class, () -> playlistList.addPlaylist(duplicate), 
            "Aggiungere una playlist con lo stesso nome deve lanciare IllegalArgumentException");
    }

    @Test
    @DisplayName("addPlaylist: lancia eccezione se si aggiunge null")
    void testAddNullPlaylistThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> playlistList.addPlaylist(null), 
            "Aggiungere null deve lanciare IllegalArgumentException");
    }

    @Test
    @DisplayName("deletePlaylist: rimuove una playlist esistente")
    void testDeletePlaylist() {
        Playlist p = new Playlist("Da eliminare");
        playlistList.addPlaylist(p);
        
        playlistList.deletePlaylist(p);
        
        assertFalse(playlistList.getPlaylists().contains(p), "La playlist dovrebbe essere stata rimossa");
    }

    @Test
    @DisplayName("deletePlaylist: non fa nulla se la playlist non esiste")
    void testDeleteNonExistentPlaylist() {
        Playlist p = new Playlist("Esistente");
        playlistList.addPlaylist(p);
        
        Playlist nonExistent = new Playlist("Non esistente");

        assertDoesNotThrow(() -> playlistList.deletePlaylist(nonExistent));
        
        assertEquals(1, playlistList.getPlaylists().size(), "La lista deve contenere ancora 1 elemento");
    }

    @Test
    @DisplayName("deletePlaylist: non fa nulla se si passa null")
    void testDeleteNullPlaylist() {
        Playlist p = new Playlist("Esistente");
        playlistList.addPlaylist(p);
        
        assertDoesNotThrow(() -> playlistList.deletePlaylist(null));
        assertEquals(1, playlistList.getPlaylists().size());
    }

    @Test
    @DisplayName("deletePlaylists: elimina un gruppo di playlist")
    void testDeleteMultiplePlaylists() {
        Playlist p1 = new Playlist("P1");
        Playlist p2 = new Playlist("P2");
        Playlist p3 = new Playlist("P3");
        
        playlistList.addPlaylist(p1);
        playlistList.addPlaylist(p2);
        playlistList.addPlaylist(p3);

        ArrayList<Playlist> toDelete = new ArrayList<>();
        toDelete.add(p1);
        toDelete.add(p2);

        playlistList.deletePlaylists(toDelete);

        assertEquals(1, playlistList.getPlaylists().size());
        assertTrue(playlistList.getPlaylists().contains(p3));
        assertFalse(playlistList.getPlaylists().contains(p1));
    }

    @Test
    @DisplayName("deletePlaylists: gestisce lista contenente elementi nulli senza crashare")
    void testDeleteMultiplePlaylistsWithNulls() {
        Playlist p1 = new Playlist("P1");
        playlistList.addPlaylist(p1);

        ArrayList<Playlist> toDelete = new ArrayList<>();
        toDelete.add(null);

        assertDoesNotThrow(() -> playlistList.deletePlaylists(toDelete));
        assertEquals(1, playlistList.getPlaylists().size());
    }

    @Test
    @DisplayName("Singleton: verifica esistenza e che il puntatore sia corretto")
    void testExistsAndPointer() {
        assertTrue(PlaylistList.exists());
        assertEquals(playlistList, PlaylistList.getPlaylistListPointer());
    }
}