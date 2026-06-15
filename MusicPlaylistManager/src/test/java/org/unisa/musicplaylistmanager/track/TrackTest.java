package org.unisa.musicplaylistmanager.track;

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
        track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Year.of(1975),
                "Rock",
                354,
                true,
                false,
                false
        );
    }

    // -----------------------------------------------------------------------
    // Constructor – valid input
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: oggetto creato correttamente con dati validi")
    void testConstructorValid() {
        assertNotNull(track);
        assertEquals("Bohemian Rhapsody", track.getTitle());
        assertEquals("Queen", track.getAuthor());
        assertEquals(Year.of(1975), track.getYear());
        assertEquals("Rock", track.getGenre());
        assertEquals(354, track.getDuration());
        assertTrue(track.isFavourite());
        assertFalse(track.isExplicitContent());
        assertFalse(track.isNewRelease());
    }

    @Test
    @DisplayName("Constructor: durata zero non è accettata")
    void testConstructorDurationZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Silence", "Artist", Year.of(2000), "Ambient", 0,
                        false, false, false));
    }

    @Test
    @DisplayName("Constructor: anno corrente è accettato")
    void testConstructorCurrentYear() {
        assertDoesNotThrow(() ->
                new Track("New Track", "Artist", Year.now(), "Pop", 180,
                        false, false, true));
    }

    // -----------------------------------------------------------------------
    // Constructor – invalid input
    // -----------------------------------------------------------------------

    @Test
    void testConstructorNullTitle() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track(null, "Queen", Year.of(1975), "Rock", 354,
                        false, false, false));
    }

    @Test
    void testConstructorBlankTitle() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("   ", "Queen", Year.of(1975), "Rock", 354,
                        false, false, false));
    }

    @Test
    void testConstructorNullAuthor() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", null, Year.of(1975), "Rock", 354,
                        false, false, false));
    }

    @Test
    void testConstructorEmptyGenre() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "", 354,
                        false, false, false));
    }

    @Test
    void testConstructorNullYear() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", null, "Rock", 354,
                        false, false, false));
    }

    @Test
    void testConstructorFutureYear() {
        Year future = Year.now().plusYears(1);
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", future, "Rock", 354,
                        false, false, false));
    }

    @Test
    void testConstructorNegativeDuration() {
        assertThrows(IllegalArgumentException.class, () ->
                new Track("Bohemian Rhapsody", "Queen", Year.of(1975), "Rock", -1,
                        false, false, false));
    }

    // -----------------------------------------------------------------------
    // Setters – valid input
    // -----------------------------------------------------------------------

    @Test
    void testSetTitleValid() {
        track.setTitle("We Will Rock You");
        assertEquals("We Will Rock You", track.getTitle());
    }

    @Test
    void testSetAuthorValid() {
        track.setAuthor("Led Zeppelin");
        assertEquals("Led Zeppelin", track.getAuthor());
    }

    @Test
    void testSetGenreValid() {
        track.setGenre("Hard Rock");
        assertEquals("Hard Rock", track.getGenre());
    }

    @Test
    void testSetYearValid() {
        track.setYear(Year.of(1980));
        assertEquals(Year.of(1980), track.getYear());
    }

    @Test
    void testSetDurationValid() {
        track.setDuration(200);
        assertEquals(200, track.getDuration());
    }

    @Test
    void testSetFavourite() {
        track.setFavourite(false);
        assertFalse(track.isFavourite());
        track.setFavourite(true);
        assertTrue(track.isFavourite());
    }

    @Test
    void testSetExplicit() {
        track.setExplicit(true);
        assertTrue(track.isExplicitContent());
    }

    @Test
    void testSetNewRelease() {
        track.setNewRelease(true);
        assertTrue(track.isNewRelease());
    }

    // -----------------------------------------------------------------------
    // Setters – invalid input
    // -----------------------------------------------------------------------

    @Test
    void testSetTitleBlank() {
        assertThrows(IllegalArgumentException.class, () -> track.setTitle(""));
    }

    @Test
    void testSetAuthorNull() {
        assertThrows(IllegalArgumentException.class, () -> track.setAuthor(null));
    }

    @Test
    void testSetGenreBlank() {
        assertThrows(IllegalArgumentException.class, () -> track.setGenre("  "));
    }

    @Test
    void testSetYearFuture() {
        Year future = Year.now().plusYears(2);
        assertThrows(IllegalArgumentException.class, () -> track.setYear(future));
    }

    @Test
    void testSetDurationNegative() {
        assertThrows(IllegalArgumentException.class, () -> track.setDuration(-5));
    }

    // -----------------------------------------------------------------------
    // MOST PLAYED 
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("incrementNumOfPlay: incrementa correttamente il contatore")
    void testIncrementNumOfPlay() {
        int initial = track.getNumOfPlay();

        track.incrementNumOfPlay();
        track.incrementNumOfPlay();

        assertEquals(initial + 2, track.getNumOfPlay());
    }

    @Test
    @DisplayName("playCount iniziale è zero")
    void testInitialPlayCount() {
        assertEquals(0, track.getNumOfPlay());
    }

    // -----------------------------------------------------------------------
    // equals()
    // -----------------------------------------------------------------------

    @Test
    void testEqualsSameInstance() {
        assertEquals(track, track);
    }

    @Test
    void testEqualsEqualTracks() {
        Track other = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Pop", 200, false, true, true);
        assertEquals(track, other);
    }

    @Test
    void testEqualsDifferentTitle() {
        Track other = new Track("Radio Ga Ga", "Queen", Year.of(1975),
                "Rock", 354, true, false, false);
        assertNotEquals(track, other);
    }

    @Test
    void testEqualsDifferentAuthor() {
        Track other = new Track("Bohemian Rhapsody", "Freddie", Year.of(1975),
                "Rock", 354, true, false, false);
        assertNotEquals(track, other);
    }

    @Test
    void testEqualsDifferentYear() {
        Track other = new Track("Bohemian Rhapsody", "Queen", Year.of(1976),
                "Rock", 354, true, false, false);
        assertNotEquals(track, other);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(null, track);
    }

    @Test
    void testEqualsDifferentType() {
        assertNotEquals("Bohemian Rhapsody", track);
    }

    // -----------------------------------------------------------------------
    // hashCode()
    // -----------------------------------------------------------------------

    @Test
    void testHashCodeEqualTracks() {
        Track other = new Track("Bohemian Rhapsody", "Queen", Year.of(1975),
                "Punk", 100, false, false, false);
        assertEquals(track.hashCode(), other.hashCode());
    }

    @Test
    void testHashCodeDifferentTracks() {
        Track other = new Track("Another One Bites the Dust", "Queen", Year.of(1980),
                "Rock", 215, false, false, false);
        assertNotEquals(track.hashCode(), other.hashCode());
    }

    // -----------------------------------------------------------------------
    // toString()
    // -----------------------------------------------------------------------

    @Test
    void testToString() {
        assertEquals("Bohemian Rhapsody | Queen | 1975", track.toString());
    }

    // -----------------------------------------------------------------------
    // Personal Tags
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Gestione dei tag personali (add, remove, get)")
    void testPersonalTags() {
        assertNotNull(track.getPersonalTags());
        assertTrue(track.getPersonalTags().isEmpty());

        track.addPersonalTag("Rock");
        assertEquals(1, track.getPersonalTags().size());
        assertTrue(track.getPersonalTags().contains("Rock"));

        // Test duplicati
        track.addPersonalTag("Rock");
        assertEquals(1, track.getPersonalTags().size());

        track.addPersonalTag("Anni 2000");
        assertEquals(2, track.getPersonalTags().size());
        assertTrue(track.getPersonalTags().contains("Anni 2000"));

        track.removePersonalTag("Rock");
        assertEquals(1, track.getPersonalTags().size());
        assertFalse(track.getPersonalTags().contains("Rock"));
    }
}