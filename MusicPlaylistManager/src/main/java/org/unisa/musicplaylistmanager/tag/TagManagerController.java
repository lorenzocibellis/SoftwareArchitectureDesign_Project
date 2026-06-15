package org.unisa.musicplaylistmanager.tag;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Stage;

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
                    setGraphic(badge);
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
                alert.setHeaderText("Tag già esistente");
                alert.setContentText("Hai già inserito questo tag nella tua lista.");
                alert.showAndWait();
            } else {
                newTagField.clear();
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
