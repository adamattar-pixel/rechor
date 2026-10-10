package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;

import javafx.scene.control.TextField;
import javafx.util.Subscription;

import java.util.ArrayList;

/**
 * Composant graphique pour saisir un arrêt (stop) avec autocomplétion.
 * Affiche une liste déroulante de correspondances possibles en fonction de la saisie.
 *
 * @param textField Champ de texte pour la saisie.
 * @param stopO     Observable du texte actuel (nom de l'arrêt).
 */
public record StopField(TextField textField, ObservableValue<String> stopO) {
    private final static int MAX_RESULTS = 30;
    private static final double DEFAULT_MAX_HEIGHT = 240;

    /**
     * Crée un StopField à partir d'un index d'arrêts pour proposer des suggestions.
     *
     * @param index L'index permettant de rechercher les arrêts correspondants à la saisie.
     * @return une instance de StopField avec autocomplétion.
     */
    public static StopField create(StopIndex index) {
        TextField query = new TextField();

        ListView<String> results = new ListView<>();

        Popup popup = new Popup();
        popup.getContent().add(results);
        popup.setHideOnEscape(false);

        // Navigation dans la liste avec flèches haut/bas dans le champ texte
        var selectionModel = results.getSelectionModel();
        query.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                selectionModel.selectPrevious();
                results.scrollTo(results.getSelectionModel().getSelectedIndex());
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                selectionModel.selectNext();
                results.scrollTo(results.getSelectionModel().getSelectedIndex());
                event.consume();
            }
        });

        ArrayList<Subscription> subscriptions = new ArrayList<>();

        // Afficher ou cacher le popup selon focus du champ texte
        query.focusedProperty().subscribe(observable -> {
            if (observable) {
                popup.show(query, query.getLayoutX(), query.getLayoutY());

                subscriptions.add(query.textProperty().subscribe(newQuery -> {
                    results.getItems().setAll(index.stopsMatching(newQuery, MAX_RESULTS));
                    results.setFocusTraversable(false);
                    results.setMaxHeight(DEFAULT_MAX_HEIGHT);
                    results.getSelectionModel().selectFirst();
                }));
                subscriptions.add(query.boundsInLocalProperty().subscribe(bounds -> {
                    Bounds bounds2 = query.localToScreen(bounds);
                    popup.setAnchorX(bounds2.getMinX());
                    popup.setAnchorY(bounds2.getMaxY());
                }));

            } else {
                query.textProperty().set(results.getSelectionModel().getSelectedItem());
                query.setText(selectionModel.getSelectedItem());
                popup.hide();
                subscriptions.forEach(Subscription::unsubscribe);
                subscriptions.clear();
            }
        });


        return new StopField(query, query.textProperty());
    }

    /**
     * Modifie le texte du champ avec un nom d'arrêt donné.
     *
     * @param StopName Nom de l'arrêt à placer dans le champ texte.
     * @return Une nouvelle instance StopField avec le même TextField mais texte mis à jour.
     */
    public StopField setTo(String StopName) {
        textField.setText(StopName);
        return new StopField(textField, stopO);
    }

}
