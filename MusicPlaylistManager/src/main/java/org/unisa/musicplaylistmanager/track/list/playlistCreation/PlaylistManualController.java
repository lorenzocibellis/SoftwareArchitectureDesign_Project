package org.unisa.musicplaylistmanager.track.list.playlistCreation;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.service.alert.AlertManager;
import org.unisa.musicplaylistmanager.command.AddPlaylistCommand;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.track.list.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.list.playlistList.PlaylistList;

/**
 * Controller per la finestra di dialogo di creazione di una nuova playlist.
 * Gestisce l'input dell'utente e l'aggiunta della nuova playlist al sistema.
 *
 * @author gruppo10
 */
public class PlaylistManualController {

    //Definizione attributi
    @FXML
    private Button addPlaylistButton;

    @FXML
    private Button buttonBack;

    @FXML
    private TextField nameInput;

    private PlaylistList playlistList;
    private ObservableList<Playlist> playlistListObservable;

    /**
     * Aggiunge la playlist creata sia al modello dei dati sia alla lista osservabile
     * della UI per un aggiornamento in tempo reale.
     * 
     * @param p la playlist da aggiungere
     */
    private void add(Playlist p) {
        AddPlaylistCommand command = new AddPlaylistCommand(p, playlistList, playlistListObservable);
        CommandInvoker.getCommandInvokerPointer().setCommand(command);
    }

    /**
     * Crea un nuovo oggetto Playlist estraendo il nome dal campo di testo.
     * 
     * @return la nuova {@link Playlist}
     */
    private Playlist getPlaylist(){
        return new Playlist(nameInput.getText().trim());
    }


    /**
     * Imposta il riferimento alla collezione globale delle playlist.
     * 
     * @param p l'oggetto {@link PlaylistList}
     */
    public void setPlaylistList(PlaylistList p){
        this.playlistList = p;
    }

    /**
     * Imposta la lista osservabile della finestra principale per consentire 
     * l'aggiornamento automatico della UI quando viene creata una nuova playlist.
     * 
     * @param o l'{@link ObservableList} delle playlist
     */
    public void setObservable(ObservableList<Playlist> o){
        this.playlistListObservable = o;
    }

    /**
     * Valida i dati inseriti dall'utente.
     * Mostra un alert in caso di errore o omissione.
     *
     * @param playlistName il nome inserito per la playlist
     * @return {@code true} se i controlli sono superati
     */
    private boolean isInputValid(String playlistName) {
        if (playlistList == null || playlistListObservable == null) {
            AlertManager.showMessage(Alert.AlertType.ERROR, "Errore interno", "Dati mancanti.", "Impossibile creare la playlist. Riprova dalla schermata principale.");
            return false;
        }

        if (playlistName.isEmpty()) {
            AlertManager.showMessage(Alert.AlertType.WARNING,
                    "Nome mancante",
                    "Inserisci un nome per la playlist.",
                    "Il nome della playlist è obbligatorio e non può essere vuoto.");
            return false;
        }

        return true;
    }

    /**
     * Gestisce l'azione del pulsante di conferma ("Aggiungi Playlist").
     * Crea la playlist, la aggiunge e chiude la finestra. In caso di errore
     * (es. nome duplicato), mostra un alert.
     * 
     * @param actionEvent l'evento generato dal click
     */
    @FXML
    void createPlaylist(ActionEvent actionEvent){
        String playlistName = nameInput.getText().trim();

        // Validazione dei dati inseriti
        if (!isInputValid(playlistName)) {
            return;
        }

        try{
            PlaylistList.getPlaylistListPointer().checkValidName(playlistName);
            Playlist p = this.getPlaylist();
            this.add(p);
            this.goBack(actionEvent);
        } catch (Exception e) {
            AlertManager.showMessage(Alert.AlertType.ERROR, "Errore", "Si è verificato un errore durante la creazione.", e.getMessage());
        }
    }

    /**
     * Gestisce l'azione del pulsante di annullamento.
     * Chiude la finestra senza effettuare operazioni.
     * 
     * @param actionEvent l'evento generato dal click
     */
    @FXML
    void goBack(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
