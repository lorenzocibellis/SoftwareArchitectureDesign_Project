package org.unisa.musicplaylistmanager.playlist;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.unisa.musicplaylistmanager.command.AddPlaylistCommand;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.observer.ObserverPlaylist;
import org.unisa.musicplaylistmanager.track.TrackList;

/**
 * Controller per la finestra di dialogo di creazione di una nuova playlist.
 * Gestisce l'input dell'utente e l'aggiunta della nuova playlist al sistema.
 *
 * @author gruppo10
 */
public class PlaylistCreationController {

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
        //CommandInvoker.getCommandInvokerPointer().setCommand(command);
        command.execute();
        //playlistList.addPlaylist(p);
        //playlistListObservable.add(p);

        // Pattern Observer: crea observer per la playlist e lo registra sul subject della TrackList
        /*
        if (TrackList.exists()) {
            ObserverPlaylist observer = new ObserverPlaylist(p);
            p.setObserver(observer);
            TrackList.getTrackListPointer().getSubjectTrackList().attach(observer);
        }

         */
    }

    /**
     * Crea un nuovo oggetto Playlist estraendo il nome dal campo di testo.
     * 
     * @return la nuova {@link Playlist}
     */
    private Playlist getPlaylist(){
        return new Playlist(nameInput.getText());
    }

    /**
     * Mostra una finestra di avviso in caso di errore durante la creazione.
     * 
     * @param title il titolo della finestra di errore
     * @param header l'intestazione dell'errore
     * @param content il dettaglio dell'errore da mostrare
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
     * Gestisce l'azione del pulsante di conferma ("Aggiungi Playlist").
     * Crea la playlist, la aggiunge e chiude la finestra. In caso di errore
     * (es. nome duplicato), mostra un alert.
     * 
     * @param actionEvent l'evento generato dal click
     */
    @FXML
    void addPlaylist(ActionEvent actionEvent){
        try{
            Playlist p = this.getPlaylist();
            this.add(p);
            this.goBack(actionEvent);
        } catch (Exception e) {
            showError("Errore", "Si è verificato un errore durante la creazione.", e.getMessage());
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
