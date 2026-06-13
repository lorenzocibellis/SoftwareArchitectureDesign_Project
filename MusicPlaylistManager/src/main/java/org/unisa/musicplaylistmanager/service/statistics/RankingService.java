package org.unisa.musicplaylistmanager.service.statistics;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.playlist.MostPlayed;

import java.util.Comparator;
import java.util.List;

/**
 * Servizio responsabile del mantenimento di una classifica in tempo reale.
 * 
 * Questa classe agisce prende in input una lista (es. tutta la libreria di canzoni)
 * e restituisce costantemente una sottolista ordinata dei migliori elementi (es. le Top 3).
 * L'uso dei Generics (<T extends MostPlayed>) permette di riutilizzare questa esatta logica
 * sia per le Track che per le Playlist.
 */
public class RankingService<T extends MostPlayed> {

    // La lista di partenza
    private final ObservableList<T> itemsToRank;
    
    // le top 3 canzoni
    private final ObservableList<T> topItems;
    
    // limite del podio
    private final int limit;

    public RankingService(ObservableList<T> itemsToRank, int limit) {
        this.itemsToRank = itemsToRank;
        this.limit = limit;
        

        // l'extractor avvisa la UI anche quando il numero di ascolti
        // cambia ma la canzone non cambia di posizione in classifica.
        this.topItems = FXCollections.observableArrayList(item -> new Observable[]{item.playCountProperty()});


        // aggiungiamo un listener su itemsToRank per sapere se l'utente aggiunge o elimina brani
        itemsToRank.addListener((ListChangeListener.Change<? extends T> c) -> {
            boolean rankingUpdate = false;
            while (c.next()) {
                if (c.wasAdded()) {
                    // è stata aggiunta una nuova canzone alla libreria
                    for (T item : c.getAddedSubList()) {
                        // aggiungiamo un listener a questa nuova canzone per avere traccia dei suoi futuri ascolti
                        attachListener(item);
                        // se la canzone aggiunta ha già degli ascolti, potrebbe cambiare il podio (ad esempio dopo undo)
                        if (item.getNumOfPlay() > 0) {
                            rankingUpdate = true;
                        }
                    }
                }
                if (c.wasRemoved()) {
                    // è stata eliminata una canzone dalla libreria
                    for (T item : c.getRemoved()) {
                        // se la canzone eliminata era una di quelle sul podio,
                        // ricalcoliamo il podio
                        if (topItems.contains(item)) {
                            rankingUpdate = true;
                        }
                    }
                }
            }

            if (rankingUpdate) {
                fullRefreshRanking();
            }
        });



        // All'apertura dell'App,aggiungiamo un listener a tutte le canzoni già presenti in libreria
        // per tracciare i loro ascolti futuri
        for (T item : itemsToRank) {
            attachListener(item);
        }

        // creiamo il podio la prima volta
        fullRefreshRanking();
    }

    /**
     * Aggancia un listener alla singola canzone/playlist.
     * Appena la canzone viene ascoltata (e il suo playCountProperty cambia), 
     * avvisa il RankingService di aggiornare il podio.
     */
    private void attachListener(T item) {
        item.playCountProperty().addListener((obs, oldVal, newVal) -> {
            handlePlayCountChange(item);
        });
    }

    /**
     * Questo metodo viene chiamato ogni volta che una canzone finisce di suonare.
     * Evita di rileggere e riordinare l'intera libreria, muove solo i riferimenti dentro al podio.
     */
    private void handlePlayCountChange(T item) {
        // La canzone appena riprodotta è già' sul podio?
        if (topItems.contains(item)) {
            // riordino gli elementi nel podio
            topItems.sort(Comparator.comparingInt(MostPlayed::getNumOfPlay).reversed());
        } else {
            // la canzone appena suonata non è sul podio.
            // se ci sono spazi liberi nel podio
            if (topItems.size() < limit) {
                topItems.add(item); // Sale sul podio
                topItems.sort(Comparator.comparingInt(MostPlayed::getNumOfPlay).reversed());
            } else {
                // il podio è pieno
                // controlliamo se la canzone appena riprodotta ha superato l'ultima nel podio
                T lastInTop = topItems.get(topItems.size() - 1); // prende il 3° classificato
                
                if (item.getNumOfPlay() > lastInTop.getNumOfPlay()) {
                    // eliminiamo il 3° classificato dal podio
                    topItems.remove(topItems.size() - 1);
                    // mettiamo al suo posto la nuova canzone
                    topItems.add(item);
                    // riordiniamo i 3 posti del podio
                    topItems.sort(Comparator.comparingInt(MostPlayed::getNumOfPlay).reversed());
                }
            }
        }
    }

    /**
     * Prende tutta la libreria, la ordina, e prende le prime 3.
     * Lo fa solo all'avvio dell'app o se cancelli una canzone che stava fisicamente in classifica.
     */
    private void fullRefreshRanking() {
        List<T> top = itemsToRank.stream()
                .filter(item -> item.getNumOfPlay() > 0) // prende solo brani ascoltati almeno una volta
                .sorted(Comparator.comparingInt(MostPlayed::getNumOfPlay).reversed()) // ordina dal più grande al più piccolo
                .limit(limit) // prende solo i primi 3
                .toList();

        topItems.setAll(top); // aggiorna i puntatori nel podio
    }

    /**
     * Metodo che i Controller chiamano per ottenere il podio in modo da mostrarlo a schermo
     */
    public ObservableList<T> getTopItems() {
        return topItems;
    }
}
