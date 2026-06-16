package org.unisa.musicplaylistmanager.service.alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * Classe utilitaria che permette di centralizzare la logica di generazione degli Alert.
 * La classe non può essere instanziata.
 *
 */
public class AlertManager {

    /**
     *
     * Costruttore particolare che non permette di instanziare la classe.
     *
     * @throws UnsupportedOperationException Eccezione lanciata quando si prova ad instanziare questo oggetto.
     *
     */
    private AlertManager(){
        throw new UnsupportedOperationException("Questa classe non può essere instanziata");
    }

    /**
     *
     * Mostra un alert informativo (INFO, WARNING, ERROR).
     *
     * @param type    Tipo di alert.
     * @param title   Titolo della finestra.
     * @param header  Intestazione.
     * @param content Il messaggio principale.
     *
     */
    public static void showMessage(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Blocca l'interazione con le altre finestre finché non viene chiuso
        alert.showAndWait();
    }

    /**
     *
     * Mostra un alert di conferma e restituisce la scelta effettuata.
     *
     * @param title   Titolo della finestra.
     * @param header  Intestazione dell'alert.
     * @param content Il messaggio principale.
     * @return {@code true} se l'utente clicca OK, {@code false} altrimenti.
     *
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
