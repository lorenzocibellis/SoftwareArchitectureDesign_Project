package org.unisa.musicplaylistmanager.tag;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Controller per la gestione dei Tag Personali.
 * Gestisce l'interfaccia utente per la visualizzazione e l'aggiunta 
 * dei tag personalizzati globali.
 */
public class TagManagerController {

    @FXML
    private ListView<String> tagsList;
    @FXML
    private TextField newTagField;
    @FXML
    private Button addButton;

    /**
     * Popola la ListView con i tag esistenti e configura l'aspetto delle celle.
     * Gestisce inoltre lo stato abilitato/disabilitato del pulsante di aggiunta.
     */
    @FXML
    public void initialize() {
        ObservableList<String> tags = PersonalTagManager.getInstance().getPersonalTags();
        tagsList.setItems(tags);
        tagsList.setPlaceholder(new Label("Nessun tag inserito."));

        tagsList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("tag-badge", "tag-personal");
                    
                    Button deleteButton = new Button("✕");
                    deleteButton.getStyleClass().add("delete-tag-button");
                    deleteButton.setOnAction(e -> deleteTag(item));

                    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10, badge, spacer, deleteButton);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    setGraphic(hbox);
                }
            }
        });

        addButton.setDisable(true);
        newTagField.textProperty().addListener((obs, oldVal, newVal) -> {
            addButton.setDisable(newVal.trim().isEmpty());
        });

        Platform.runLater(() -> newTagField.requestFocus());
    }

    /**
     * Aggiunge un nuovo tag alla lista dei tag personali.
     * Se il tag esiste già, viene mostrato un avviso all'utente.
     * @param event l'evento generato dal click sul pulsante
     */
    @FXML
    public void addTag(ActionEvent event) {
        String tag = newTagField.getText();
        if (tag != null && !tag.trim().isEmpty()) {
            boolean added = PersonalTagManager.getInstance().addTag(tag);
            if (!added) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attenzione");
                alert.setHeaderText("Tag non valido o già esistente");
                alert.setContentText("Il tag esiste già nella tua lista, oppure hai provato a usare un nome riservato di sistema (es. Preferita, Esplicita, Nuova uscita).");
                alert.showAndWait();
            } else {
                newTagField.clear();
            }
        }
    }

    private Runnable onTagDeleted;

    /**
     * Imposta un'azione da eseguire quando un tag viene eliminato 
     * (es. aggiornare l'interfaccia grafica principale).
     */
    public void setOnTagDeleted(Runnable onTagDeleted) {
        this.onTagDeleted = onTagDeleted;
    }

    /**
     * Gestisce l'eliminazione di un tag delegando la logica al manager globale.
     */
    private void deleteTag(String tag) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma Eliminazione");
        confirmAlert.setHeaderText("Stai per eliminare il tag: " + tag);
        confirmAlert.setContentText("L'eliminazione rimuoverà questo tag da tutte le tracce che lo possiedono. Sei sicuro di voler procedere?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            // rimuove il tag dalla lista globale e delega la pulizia delle tracce
            boolean wasRemoved = PersonalTagManager.getInstance().removeTag(tag);

            // notifica la finestra principale SOLO se il tag è stato effettivamente eliminato
            if (wasRemoved && onTagDeleted != null) {
                onTagDeleted.run();
            }
        }
    }

    /**
     * Chiude la finestra corrente.
     * @param event l'evento generato dal click sul pulsante "Chiudi"
     */
    @FXML
    public void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
