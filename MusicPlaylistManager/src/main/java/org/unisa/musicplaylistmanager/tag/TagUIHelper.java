package org.unisa.musicplaylistmanager.tag;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * Classe di utilità per la UI dei tag.
 */
public class TagUIHelper {

    /**
     * Genera dinamicamente i radio button per i tag personali e li aggiunge al contenitore.
     * @param tags la lista dei tag personali come stringhe
     * @param personalTagsPane il FlowPane dove inserire graficamente i nodi
     * @param personalTagsRadios la lista dove memorizzare i RadioButton generati
     * @param placeholderText il testo da mostrare se la lista dei tag è vuota
     */
    public static void populatePersonalTags(List<String> tags, FlowPane personalTagsPane, List<RadioButton> personalTagsRadios, String placeholderText) {
        personalTagsPane.getChildren().clear();
        personalTagsRadios.clear();

        if (tags.isEmpty()) {
            Label placeholder = new Label(placeholderText);
            placeholder.getStyleClass().add("tags-placeholder");
            personalTagsPane.getChildren().add(placeholder);
            return;
        }

        for (String tag : tags) {
            HBox card = new HBox();
            card.setAlignment(Pos.CENTER_LEFT);
            card.setSpacing(6.0);
            card.getStyleClass().add("tag-toggle-card");
            card.setPadding(new Insets(6.0, 10.0, 6.0, 10.0));

            RadioButton rb = new RadioButton("");
            rb.setUserData(tag);

            Label preview = new Label(tag);
            preview.getStyleClass().addAll("tag-badge", "tag-personal");

            card.setOnMouseClicked(e -> {
                if (!rb.isDisabled()) {
                    rb.setSelected(!rb.isSelected());
                }
            });

            card.getChildren().addAll(rb, preview);
            personalTagsRadios.add(rb);
            personalTagsPane.getChildren().add(card);
        }
    }

    /**
     * Collega un RadioButton alla visibilità del suo tag badge.
     * Quando il RadioButton viene selezionato o deselezionato, il badge appare o scompare.
     *
     * @param radio il RadioButton da osservare
     * @param preview il Label badge da mostrare/nascondere
     */
    public static void bindTagPreview(RadioButton radio, Label preview) {
        if (radio != null && preview != null) {
            preview.setVisible(radio.isSelected());
            preview.setManaged(radio.isSelected());
            radio.selectedProperty().addListener((obs, oldVal, newVal) -> {
                preview.setVisible(newVal);
                preview.setManaged(newVal);
            });
        }
    }
}
