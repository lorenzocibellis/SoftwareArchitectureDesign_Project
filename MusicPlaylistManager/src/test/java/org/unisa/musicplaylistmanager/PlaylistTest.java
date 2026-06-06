package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;
import org.unisa.musicplaylistmanager.playlist.Playlist;

class PlaylistTest {
    private Playlist playlist;
    private Track track1;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Test Playlist");
        track1 = new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", 354, true, false, false);
    }

    @Test
    void testUpdateRemovesTrack() {
        // Aggiungi la traccia alla playlist
        playlist.addTrack(track1);
        assertTrue(playlist.getTracks().contains(track1));

        // Simula la notifica che arriva dalla TrackList (l'update dell'Observer)
        playlist.update(track1);

        // La playlist deve aver rimosso la traccia
        assertFalse(playlist.getTracks().contains(track1));
    }
}