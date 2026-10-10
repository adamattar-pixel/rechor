package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Journey.Leg;
import ch.epfl.rechor.journey.Journey.Leg.Foot;
import ch.epfl.rechor.journey.Journey.Leg.Transport;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface graphique affichant un résumé visuel d’une liste de trajets (Journey).
 *
 * Affiche les trajets dans une ListView avec des cellules personnalisées montrant
 * les heures de départ, d’arrivée, la durée, le trajet, ainsi que les transferts à pied
 * représentés graphiquement sous forme de cercles positionnés sur une ligne.
 *
 * La sélection dans la liste est automatiquement mise à jour selon une heure choisie.
 *
 * Ce record contient :
 * le nœud JavaFX racine (rootNode)
 * l’observable du trajet sélectionné dans la liste (selectStopIndexedJourneyO)
 *
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public record SummaryUI(Node rootNode, ObservableValue<Journey> selectStopIndexedJourneyO) {
    private static final double DEFAULT_PANE_SIDE = 0;
    private static final double DEFAULT_IMAGE_SIDE = 20;
    private static final double RADIUS = 3;
    private static final int PADDING = 5;

    /**
     * Crée une interface graphique SummaryUI affichant une liste de trajets.
     *
     * Configure une ListView avec des cellules personnalisées,
     * met à jour la sélection automatiquement selon l’heure choisie.
     *
     * @param selectedJourneys observable contenant la liste des trajets à afficher
     * @param chosenTime       observable contenant l’heure choisie pour sélectionner un trajet
     * @return une instance de SummaryUI prête à être utilisée dans une scène JavaFX
     */
    public static SummaryUI create(ObservableValue<List<Journey>> selectedJourneys,
                                   ObservableValue<LocalTime> chosenTime) {
        ListView<Journey> journeyList = new ListView<>();
        journeyList.getStylesheets().add("summary.css");
        journeyList.setCellFactory(listView -> new JourneyCell());

        // Mise à jour des changements dans la liste des trajets sélectionnés
        selectedJourneys.subscribe(selectedJourney -> {
            List<Journey> journeys = selectedJourney == null ? new ArrayList<>() : selectedJourney;
            journeyList.getItems().setAll(journeys);
            // Mise à jour de la sélection selon l'heure choisie (ou garde la précédente)
            if (chosenTime.getValue() != null) {
                updateJourneyList(journeyList, chosenTime.getValue());
            }
        });

        // Mise à jour des changements de l'heure choisie (ou garde la précédente)
        chosenTime.subscribe(selectedTime -> {
            if (selectedTime != null) {
                updateJourneyList(journeyList, selectedTime);
            }
        });
        return new SummaryUI(journeyList, journeyList.getSelectionModel().selectedItemProperty());
    }


    /**
     * Met à jour la sélection dans la ListView en fonction de l’heure choisie.
     *
     * Sélectionne le premier trajet partant après l’heure donnée,
     * ou le dernier trajet si aucun ne correspond.
     *
     * @param journeyList  ListView des trajets
     * @param selectedTime heure choisie pour filtrer la sélection
     */
    private static void updateJourneyList(ListView<Journey> journeyList, LocalTime selectedTime) {
        List<Journey> journeys = journeyList.getItems();

        // Recherche du premier trajet dont l'heure de départ est égale ou postérieure à l'heure sélectionnée
        Journey selectedJourney = journeys.stream()
                .filter(j -> !j.depTime().isBefore(selectedTime.
                        atDate(journeys.getFirst().arrTime().toLocalDate())))
                .findFirst()
                .orElseGet(() -> journeys.isEmpty() ? null : journeys.getLast());

        // Mise à jour de la sélection dans la ListView et défilement vers le trajet sélectionné
        if (selectedJourney != null) {
            journeyList.getSelectionModel().select(selectedJourney);
            journeyList.scrollTo(selectedJourney);
        }
    }

    /**
     * Cellule graphique personnalisée affichant un résumé d’un trajet dans la liste.
     *
     * Affiche heures, durée, direction avec icône et transferts à pied sous forme graphique.
     */
    private static class JourneyCell extends ListCell<Journey> {
        private ImageView directionImage;
        private Text departure;
        private Text durationText;
        private Text directionText;
        private Text arrivalText;
        private Group groupTransfer;
        private List<Circle> circles;
        private Pane paneTransfer;
        private BorderPane journey = new BorderPane();

        /**
         * Initialise la cellule avec les éléments graphiques et leur style.
         */
        public JourneyCell() {
            departure = new Text();
            departure.setId("departure");
            departure.getStyleClass().add("departure");

            directionImage = new ImageView();
            directionText = new Text();
            HBox route = new HBox();
            route.getStyleClass().add("route");
            // Ajout de l'image et du texte de direction dans une boîte horizontale
            route.getChildren().addAll(directionImage, directionText);

            durationText = new Text();
            HBox duration = new HBox();
            duration.getStyleClass().add("duration");
            duration.getChildren().add(durationText);

            arrivalText = new Text();

            circles = new ArrayList<>();
            groupTransfer = new Group();

            // Pane personnalisée qui dessine une ligne horizontale avec des cercles représentant les transferts
            paneTransfer = new Pane() {
                @Override
                public void layoutChildren() {
                    super.layoutChildren();
                    getChildren().clear();
                    setPrefSize(DEFAULT_PANE_SIDE, DEFAULT_PANE_SIDE);
                    Line lineTransfer = new Line(PADDING, getHeight() / 2, getWidth() - PADDING,
                            getHeight() / 2);

                    // Positionnement des cercles selon leur ratio (userData) sur la ligne
                    for (int i = 0; i < groupTransfer.getChildren().size(); i++) {
                        Circle circle = (Circle) groupTransfer.getChildren().get(i);
                        double x = (lineTransfer.getEndX() - lineTransfer.getStartX()) *
                                   ((double) circle.getUserData()) + PADDING;
                        double y = lineTransfer.getEndY();
                        circle.setCenterX(x);
                        circle.setCenterY(y);
                    }

                    groupTransfer.getChildren().setAll(circles);
                    getChildren().addAll(lineTransfer, groupTransfer);
                }
            };

            journey.getStyleClass().add("journey");
            journey.setTop(route);
            journey.setLeft(departure);
            journey.setRight(arrivalText);
            journey.setBottom(duration);
            journey.setCenter(paneTransfer);
        }

        /**
         * Met à jour le contenu graphique de la cellule selon le trajet donné.
         *
         * @param item  trajet à afficher
         * @param empty indique si la cellule est vide
         */
        @Override
        protected void updateItem(Journey item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                departure.setText(FormatterFr.formatTime(item.depTime()));
                arrivalText.setText(FormatterFr.formatTime(item.arrTime()));
                durationText.setText(FormatterFr.formatDuration(item.duration()));

                List<Leg> legs = item.legs();
                Leg firstLeg = legs.getFirst();

                // Identification du premier segment de transport (non-piéton)
                Transport t = (firstLeg instanceof Transport) ? (Transport) firstLeg :
                        (Transport) legs.get(1);

                directionText.setText(FormatterFr.formatRouteDestination(t));
                directionImage.setImage(VehicleIcons.iconFor(t.vehicle()));
                directionImage.setFitWidth(DEFAULT_IMAGE_SIDE);
                directionImage.setFitHeight(DEFAULT_IMAGE_SIDE);

                circles.clear();

                Leg lastLeg = legs.getLast();
                Duration totalDuration = item.duration();

                // Parcours des legs pour détecter les segments à pied entre le début et la fin
                for (Leg leg : legs) {
                    boolean isFirst = leg == firstLeg;
                    boolean isLast = leg == lastLeg;

                    // Pour chaque segment de type Foot (transfert à pied), sauf début et fin,
                    // on crée un cercle positionné proportionnellement à la durée écoulée depuis le départ
                    if (leg instanceof Foot && !isFirst && !isLast) {
                        Duration legStartOffset = Duration.between(item.depTime(), leg.depTime());
                        double ratio =
                                (double) legStartOffset.toMinutes() / totalDuration.toMinutes();

                        Circle transfer = new Circle(RADIUS);
                        transfer.getStyleClass().add("transfer");
                        transfer.setUserData(ratio);
                        circles.add(transfer);
                    }
                }

                // Ajout d'un cercle pour le point de départ (ratio 0.0)
                Circle departureCircle = new Circle(RADIUS);
                departureCircle.getStyleClass().add("dep-arr");
                departureCircle.setUserData(0.0);
                circles.add(departureCircle);

                // Ajout d'un cercle pour le point d'arrivée (ratio 1.0)
                Circle arrivalCircle = new Circle(RADIUS);
                arrivalCircle.getStyleClass().add("dep-arr");
                arrivalCircle.setUserData(1.0);
                circles.add(arrivalCircle);

                setGraphic(journey);
            }
        }
    }
}