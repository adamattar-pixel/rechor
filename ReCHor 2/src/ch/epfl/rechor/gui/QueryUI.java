package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Enregistrement public et immuable représentant l'interface de requête graphique
 * permettant à l'utilisateur de sélectionner l'arrêt de départ, l'arrêt d'arrivée,
 * la date et l'heure de son voyage.
 *
 * Cette interface regroupe les éléments graphiques JavaFX associés et expose
 * des valeurs observables pour suivre les saisies utilisateur.
 *
 * Les labels respectent la typographie suisse en utilisant une espace fine insécable
 * (Unicode U+202F) avant les deux-points (ex. "Départ\u202f:").
 *
 * @param rootNode le nœud JavaFX racine du graphe de scène de l'interface
 * @param depStop0 observable contenant le nom de l'arrêt de départ sélectionné
 * @param arrStop0 observable contenant le nom de l'arrêt d'arrivée sélectionné
 * @param date0    observable contenant la date du voyage sélectionnée
 * @param time0    observable contenant l'heure du voyage sélectionnée
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public record QueryUI(Node rootNode, ObservableValue<String> depStop0,
                      ObservableValue<String> arrStop0, ObservableValue<LocalDate> date0,
                      ObservableValue<LocalTime> time0) {

    /**
     * Crée et initialise l'interface de requête graphique.
     * Cette méthode construit le graphe de scène composé des champs de départ,
     * d'arrivée, de date et d'heure, ainsi que le bouton d'échange des arrêts.
     *
     * @param index un index de noms d'arrêts utilisé pour l'autocomplétion des champs
     * @return une instance de QueryUI contenant le nœud racine et les valeurs observables
     */
    public static QueryUI create(StopIndex index) {
        //Champs de l'heure
        DateTimeFormatter strictTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter relaxedTimeFormat = DateTimeFormatter.ofPattern("H:mm");
        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(
                new LocalTimeStringConverter(strictTimeFormat, relaxedTimeFormat));
        timeFormatter.setValue(LocalTime.now());

        TextField timeField = new TextField();
        timeField.setId("time");
        timeField.setTextFormatter(timeFormatter);
        Label timeLabel = new Label("Heure\u202f:");

        //Sélecteur de date
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setId("date");
        Label dateLabel = new Label("Date\u202f:");

        HBox dateTimeBox = new HBox(dateLabel, datePicker, timeLabel, timeField);

        //Champ arrêt d'arrivée
        StopField arrivalStopField = StopField.create(index);
        arrivalStopField.textField().setPromptText("Nom de l'arrêt d'arrivée");
        Label arrivalLabel = new Label("Arrivée\u202f:");

        //Champ arrêt de départ
        StopField departureStopField = StopField.create(index);
        departureStopField.textField().setId("depStop");
        departureStopField.textField().setPromptText("Nom de l'arrêt de depart");
        Label departureLabel = new Label("Départ\u202f:");

        //Bouton d'échange des arrêts
        Button swapButton = new Button("\u2194");
        swapButton.setOnAction(actionEvent -> {
            String tempText = departureStopField.textField().getText();
            departureStopField.setTo(arrivalStopField.textField().getText());
            arrivalStopField.setTo(tempText);
        });

        HBox stopsBox =
                new HBox(departureLabel, departureStopField.textField(), swapButton, arrivalLabel,
                        arrivalStopField.textField());

        //Conteneur principal
        VBox queryBox = new VBox(stopsBox, dateTimeBox);
        queryBox.setId("query");
        queryBox.getStylesheets().add("query.css");

        return new QueryUI(queryBox, departureStopField.stopO(), arrivalStopField.stopO(),
                datePicker.valueProperty(), timeFormatter.valueProperty());
    }
}
