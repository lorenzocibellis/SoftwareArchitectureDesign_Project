package org.unisa.musicplaylistmanager.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.unisa.musicplaylistmanager.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.Track;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BaseSubjectTrackList}.
 * Usa una sottoclasse concreta anonima per testare la classe astratta.
 * @author gruppo10
 */
class BaseSubjectTrackListTest {

    // -----------------------------------------------------------------------
    // Stub: sottoclasse concreta di BaseSubjectTrackList (minimale)
    // -----------------------------------------------------------------------
    private static class ConcreteSubject extends BaseSubjectTrackList {
        // nessun metodo aggiuntivo: eredita tutto da BaseSubjectTrackList
    }

    // -----------------------------------------------------------------------
    // Stub: Observer che registra le tracce ricevute
    // -----------------------------------------------------------------------
    private static class SpyObserver extends BaseObserverPlaylist {
        List<Track> receivedTracks = new ArrayList<>();

        public SpyObserver(Playlist playlist) {
            super(playlist);
        }

        @Override
        public void update(Track track) {
            receivedTracks.add(track); // registra senza rimuovere dalla playlist
        }
    }

    // -----------------------------------------------------------------------
    // Fixture
    // -----------------------------------------------------------------------

    private ConcreteSubject subject;
    private Playlist        playlist;
    private SpyObserver     observer1;
    private SpyObserver     observer2;

    private Track track1;
    private Track track2;

    @BeforeEach
    void setUp() {
        subject  = new ConcreteSubject();
        playlist = new Playlist("Test Playlist");

        track1 = new Track("Bohemian Rhapsody",  "Queen",        Year.of(1975), "Rock", 354, true,  false, false);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", Year.of(1971), "Rock", 482, false, false, false);

        playlist.addTrack(track1);
        playlist.addTrack(track2);

        observer1 = new SpyObserver(playlist);
        observer2 = new SpyObserver(playlist);
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor: oggetto creato correttamente")
    void testConstructorNotNull() {
        assertNotNull(subject);
    }

    // -----------------------------------------------------------------------
    // attach()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("attach: aggiunge un observer valido senza eccezioni")
    void testAttachValidObserver() {
        assertDoesNotThrow(() -> subject.attach(observer1));
    }

    @Test
    @DisplayName("attach: observer null non viene aggiunto (nessuna eccezione)")
    void testAttachNullObserver() {
        assertDoesNotThrow(() -> subject.attach(null));
        // Verifica indiretta: notifyObserver su lista vuota non lancia eccezioni
        assertDoesNotThrow(() -> subject.notifyObserver(track1));
    }

    @Test
    @DisplayName("attach: più observer possono essere aggiunti")
    void testAttachMultipleObservers() {
        assertDoesNotThrow(() -> {
            subject.attach(observer1);
            subject.attach(observer2);
        });
    }

    @Test
    @DisplayName("attach: lo stesso observer può essere aggiunto più volte")
    void testAttachSameObserverTwice() {
        subject.attach(observer1);
        subject.attach(observer1);
        // notifyObserver dovrà chiamarlo due volte
        subject.notifyObserver(track1);
        assertEquals(2, observer1.receivedTracks.size());
    }

    // -----------------------------------------------------------------------
    // detach()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("detach: rimuove un observer precedentemente aggiunto")
    void testDetachAttachedObserver() {
        subject.attach(observer1);
        subject.detach(observer1);
        // dopo il detach notifyObserver non deve chiamare observer1
        subject.notifyObserver(track1);
        assertTrue(observer1.receivedTracks.isEmpty());
    }

    @Test
    @DisplayName("detach: rimuovere un observer non presente non lancia eccezioni")
    void testDetachNotAttachedObserver() {
        assertDoesNotThrow(() -> subject.detach(observer1));
    }

    @Test
    @DisplayName("detach: rimuovere null non lancia eccezioni")
    void testDetachNull() {
        assertDoesNotThrow(() -> subject.detach(null));
    }

    @Test
    @DisplayName("detach: dopo detach gli altri observer rimangono attivi")
    void testDetachKeepsOtherObservers() {
        subject.attach(observer1);
        subject.attach(observer2);
        subject.detach(observer1);

        subject.notifyObserver(track1);

        assertTrue(observer1.receivedTracks.isEmpty(),  "observer1 non deve ricevere notifiche");
        assertEquals(1, observer2.receivedTracks.size(), "observer2 deve ricevere la notifica");
    }

    // -----------------------------------------------------------------------
    // notifyObserver()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("notifyObserver: nessuna eccezione se la lista observer è vuota")
    void testNotifyObserverEmptyList() {
        assertDoesNotThrow(() -> subject.notifyObserver(track1));
    }

    @Test
    @DisplayName("notifyObserver: chiama update su tutti gli observer registrati")
    void testNotifyObserverCallsAllObservers() {
        subject.attach(observer1);
        subject.attach(observer2);

        subject.notifyObserver(track1);

        assertEquals(1, observer1.receivedTracks.size());
        assertEquals(1, observer2.receivedTracks.size());
        assertEquals(track1, observer1.receivedTracks.get(0));
        assertEquals(track1, observer2.receivedTracks.get(0));
    }

    @Test
    @DisplayName("notifyObserver: passa la traccia corretta a ogni observer")
    void testNotifyObserverPassesCorrectTrack() {
        subject.attach(observer1);

        subject.notifyObserver(track2);

        assertEquals(track2, observer1.receivedTracks.get(0));
    }

    @Test
    @DisplayName("notifyObserver: più notifiche successive vengono ricevute nell'ordine corretto")
    void testNotifyObserverMultipleCalls() {
        subject.attach(observer1);

        subject.notifyObserver(track1);
        subject.notifyObserver(track2);

        assertEquals(2, observer1.receivedTracks.size());
        assertEquals(track1, observer1.receivedTracks.get(0));
        assertEquals(track2, observer1.receivedTracks.get(1));
    }

    @Test
    @DisplayName("notifyObserver con track null: viene propagato null agli observer senza eccezioni")
    void testNotifyObserverNullTrack() {
        subject.attach(observer1);
        assertDoesNotThrow(() -> subject.notifyObserver(null));
        assertEquals(1, observer1.receivedTracks.size());
        assertNull(observer1.receivedTracks.get(0));
    }

    // -----------------------------------------------------------------------
    // Integrazione attach → notify → detach
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Integrazione: attach, notify, detach, notify → solo prima notifica ricevuta")
    void testAttachNotifyDetachNotify() {
        subject.attach(observer1);
        subject.notifyObserver(track1);   // observer1 riceve track1

        subject.detach(observer1);
        subject.notifyObserver(track2);   // observer1 NON deve ricevere track2

        assertEquals(1, observer1.receivedTracks.size());
        assertEquals(track1, observer1.receivedTracks.get(0));
    }
}