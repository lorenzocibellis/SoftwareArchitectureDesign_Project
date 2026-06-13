package org.unisa.musicplaylistmanager.service.statistics;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.unisa.musicplaylistmanager.playlist.MostPlayed;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe di servizio RankingService.
 * Si utilizza un oggetto MockItem che implementa l'interfaccia MostPlayed
 * per testare esclusivamente la logica della classifica in modo isolato
 */
class RankingServiceTest {

    // mock per testare la logica
    private static class MockItem implements MostPlayed {
        private final String name;
        private final IntegerProperty playCount = new SimpleIntegerProperty(0);

        public MockItem(String name) {
            this.name = name;
        }

        @Override
        public int getNumOfPlay() {
            return playCount.get();
        }

        @Override
        public void incrementNumOfPlay() {
            playCount.set(playCount.get() + 1);
        }

        @Override
        public ReadOnlyIntegerProperty playCountProperty() {
            return playCount;
        }

        @Override
        public String getDisplayName() {
            return name;
        }

        public void setPlayCount(int count) {
            playCount.set(count);
        }
    }

    private ObservableList<MockItem> itemsToRank;
    private RankingService<MockItem> rankingService;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {

        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {

        }
        Thread.sleep(200);
    }

    @BeforeEach
    void setUp() {
        itemsToRank = FXCollections.observableArrayList();
        rankingService = new RankingService<>(itemsToRank, 3);
    }

    @Test
    @DisplayName("Il podio deve essere vuoto quando la libreria è vuota o nessuno ha ascolti")
    void testEmptyPodium() {
        assertTrue(rankingService.getTopItems().isEmpty());
        
        itemsToRank.add(new MockItem("Traccia a zero ascolti"));
        // non entra in Top 3 se ha 0 ascolti
        assertTrue(rankingService.getTopItems().isEmpty());
    }

    @Test
    @DisplayName("Il podio si aggiorna dinamicamente al crescere degli ascolti")
    void testDynamicPlayCountUpdate() {
        MockItem item1 = new MockItem("A");
        MockItem item2 = new MockItem("B");
        
        itemsToRank.addAll(item1, item2);
        
        item1.incrementNumOfPlay();
        assertEquals(1, rankingService.getTopItems().size());
        assertEquals(item1, rankingService.getTopItems().get(0));
        

        item2.setPlayCount(5);
        assertEquals(2, rankingService.getTopItems().size());
        assertEquals(item2, rankingService.getTopItems().get(0));
    }

    @Test
    @DisplayName("La classifica non deve superare il limite prestabilito di posizioni")
    void testPodiumLimit() {
        MockItem item1 = new MockItem("1"); item1.setPlayCount(10);
        MockItem item2 = new MockItem("2"); item2.setPlayCount(8);
        MockItem item3 = new MockItem("3"); item3.setPlayCount(6);
        MockItem item4 = new MockItem("4"); item4.setPlayCount(4);

        itemsToRank.addAll(item1, item2, item3, item4);
        
        assertEquals(3, rankingService.getTopItems().size());
        assertFalse(rankingService.getTopItems().contains(item4));
        assertEquals(item1, rankingService.getTopItems().get(0));
    }

    @Test
    @DisplayName("Una traccia fuori classifica deve poter entrare nel podio")
    void testOvertake() {
        MockItem item1 = new MockItem("1"); item1.setPlayCount(10);
        MockItem item2 = new MockItem("2"); item2.setPlayCount(8);
        MockItem item3 = new MockItem("3"); item3.setPlayCount(6);
        MockItem item4 = new MockItem("4"); item4.setPlayCount(4);
        
        itemsToRank.addAll(item1, item2, item3, item4);
        

        item4.setPlayCount(20);
        
        assertEquals(3, rankingService.getTopItems().size());
        assertTrue(rankingService.getTopItems().contains(item4));
        assertEquals(item4, rankingService.getTopItems().get(0));

        assertFalse(rankingService.getTopItems().contains(item3));
    }

    @Test
    @DisplayName("Eliminare una canzone sul podio fa salire chi era fuori classifica")
    void testItemRemoval() {
        MockItem item1 = new MockItem("1"); item1.setPlayCount(10);
        MockItem item2 = new MockItem("2"); item2.setPlayCount(8);
        MockItem item3 = new MockItem("3"); item3.setPlayCount(6);
        MockItem item4 = new MockItem("4"); item4.setPlayCount(4); 
        
        itemsToRank.addAll(item1, item2, item3, item4);
        
        // elimino il 1° classificato dalla libreria
        itemsToRank.remove(item1);
        
        assertEquals(3, rankingService.getTopItems().size());
        // il 2° classificato scala al 1° posto
        assertEquals(item2, rankingService.getTopItems().get(0));

        assertTrue(rankingService.getTopItems().contains(item4));
        assertEquals(item4, rankingService.getTopItems().get(2));
    }
}
