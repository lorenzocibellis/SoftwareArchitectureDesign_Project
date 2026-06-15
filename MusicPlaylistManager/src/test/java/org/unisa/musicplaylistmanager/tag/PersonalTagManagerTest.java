package org.unisa.musicplaylistmanager.tag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PersonalTagManagerTest {

    private PersonalTagManager manager;

    @BeforeEach
    void setUp() {
        manager = PersonalTagManager.getInstance();
        // svuota i tag per avere uno stato pulito prima di ogni test
        for (String tag : new ArrayList<>(manager.getPersonalTags())) {
            manager.removeTag(tag);
        }
    }

    @Test
    @DisplayName("Controllo istanza Singleton")
    void testSingleton() {
        PersonalTagManager anotherInstance = PersonalTagManager.getInstance();
        assertSame(manager, anotherInstance);
    }

    @Test
    @DisplayName("Aggiunta tag valido")
    void testAddValidTag() {
        assertTrue(manager.addTag("Rock"));
        assertEquals(1, manager.getPersonalTags().size());
        assertTrue(manager.getPersonalTags().contains("Rock"));
    }

    @Test
    @DisplayName("Aggiunta tag duplicato ignorando il case")
    void testAddDuplicateTag() {
        assertTrue(manager.addTag("Pop"));
        assertFalse(manager.addTag("POP"));
        assertFalse(manager.addTag("pop"));
        assertEquals(1, manager.getPersonalTags().size());
    }

    @Test
    @DisplayName("Blocco tag nulli, vuoti o riservati")
    void testAddReservedTag() {
        assertFalse(manager.addTag(null));
        assertFalse(manager.addTag(""));
        assertFalse(manager.addTag("   "));
        
        // tag riservati di sistema
        assertFalse(manager.addTag("Preferita"));
        assertFalse(manager.addTag("Esplicita"));
        assertFalse(manager.addTag("Nuova Uscita"));
        assertFalse(manager.addTag("new"));
        
        assertTrue(manager.getPersonalTags().isEmpty());
    }

    @Test
    @DisplayName("Rimozione di un tag esistente")
    void testRemoveTag() {
        manager.addTag("Indie");
        assertTrue(manager.getPersonalTags().contains("Indie"));
        
        assertTrue(manager.removeTag("Indie"));
        assertFalse(manager.getPersonalTags().contains("Indie"));
    }

    @Test
    @DisplayName("Rimozione di un tag non esistente")
    void testRemoveNonExistingTag() {
        assertFalse(manager.removeTag("Metal"));
    }
}
