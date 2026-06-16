package org.unisa.musicplaylistmanager.track.list.playlistCreation;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.FlowPane;
import org.unisa.musicplaylistmanager.service.alert.AlertManager;
import org.unisa.musicplaylistmanager.command.AddPlaylistCommand;
import org.unisa.musicplaylistmanager.command.CommandInvoker;
import org.unisa.musicplaylistmanager.tag.PersonalTagManager;
import org.unisa.musicplaylistmanager.tag.TagUIHelper;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.list.playlist.Playlist;
import org.unisa.musicplaylistmanager.track.list.playlistList.PlaylistList;
import org.unisa.musicplaylistmanager.track.list.tracklist.TrackList;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la creazione automatica di playlist basata su tag, genere e anno.
 * Filtra le tracce dalla libreria principale (TrackList) in base ai criteri selezionati
 * e crea una playlist contenente solo le tracce che rispettano TUTTI i filtri attivi.
 *
 * @author gruppo10
 */
public class PlaylistAutomaticController {

    @FXML
    private TextField nameInput;

    @FXML
    private RadioButton favouriteRadio;

    @FXML
    private RadioButton explicitRadio;

    @FXML
    private RadioButton newReleaseRadio;

    @FXML
    private Label favouritePreview;

    @FXML
    private Label explicitPreview;

    @FXML
    private Label newReleasePreview;

    @FXML
    private TextField genreInput;
    
    @FXML
    private TextField authorInput;

    @FXML
    private TextField yearInput;

    @FXML
    private Button createButton;

    @FXML
    private Button buttonBack;

    @FXML
    private FlowPane personalTagsPane;
    
    private List<RadioButton> personalTagsRadios = new ArrayList<>();

    private PlaylistList playlistList;
    private ObservableList<Playlist> playlistListObservable;

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Configura i listener per mostrare/nascondere i badge preview dei tag
     * e per forzare l'input numerico sul campo anno.
     */
    @FXML
    public void initialize() {
        TagUIHelper.bindTagPreview(favouriteRadio, favouritePreview);
        TagUIHelper.bindTagPreview(explicitRadio, explicitPreview);
        TagUIHelper.bindTagPreview(newReleaseRadio, newReleasePreview);

        // Listener per forzare l'input a essere solo numerico e di massimo 4 cifre per l'anno
        yearInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String filtered = newValue.replaceAll("[^\\d]", "");
            if (filtered.length() > 4) {
                filtered = filtered.substring(0, 4);
            }
            if (!yearInput.getText().equals(filtered)) {
                yearInput.setText(filtered);
            }
        });

        populatePersonalTags();
    }

    /**
     * Genera dinamicamente i radio button per i tag personali recuperati dal sistema.
     */
    private void populatePersonalTags() {
        List<String> tags = PersonalTagManager.getInstance().getPersonalTags();
        TagUIHelper.populatePersonalTags(
                tags,
                personalTagsPane,
                personalTagsRadios,
                "Nessun tag personale creato. Aggiungili dalla schermata iniziale!"
        );
    }

    /**
     * Imposta il riferimento alla collezione globale delle playlist.
     *
     * @param p l'oggetto {@link PlaylistList}
     */
    public void setPlaylistList(PlaylistList p) {
        this.playlistList = p;
    }

    /**
     * Imposta la lista osservabile per aggiornamento UI.
     *
     * @param o l'{@link ObservableList} delle playlist
     */
    public void setObservable(ObservableList<Playlist> o) {
        this.playlistListObservable = o;
    }

    /**
     * Filtra le tracce della TrackList in base ai criteri selezionati dall'utente.
     * Tutti i filtri attivi devono essere soddisfatti contemporaneamente (logica AND).
     *
     * @return lista di tracce che soddisfano tutti i filtri
     */
    private ArrayList<Track> filterTracks() {
        ArrayList<Track> result = new ArrayList<>();

        if (!TrackList.exists()) {
            return result;
        }

        TrackList trackList = TrackList.getTrackListPointer();
        boolean filterFavourite = favouriteRadio.isSelected();
        boolean filterExplicit = explicitRadio.isSelected();
        boolean filterNewRelease = newReleaseRadio.isSelected();
        String genreFilter = genreInput.getText().trim();
        String authorFilter = (authorInput != null) ? authorInput.getText().trim() : "";
        String yearFilter = yearInput.getText().trim();

        Year yearValue = null;
        if (!yearFilter.isEmpty()) {
            try {
                yearValue = Year.parse(yearFilter);
            } catch (Exception e) {
                return result;
            }
        }
        
        List<String> requiredPersonalTags = new ArrayList<>();
        for (RadioButton rb : personalTagsRadios) {
            if (rb.isSelected()) {
                requiredPersonalTags.add(rb.getUserData().toString());
            }
        }

        for (Track track : trackList.getTracks()) {
            boolean matches = true;

            // Filtro tag: se il radio è selezionato, la traccia DEVE avere quel tag
            if (filterFavourite && !track.isFavourite()) {
                matches = false;
            }
            if (filterExplicit && !track.isExplicitContent()) {
                matches = false;
            }
            if (filterNewRelease && !track.isNewRelease()) {
                matches = false;
            }

            // Filtro genere (case-insensitive)
            if (!genreFilter.isEmpty() && !track.getGenre().equalsIgnoreCase(genreFilter)) {
                matches = false;
            }

            // Filtro autore (case-insensitive)
            if (!authorFilter.isEmpty() && !track.getAuthor().equalsIgnoreCase(authorFilter)) {
                matches = false;
            }

            // Filtro anno
            if (yearValue != null && !track.getYear().equals(yearValue)) {
                matches = false;
            }
            
            // Filtro tag personali
            if (!requiredPersonalTags.isEmpty()) {
                List<String> trackTags = track.getPersonalTags();
                if (trackTags == null) {
                    matches = false;
                } else {
                    for (String reqTag : requiredPersonalTags) {
                        if (!trackTags.contains(reqTag)) {
                            matches = false;
                            break;
                        }
                    }
                }
            }

            if (matches) {
                result.add(track);
            }
        }

        return result;
    }

    /**
     * Verifica che almeno un filtro sia stato selezionato o compilato.
     *
     * @return {@code true} se almeno un filtro è attivo
     */
    private boolean hasAtLeastOneFilter() {
        boolean hasPersonalTag = personalTagsRadios.stream().anyMatch(RadioButton::isSelected);
        return favouriteRadio.isSelected()
                || explicitRadio.isSelected()
                || newReleaseRadio.isSelected()
                || hasPersonalTag
                || !genreInput.getText().trim().isEmpty()
                || !authorInput.getText().trim().isEmpty()
                || !yearInput.getText().trim().isEmpty();
    }

    /**
     * Valida i dati di input inseriti dall'utente.
     * Mostra un alert specifico in caso di errore.
     *
     * @param playlistName il nome della playlist
     * @param yearText     il testo dell'anno
     * @return {@code true} se tutti i controlli sono superati
     */
    private boolean isInputValid(String playlistName, String yearText) {
        // Validazione nome
        if (playlistName.isEmpty()) {
            AlertManager.showMessage(Alert.AlertType.WARNING,
                    "Nome mancante",
                    "Inserisci un nome per la playlist.",
                    "Il nome della playlist è obbligatorio.");
            return false;
        }

        // Verifica che almeno un filtro sia attivo
        if (!hasAtLeastOneFilter()) {
            AlertManager.showMessage(Alert.AlertType.WARNING,
                    "Nessun filtro selezionato",
                    "Seleziona almeno un filtro.",
                    "Devi selezionare almeno un tag, oppure inserire un genere, un autore o un anno.");
            return false;
        }

        // Verifica TrackList esiste
        if (!TrackList.exists() || TrackList.getTrackListPointer().getTracks().isEmpty()) {
            AlertManager.showMessage(Alert.AlertType.WARNING,
                    "Libreria vuota",
                    "Non ci sono tracce nella libreria.",
                    "Aggiungi delle tracce alla libreria prima di creare una playlist automatica.");
            return false;
        }

        // Validazione anno se inserito
        if (!yearText.isEmpty()) {
            if (!yearText.matches("\\d{4}")) {
                AlertManager.showMessage(Alert.AlertType.WARNING,
                        "Anno non valido",
                        "L'anno deve contenere esattamente 4 cifre numeriche.",
                        "Inserisci un anno in formato numerico (es. 2024).");
                return false;
            }
            try {
                int y = Integer.parseInt(yearText);
                if (y > Year.now().getValue()) {
                    AlertManager.showMessage(Alert.AlertType.WARNING,
                            "Anno non valido",
                            "L'anno non può essere maggiore di quello attuale.",
                            "L'anno deve essere minore o uguale a " + Year.now().getValue() + ".");
                    return false;
                }
            } catch (NumberFormatException e) {
                AlertManager.showMessage(Alert.AlertType.WARNING,
                        "Anno non valido",
                        "L'anno inserito non è un numero valido.",
                        "Inserisci un anno in formato numerico (es. 2024).");
                return false;
            }
        }

        // Verifica dati necessari
        if (playlistList == null || playlistListObservable == null) {
            AlertManager.showMessage(Alert.AlertType.ERROR,
                    "Errore interno",
                    "Dati mancanti.",
                    "Impossibile creare la playlist. Riprova dalla schermata principale.");
            return false;
        }

        return true;
    }

    /**
     * Gestisce il click sul pulsante "Crea Playlist".
     * Valida input, filtra tracce, e crea la playlist se ci sono risultati.
     * Se nessuna traccia corrisponde ai filtri, avvisa l'utente con un alert.
     *
     * @param actionEvent l'evento generato dal click
     */
    @FXML
    void createPlaylist(ActionEvent actionEvent) {
        String playlistName = nameInput.getText().trim();
        String yearText = yearInput.getText().trim();

        // Validazione degli input
        if (!isInputValid(playlistName, yearText)) {
            return;
        }

        // Filtra tracce
        ArrayList<Track> matchingTracks = filterTracks();

        // Se nessuna traccia corrisponde, avvisa e non crea la playlist
        if (matchingTracks.isEmpty()) {
            AlertManager.showMessage(Alert.AlertType.INFORMATION,
                    "Nessun risultato",
                    "Nessuna traccia corrisponde ai filtri selezionati.",
                    "Prova a modificare i criteri di ricerca.");
            return;
        }

        // Crea playlist e aggiungi tracce
        try {
            PlaylistList.getPlaylistListPointer().checkValidName(playlistName);
            Playlist playlist = new Playlist(playlistName);
            for (Track track : matchingTracks) {
                playlist.addTrack(track);
            }


            // Aggiungi alla lista globale tramite Command pattern
            AddPlaylistCommand command = new AddPlaylistCommand(playlist, playlistList, playlistListObservable);
            CommandInvoker.getCommandInvokerPointer().setCommand(command);

            // Mostra conferma
            AlertManager.showMessage(Alert.AlertType.INFORMATION,
                    "Playlist creata",
                    "La playlist \"" + playlistName + "\" è stata creata con successo!",
                    matchingTracks.size() == 1
                            ? "È stata aggiunta 1 traccia."
                            : "Sono state aggiunte " + matchingTracks.size() + " tracce.");

            // Chiudi la finestra
            goBack(actionEvent);

        } catch (Exception e) {
            AlertManager.showMessage(Alert.AlertType.ERROR,
                    "Errore",
                    "Si è verificato un errore durante la creazione.",
                    e.getMessage());
        }
    }

    /**
     * Chiude la finestra corrente.
     *
     * @param actionEvent l'evento generato dal click
     */
    @FXML
    void goBack(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

}