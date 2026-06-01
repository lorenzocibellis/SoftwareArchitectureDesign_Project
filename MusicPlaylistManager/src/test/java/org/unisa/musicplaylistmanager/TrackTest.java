/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.unisa.musicplaylistmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Track}.
 * @author gruppo10
 */
class TrackTest {

    private Track track;

    // -----------------------------------------------------------------------
    // Setup
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        track = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Rock", 354, true, false, false);
    }

    // -----------------------------------------------------------------------
    // Constructor – valid input
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: oggetto creato correttamente con dati validi")
    void testConstructorValid() {
        assertNotNull(track);
        assertEquals("Bohemian Rhapsody", track.getTitle());
        assertEquals("Queen",             track.getAuthor());
        assertEquals(Year.of(1975),       track.getYear());
        assertEquals("Rock",              track.getGenre());
        assertEquals(354,                 track.getDuration());
        assertTrue(track.isFavourite());
        assertFalse(track.isExplicitContent());
        assertFalse(track.isNewRelease());
    }

    @Test
    @DisplayName("Constructor: durata zero è accettata")
    void testConstructorDurationZero() {
        assertDoesNotThrow(() ->
                new Track("Silence", "Artist", Year.of(2000), "Ambient", 0, false, false, false));
    }

    @Test
    @DisplayName("Constructor: anno corrente è accettato")
    void testConstructorCurrentYear() {
        assertDoesNotThrow(() ->
                new Track("New Track", "Artist", Year.now(), "Pop", 180, false, false, true));
    }

    // -----------------------------------------------------------------------
    // Constructor – invalid input
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: titolo null lancia IllegalArgumentException")
    void testConstructorNullTitle() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track(null, "Queen", Year.of(1975), "Rock", 354, false, false, false));
    }

    @Test
    @DisplayName("Constructor: titolo blank lancia IllegalArgumentException")
    void testConstructorBlankTitle() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("   ", "Queen", Year.of(1975), "Rock", 354, false, false, false));
    }

    @Test
    @DisplayName("Constructor: autore null lancia IllegalArgumentException")
    void testConstructorNullAuthor() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", null, Year.of(1975), "Rock", 354, false, false, false));
    }

    @Test
    @DisplayName("Constructor: genere vuoto lancia IllegalArgumentException")
    void testConstructorEmptyGenre() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "", 354, false, false, false));
    }

    @Test
    @DisplayName("Constructor: anno null lancia IllegalArgumentException")
    void testConstructorNullYear() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", null, "Rock", 354, false, false, false));
    }

    @Test
    @DisplayName("Constructor: anno futuro lancia IllegalArgumentException")
    void testConstructorFutureYear() {
        Year future = Year.now().plusYears(1);
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", future, "Rock", 354, false, false, false));
    }

    @Test
    @DisplayName("Constructor: durata negativa lancia IllegalArgumentException")
    void testConstructorNegativeDuration() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", -1, false, false, false));
    }

    // -----------------------------------------------------------------------
    // Setters – valid input
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setTitle: aggiorna il titolo correttamente")
    void testSetTitleValid() {
        track.setTitle("We Will Rock You");
        assertEquals("We Will Rock You", track.getTitle());
    }

    @Test
    @DisplayName("setAuthor: aggiorna l'autore correttamente")
    void testSetAuthorValid() {
        track.setAuthor("Led Zeppelin");
        assertEquals("Led Zeppelin", track.getAuthor());
    }

    @Test
    @DisplayName("setGenre: aggiorna il genere correttamente")
    void testSetGenreValid() {
        track.setGenre("Hard Rock");
        assertEquals("Hard Rock", track.getGenre());
    }

    @Test
    @DisplayName("setYear: aggiorna l'anno correttamente")
    void testSetYearValid() {
        track.setYear(Year.of(1980));
        assertEquals(Year.of(1980), track.getYear());
    }

    @Test
    @DisplayName("setDuration: aggiorna la durata correttamente")
    void testSetDurationValid() {
        track.setDuration(200);
        assertEquals(200, track.getDuration());
    }

    @Test
    @DisplayName("setFavourite: aggiorna il flag favourite")
    void testSetFavourite() {
        track.setFavourite(false);
        assertFalse(track.isFavourite());
        track.setFavourite(true);
        assertTrue(track.isFavourite());
    }

    @Test
    @DisplayName("setExplicit: aggiorna il flag explicit")
    void testSetExplicit() {
        track.setExplicit(true);
        assertTrue(track.isExplicitContent());
    }

    @Test
    @DisplayName("setNewRelease: aggiorna il flag newRelease")
    void testSetNewRelease() {
        track.setNewRelease(true);
        assertTrue(track.isNewRelease());
    }

    // -----------------------------------------------------------------------
    // Setters – invalid input
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setTitle: titolo blank lancia IllegalArgumentException")
    void testSetTitleBlank() {
        assertThrows(IllegalArgumentException.class, () -> track.setTitle(""));
    }

    @Test
    @DisplayName("setAuthor: autore null lancia IllegalArgumentException")
    void testSetAuthorNull() {
        assertThrows(IllegalArgumentException.class, () -> track.setAuthor(null));
    }

    @Test
    @DisplayName("setGenre: genere blank lancia IllegalArgumentException")
    void testSetGenreBlank() {
        assertThrows(IllegalArgumentException.class, () -> track.setGenre("  "));
    }

    @Test
    @DisplayName("setYear: anno futuro lancia IllegalArgumentException")
    void testSetYearFuture() {
        Year future = Year.now().plusYears(2);
        assertThrows(IllegalArgumentException.class, () -> track.setYear(future));
    }

    @Test
    @DisplayName("setDuration: durata negativa lancia IllegalArgumentException")
    void testSetDurationNegative() {
        assertThrows(IllegalArgumentException.class, () -> track.setDuration(-5));
    }

    // -----------------------------------------------------------------------
    // equals()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("equals: stessa istanza → true")
    void testEqualsSameInstance() {
        assertEquals(track, track);
    }

    @Test
    @DisplayName("equals: tracce con stesso titolo, autore e anno → true")
    void testEqualsEqualTracks() {
        Track other = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Pop", 200, false, true, true);
        assertEquals(track, other);
    }

    @Test
    @DisplayName("equals: titolo diverso → false")
    void testEqualsDifferentTitle() {
        Track other = new Track("Radio Ga Ga", "Queen", Year.of(1975),
                "Rock", 354, true, false, false);
        assertNotEquals(track, other);
    }

    @Test
    @DisplayName("equals: autore diverso → false")
    void testEqualsDifferentAuthor() {
        Track other = new Track("Bohemian Rhapsody", "Freddie", Year.of(1975),
                "Rock", 354, true, false, false);
        assertNotEquals(track, other);
    }

    @Test
    @DisplayName("equals: anno diverso → false")
    void testEqualsDifferentYear() {
        Track other = new Track("Bohemian Rhapsody", "Queen", Year.of(1976),
                "Rock", 354, true, false, false);
        assertNotEquals(track, other);
    }

    @Test
    @DisplayName("equals: confronto con null → false")
    void testEqualsNull() {
        assertNotEquals(null, track);
    }

    @Test
    @DisplayName("equals: confronto con tipo diverso → false")
    void testEqualsDifferentType() {
        assertNotEquals("Bohemian Rhapsody", track);
    }

    // -----------------------------------------------------------------------
    // hashCode()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("hashCode: tracce uguali hanno lo stesso hashCode")
    void testHashCodeEqualTracks() {
        Track other = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Punk", 100, false, false, false);
        assertEquals(track.hashCode(), other.hashCode());
    }

    @Test
    @DisplayName("hashCode: tracce diverse (di solito) hanno hashCode diversi")
    void testHashCodeDifferentTracks() {
        Track other = new Track("Another One Bites the Dust", "Queen", Year.of(1980),
                "Rock", 215, false, false, false);
        assertNotEquals(track.hashCode(), other.hashCode());
    }

    // -----------------------------------------------------------------------
    // toString()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("toString: formato atteso 'titolo | autore | anno'")
    void testToString() {
        String expected = "Bohemian Rhapsody | Queen | 1975";
        assertEquals(expected, track.toString());
    }
}