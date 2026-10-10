package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Journey.Leg;
import ch.epfl.rechor.journey.Journey.Leg.IntermediateStop;
import ch.epfl.rechor.journey.Journey.Leg.Foot;
import ch.epfl.rechor.journey.Journey.Leg.Transport;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;

import java.util.List;

/**
 * DetailUI sert à afficher les détails d'un voyage (Journey) dans une interface graphique JavaFX
 *
 * Cette classe crée une vue scrollable contenant la liste des étapes (legs) du voyage, avec
 * la possibilité d'afficher une carte du trajet et d'exporter le voyage au format calendrier iCal.
 *
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public record DetailUI(Node rootNode) {
    private final static double RADIUS = 3;
    private final static int SIDE_LENGTH = 31;
    private static final int FIRST_COLUMN = 0;
    private static final int THIRD_COLUMN = 2;
    private static final double DEFAULT_LINE_WIDTH = 2;

    /**
     * Crée une instance de DetailUI en observant un voyage observable.
     *
     * @param observableJourney Observable contenant le voyage à afficher, qui peut changer.
     * @return une nouvelle instance de DetailUI contenant la vue graphique.
     */
    public static DetailUI create(ObservableValue<Journey> observableJourney) {
        Pane annotationLayer = new Pane();
        annotationLayer.setId("annotations");

        // Liste de paires de cercles à relier par des lignes rouges (départ-arrivée)
        List<Pair<Circle, Circle>> lineConnectors = new ArrayList<>();

        GridPane legGrid = new GridPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                setId("legs");

                // Crée et affiche les lignes rouges entre les étapes
                List<Line> lines = lineConnectors.stream().map(pair -> {
                    Bounds b1 = pair.getKey().getBoundsInParent();
                    Bounds b2 = pair.getValue().getBoundsInParent();
                    double startX = b1.getMinX() + b1.getWidth() / 2;
                    double startY = b1.getMaxY();
                    double endX = b2.getMinX() + b2.getWidth() / 2;
                    double endY = b2.getMinY();
                    return new Line(startX, startY, endX, endY);
                }).peek(line -> {
                    line.setStrokeWidth(DEFAULT_LINE_WIDTH);
                    line.setStroke(Color.RED);
                }).toList();

                annotationLayer.getChildren().setAll(lines);
            }
        };
        StackPane visualStack = new StackPane(annotationLayer, legGrid);

        Button mapButton = new Button("Carte");
        Button calendarButton = new Button("Calendrier");
        HBox actionButtons = new HBox(3, mapButton, calendarButton);
        actionButtons.setId("buttons");

        VBox journeyView = new VBox(2, visualStack, actionButtons);
        VBox noJourneyView = new VBox(5, new Text("Aucun voyage"));
        noJourneyView.setId("no-journey");

        StackPane mainContainer = new StackPane(journeyView, noJourneyView);
        ScrollPane detailPane = new ScrollPane(mainContainer);
        detailPane.setId("detail");
        detailPane.getStylesheets().add("detail.css");

        // Gestion des actions des boutons
        mapButton.setOnAction(e -> openMap(observableJourney.getValue()));
        calendarButton.setOnAction(
                e -> exportJourneyAsCalendar(observableJourney.getValue(), mapButton));

        // Mise à jour automatique de l'interface quand le voyage observable change
        observableJourney.subscribe(journey -> {
            updateUI(journey, journeyView, noJourneyView, legGrid, lineConnectors);
        });

        return new DetailUI(detailPane);
    }

    /**
     * Met à jour l'interface graphique en fonction du voyage courant.
     *
     * @param journey        Le voyage à afficher (peut être null).
     * @param journeyView    Vue principale contenant les détails du voyage.
     * @param noJourneyView  Vue affichée si aucun voyage n'est sélectionné.
     * @param legGrid        Grille contenant les étapes du voyage.
     * @param lineConnectors Liste des paires de cercles à relier par des lignes.
     */
    private static void updateUI(Journey journey, VBox journeyView, VBox noJourneyView,
                                 GridPane legGrid, List<Pair<Circle, Circle>> lineConnectors) {
        boolean hasJourney = journey != null;
        journeyView.setVisible(hasJourney);
        noJourneyView.setVisible(!hasJourney);
        if (!hasJourney) return;

        legGrid.getChildren().clear();
        lineConnectors.clear();

        int row = 0;
        for (Leg leg : journey.legs()) {
            row = createRow(leg, legGrid, lineConnectors, row);
        }
    }

    /**
     * Crée une ligne dans la grille pour une étape (leg) du voyage.
     *
     * @param leg   L'étape à afficher.
     * @param grid  La grille dans laquelle ajouter les composants.
     * @param lines Liste des paires de cercles à relier par des lignes rouges.
     * @param row   La ligne de départ dans la grille.
     * @return la prochaine ligne libre dans la grille après ajout.
     */
    private static int createRow(Leg leg, GridPane grid, List<Pair<Circle, Circle>> lines,
                                 int row) {
        if (leg instanceof Foot foot) {
            // Cas Foot : juste afficher la description
            grid.add(new Text(FormatterFr.formatLeg(foot)), THIRD_COLUMN,
                    row + 1, 2, 1);
            return row + 2;

        } else if (leg instanceof Transport transport) {
            // Cas Transport : afficher horaires, stations, icône véhicule, arrêts intermédiaires, etc.
            Text depTime = new Text(FormatterFr.formatTime(transport.depTime()));
            depTime.getStyleClass().add("departure");

            Circle depCircle = new Circle(RADIUS, Color.BLACK);
            Text depStation = new Text(transport.depStop().name());
            Text depPlatform = new Text(FormatterFr.formatPlatformName(transport.depStop()));
            depPlatform.getStyleClass().add("departure");

            grid.addRow(row, depTime, depCircle, depStation, depPlatform);

            ImageView vehicleIcon = new ImageView(VehicleIcons.iconFor(transport.vehicle()));
            vehicleIcon.setFitWidth(SIDE_LENGTH);
            vehicleIcon.setFitHeight(SIDE_LENGTH);

            grid.add(new Text(FormatterFr.formatRouteDestination(transport)),
                    THIRD_COLUMN, row + 1, 2, 1);

            if (!transport.intermediateStops().isEmpty()) {
                grid.add(vehicleIcon, FIRST_COLUMN, row + 1, 1, 2);
                grid.add(createIntermediateStopsAccordion(transport), THIRD_COLUMN,
                        row + 2, 2, 1);

            } else {
                grid.add(vehicleIcon, FIRST_COLUMN, row + 1);
            }

            Text arrTime = new Text(FormatterFr.formatTime(transport.arrTime()));
            Circle arrCircle = new Circle(RADIUS, Color.BLACK);
            Text arrStation = new Text(transport.arrStop().name());
            Text arrPlatform = new Text(FormatterFr.formatPlatformName(transport.arrStop()));

            grid.addRow(row + 3, arrTime, arrCircle, arrStation, arrPlatform);

            lines.add(new Pair<>(depCircle, arrCircle));

            return row + 4;
        }
        return row;
    }

    /**
     * Crée un accordéon affichant les arrêts intermédiaires d'une étape de transport.
     *
     * @param leg Étape de transport contenant les arrêts intermédiaires.
     * @return un Accordion contenant la liste des arrêts intermédiaires.
     */
    private static Accordion createIntermediateStopsAccordion(Transport leg) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("intermediate-stops");

        int i = 0;
        for (IntermediateStop stop : leg.intermediateStops()) {
            Text arrName = new Text(FormatterFr.formatTime(stop.arrTime()));
            Text depName = new Text(FormatterFr.formatTime(stop.depTime()));
            Text stopName = new Text(stop.stop().name());
            grid.addRow(i++, arrName, depName, stopName);
        }

        TitledPane pane = new TitledPane(leg.intermediateStops().size() + " arrêts, " +
                                         FormatterFr.formatDuration(leg.duration()), grid);
        return new Accordion(pane);
    }

    /**
     * Ouvre le navigateur pour afficher la carte du voyage via un site web utilisant GeoJSON.
     *
     * @param journey Le voyage à afficher sur la carte.
     */
    private static void openMap(Journey journey) {
        try {
            URI uri = new URI("https", "umap.osm.ch", "/fr/map",
                    "data=" + JourneyGeoJsonConverter.toGeoJson(journey), null);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException e) {
            System.out.println("Erreur lors de l'ouverture de la carte : " + e.getMessage());
        }
    }

    /**
     * Ouvre une boîte de dialogue pour exporter le voyage au format iCalendar (.ics).
     *
     * @param journey      Le voyage à exporter.
     * @param parentButton Le bouton parent pour positionner la boîte de dialogue.
     */
    private static void exportJourneyAsCalendar(Journey journey, Button parentButton) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter le voyage au format iCalendar");

        String name = "voyage_" + journey.depTime().toLocalDate() + ".ics";
        chooser.setInitialFileName(name);

        File file = chooser.showSaveDialog(parentButton.getScene().getWindow());
        if (file != null) {
            try {
                Files.writeString(file.toPath(), JourneyIcalConverter.toIcalendar(journey));
            } catch (IOException e) {
                System.out.println("Erreur d'exportation iCalendar : " + e.getMessage());
            }
        }
    }
}
