package org.unisa.musicplaylistmanager; 

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.playlist.PlaylistList;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

class PlaylistListTest {

    private PlaylistList playlistList;

    @BeforeEach
    void setUp() {
        // Inizializza la lista prima di ogni test
        playlistList = PlaylistList.getPlaylistListPointer();
    }

    @Test
    void testAddPlaylist() {
        // Passiamo un nome al costruttore della Playlist
        Playlist p = new Playlist("La mia Playlist");
        playlistList.addPlaylist(p);
        
        assertTrue(playlistList.getPlaylists().contains(p), "La playlist dovrebbe essere presente");
    }

    @Test
    void testAddDuplicatePlaylistThrowsException() {
        Playlist p = new Playlist("Test");
        playlistList.addPlaylist(p);
        
        // Verifica l'eccezione in caso di duplicato
        assertThrows(IllegalArgumentException.class, () -> playlistList.addPlaylist(p));
    }

    @Test
    void testDeletePlaylist() {
        Playlist p = new Playlist("Da eliminare");
        playlistList.addPlaylist(p);
        
        playlistList.deletePlaylist(p);
        
        assertFalse(playlistList.getPlaylists().contains(p), "La playlist dovrebbe essere stata rimossa");
        // Verifica che sia stata svuotata anche internamente grazie a deleteAll()
        assertTrue(p.getTracks().isEmpty(), "Le tracce della playlist dovrebbero essere state rimosse");
    }

    @Test
    void testDeleteMultiplePlaylists() {
        Playlist p1 = new Playlist("P1");
        Playlist p2 = new Playlist("P2");
        playlistList.addPlaylist(p1);
        playlistList.addPlaylist(p2);

        ArrayList<Playlist> toDelete = new ArrayList<>();
        toDelete.add(p1);
        toDelete.add(p2);

        playlistList.deletePlaylists(toDelete);

        assertTrue(playlistList.getPlaylists().isEmpty(), "La lista dovrebbe essere vuota");
    }

    @Test
    void testExistsAndPointer() {
        assertTrue(PlaylistList.exists());
        assertEquals(playlistList, PlaylistList.getPlaylistListPointer());
    }
}