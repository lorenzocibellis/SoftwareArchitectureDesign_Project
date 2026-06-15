package org.unisa.musicplaylistmanager.tag;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
     * Restituisce la lista osservabile dei tag personali.
     */
    public ObservableList<String> getPersonalTags() {
        return personalTags;
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
        for (String t : personalTags) {
            if (t.toUpperCase().equals(normalized)) {
                return false;
            }
        }
        personalTags.add(tag.trim());
        return true;
    }
}
