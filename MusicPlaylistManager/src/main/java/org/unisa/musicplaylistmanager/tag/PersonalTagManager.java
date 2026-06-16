package org.unisa.musicplaylistmanager.tag;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.unisa.musicplaylistmanager.track.list.tracklist.TrackList;

/**
 * Singleton che gestisce i tag personali globali.
 * Permette all'utente di aggiungere nuovi tag che saranno poi disponibili
 * per la selezione durante la creazione o modifica di una traccia.
 */
public class PersonalTagManager {

    private static PersonalTagManager instance;
    private final ObservableList<String> personalTags;

    private PersonalTagManager() {
        personalTags = FXCollections.observableArrayList();
    }

    /**
     * @return L'istanza Singleton.
     */
    public static PersonalTagManager getInstance() {
        if (instance == null) {
            instance = new PersonalTagManager();
        }
        return instance;
    }

    /**
     * Restituisce la lista osservabile dei tag personali (in sola lettura).
     */
    public ObservableList<String> getPersonalTags() {
        return FXCollections.unmodifiableObservableList(personalTags);
    }

    /**
     * Aggiunge un nuovo tag se non esiste già.
     * @param tag Il tag da aggiungere.
     * @return true se è stato aggiunto, false se esisteva già
     */
    public boolean addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return false;
        }
        String normalized = tag.trim().toUpperCase();
        
        // non permette all'utente di inserire come nome del tag personale un tag dedicato di sistema
        if (normalized.equals("PREFERITA") || normalized.equals("ESPLICITA") || normalized.equals("NUOVA USCITA") || normalized.equals("NEW")) {
            return false;
        }

        for (String t : personalTags) {
            if (t.toUpperCase().equals(normalized)) {
                return false;
            }
        }
        personalTags.add(tag.trim());
        return true;
    }

    /**
     * Rimuove un tag dalla lista globale e provvede a cancellarlo
     * da tutte le tracce attualmente in memoria.
     * @param tag Il tag da rimuovere.
     * @return true se è stato rimosso, false altrimenti.
     */
    public boolean removeTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return false;
        }
        boolean removed = personalTags.remove(tag);
        
        // se il tag è stato rimosso con successo, lo elimina anche dalle tracce
        if (removed && TrackList.exists()) {
            TrackList.getTrackListPointer()
                .getTracks()
                .stream()
                .filter(track -> track.getPersonalTags() != null && track.getPersonalTags().contains(tag))
                .forEach(track -> track.removePersonalTag(tag));
        }
        
        return removed;
    }
}
