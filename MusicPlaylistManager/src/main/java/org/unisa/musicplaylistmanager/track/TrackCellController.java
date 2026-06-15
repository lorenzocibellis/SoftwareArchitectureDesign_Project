package org.unisa.musicplaylistmanager.track;

import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.value.ChangeListener;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import org.unisa.musicplaylistmanager.service.player.ActivePlayerManager;

/**
 * Controller per una singola cella (riga) nella ListView delle tracce.
 * Estende {@link ListCell} per integrarsi con la ListView di JavaFX e gestire
 * dinamicamente la visualizzazione e l'interazione per singola traccia.
 *
 * @author gruppo10
 */
public class TrackCellController extends ListCell<Track> {

    //DEFINIZIONE OGGETTI JAVAFX
    @FXML
    private Label titleLabel;
    @FXML
    private Label detailsLabel;
    @FXML
    private Button infoButton;
    @FXML
    private Label favouriteTag;
    @FXML
    private Label explicitTag;
    @FXML
    private Label newReleaseTag;
    @FXML
    private VBox iconContainer;
    @FXML
    private Label iconLabel;
    @FXML
    private javafx.scene.image.ImageView coverImageView;
    @FXML
    private HBox personalTagsBox;

    // definizione attributi

    // path verso i file View.fxml
    private String resourceRoot = "/org/unisa/musicplaylistmanager/track/";

    // indica l'oggetto che compone tutta la vista
    private HBox root;

    // utilitaria per operazioni "on-click"
    private final Consumer<Track> onInfoClicked;


    // METODI

    /**
     * Listener che osserva i cambiamenti della traccia in riproduzione.
     * Mantenuto come riferimento forte per evitare che venga rimosso dal Garbage Collector
     * quando si utilizza un WeakChangeListener.
     */
    private final ChangeListener<Track> playerListener = (obs, oldTrack, newTrack) -> {
        Track myTrack = getItem();
        if (myTrack != null) {
            boolean isPlaying = (newTrack != null && newTrack.equals(myTrack));
            updateStyle(isPlaying);
        }
    };

    /**
     * Costruttore.
     *
     * @param onInfoClicked funzione che viene eseguita quando il bottone "info" viene cliccato.
     */
    public TrackCellController(Consumer<Track> onInfoClicked) {
        this.onInfoClicked = onInfoClicked;
        loadFXML();
        
        //  listener legato alla cella per reagire istantaneamente ai cambi di traccia
        ActivePlayerManager.getInstance().currentTrackProperty().addListener(
            new WeakChangeListener<>(playerListener)
        );
    }

    /**
     * Carica il file FXML associato e imposta questo controller.
     */
    private void loadFXML() {
        try {
            // caricamento della View e settaggio del controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceRoot + "TrackCellView.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Applica gli stili CSS alla cella per indicare visivamente se la traccia è 
     * attualmente in riproduzione o meno. Gestisce anche il caricamento della copertina.
     *
     * @param isPlaying {@code true} se la traccia è in riproduzione e deve essere evidenziata.
     */
    private void updateStyle(boolean isPlaying) {
        Track myTrack = getItem();
        
        // Controlla se la traccia ha una copertina personalizzata (path assoluto)
        if (myTrack != null && myTrack.getCoverImage() != null && !myTrack.getCoverImage().trim().isEmpty()) {
            iconLabel.setVisible(false);
            iconLabel.setManaged(false);
            coverImageView.setVisible(true);
            coverImageView.setManaged(true);
            
            try {
                // MODIFICA CRUCIALE: Lettura del file tramite percorso assoluto salvato dal FileChooser
                File file = new File(myTrack.getCoverImage());
                if (file.exists()) {
                    coverImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
                } else {
                    // Fallback se il file immagine non viene trovato sul disco
                    setDefaultIcon(isPlaying);
                }
            } catch (Exception e) {
                setDefaultIcon(isPlaying);
            }
        } else {
            // Mostra l'icona testuale standard
            setDefaultIcon(isPlaying);
        }

        // Gestione del colore del testo e dello sfondo del container
        if (isPlaying) {
            titleLabel.setStyle("-fx-text-fill: #007AFF;"); 
            if (myTrack == null || myTrack.getCoverImage() == null || myTrack.getCoverImage().trim().isEmpty()) {
                iconContainer.setStyle("-fx-background-color: #D5E5F5; -fx-background-radius: 5;");
                iconLabel.setStyle("-fx-text-fill: #007AFF;");
            } else {
                iconContainer.setStyle("-fx-background-color: transparent;");
            }
        } else {
            titleLabel.setStyle("-fx-text-fill: #1D1D1F;");
            if (myTrack == null || myTrack.getCoverImage() == null || myTrack.getCoverImage().trim().isEmpty()) {
                iconContainer.setStyle("-fx-background-color: #EAEAEA; -fx-background-radius: 5;");
                iconLabel.setStyle("-fx-text-fill: #888888;"); 
            } else {
                iconContainer.setStyle("-fx-background-color: transparent;");
            }
        }
    }

    /**
    * Ripristina l'icona testuale predefinita, nascondendo la copertina personalizzata.
    * Mostra il simbolo animato {@code "ılılı"} se la traccia è in riproduzione,
    * oppure il simbolo musicale {@code "♫"} se è ferma.
     *
    * @param isPlaying {@code true} se la traccia è attualmente in riproduzione,
    *                  {@code false} altrimenti
    */
    private void setDefaultIcon(boolean isPlaying) {
        coverImageView.setVisible(false);
        coverImageView.setManaged(false);
        iconLabel.setVisible(true);
        iconLabel.setManaged(true);
        if (isPlaying) {
            iconLabel.setText("ılılı");
        } else {
            iconLabel.setText("♫");
        }
    }

    /**
     * Metodo chiamato da JavaFX ogni volta che la cella deve essere aggiornata.
     *
     * @param track L'oggetto Track da visualizzare in questa riga.
     * @param empty true se la riga è vuota, false altrimenti.
     */
    @Override
    protected void updateItem(Track track, boolean empty) {
        super.updateItem(track, empty);

        // controllo sull'esistenza dei dati da aggiornare
        if (empty || track == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Popola i componenti FXML con i dati della traccia
            titleLabel.setText(track.getTitle());
            detailsLabel.setText(track.getAuthor() + " - " + track.getYear().getValue());

            // Mostra/nascondi i tag visivi in base ai flag della traccia
            setTagVisible(favouriteTag, track.isFavourite());
            setTagVisible(explicitTag, track.isExplicitContent());
            setTagVisible(newReleaseTag, track.isNewRelease());
            
            // gestione dei tag personali dinamici
            if (personalTagsBox != null) {
                personalTagsBox.getChildren().clear();
                if (track.getPersonalTags() != null) {
                    for (String pTag : track.getPersonalTags()) {
                        Label tagLabel = new Label(pTag);
                        tagLabel.getStyleClass().addAll("tag-badge", "tag-personal");
                        personalTagsBox.getChildren().add(tagLabel);
                    }
                }
            }

            // Imposta l'azione per il bottone "info"
            infoButton.setOnAction(event -> {
                event.consume(); // Impedisce al click di propagarsi alla cella sottostante
                if (onInfoClicked != null) {
                    onInfoClicked.accept(track);
                }
            });

            // Gestione dell'aspetto grafico in base al fatto che la traccia sia in riproduzione o meno.
            // Recupera la traccia attualmente attiva da ActivePlayerManager.
            Track playingTrack = ActivePlayerManager.getInstance().currentTrackProperty().get();
            boolean isPlaying = (playingTrack != null && playingTrack.equals(track));

            updateStyle(isPlaying);

            // Imposta il layout FXML caricato come grafica della cella
            setText(null);
            setGraphic(root);
        }
    }

    /**
     * Rende il bottone delle informazioni (i) visibile o nascosto in base
     * al parametro passato. Utile ad esempio per nasconderlo quando si stanno
     * semplicemente selezionando tracce da aggiungere a una playlist.
     * * @param visible {@code true} per mostrare il bottone, {@code false} per nasconderlo
     * @param visible
     */
    public void setInfoButtonVisible(boolean visible) {
        if (infoButton != null) {
            infoButton.setVisible(visible);
            infoButton.setManaged(visible);
        }
    }

    /**
     * Imposta la visibilità e la gestione di un tag.
     *
     * @param tag il Label del tag
     * @param visible {@code true} per mostrare il tag
     */
    private void setTagVisible(Label tag, boolean visible) {
        if (tag != null) {
            tag.setVisible(visible);
            tag.setManaged(visible);
        }
    }
}