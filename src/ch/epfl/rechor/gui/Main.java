package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.StationAliases;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public final class Main extends Application {
    private static final double DEFAULT_MIN_WIDTH = 800;
    private static final double DEFAULT_MIN_HEIGHT = 600;
    private Profile profile;
    private ObservableValue<List<Journey>> journeyObservable;
    private List<Journey> previousJourneys = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Point d'entrée principal de l'application JavaFX.
     *
     * Cette méthode initialise tous les composants de l'interface graphique et les connecte à la logique
     * de traitement des données horaires. Elle configure notamment l'interface de requête (`QueryUI`),
     * le résumé des trajets (`SummaryUI`) et les détails du voyage (`DetailUI`), puis les affiche dans
     * une fenêtre JavaFX avec une disposition en `BorderPane`.
     *
     * La logique de calcul des trajets repose sur un `Router` et un `Profile`, mis à jour
     * dynamiquement via des `ObservableValue` liés aux entrées utilisateur (départ, arrivée, date).
     *
     * Un `SplitPane` divise la fenêtre entre la vue synthétique des trajets à gauche et
     * les détails du voyage sélectionné à droite.
     *
     * @param primaryStage la fenêtre principale JavaFX dans laquelle l'interface est affichée
     * @throws IOException si la lecture de la timetable
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Lecture des données horaires depuis la timetable
        TimeTable timetable = new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));
        Stations stations = timetable.stations();
        StationAliases stationAliases = timetable.stationAliases();

        // Récupération des noms de stations et leurs alias sous forme de listes / map
        List<String> stationNames = stationList(stations);
        Map<String, String> stationAliasesNames = stationAliasesList(stationAliases);

        StopIndex stopIndex = new StopIndex(stationNames, stationAliasesNames);
        QueryUI queryUI = QueryUI.create(stopIndex);
        Router router = new Router(timetable);

        // Liaison aux valeurs observables des champs départ, arrivée, date
        ObservableValue<String> depStop = queryUI.depStop0();
        ObservableValue<String> arrStop = queryUI.arrStop0();
        ObservableValue<LocalDate> date = queryUI.date0();

        // Création du binding pour la liste des trajets
        journeyObservable = Bindings.createObjectBinding(() -> {
            int depId = stationNames.indexOf(depStop.getValue()); // id station départ
            int arrId = stationNames.indexOf(arrStop.getValue()); // id station arrivée

            // Si une des stations n'existe pas, on retourne l'ancienne liste (pas de changement)
            if (depId == -1 || arrId == -1) {
                return previousJourneys;
            }

            // Si le profil est null ou que l'arrivée/date ont changé, on recalcule le profil
            if (profile == null || arrId != profile.arrStationId() ||
                !date.getValue().equals(profile.date())) {
                profile = router.profile(date.getValue(), arrId);
            }

            // Extraction des trajets disponibles depuis depId selon le profil
            previousJourneys = JourneyExtractor.journeys(profile, depId);
            return previousJourneys;
        }, date, arrStop, depStop);

        SummaryUI summaryUI = SummaryUI.create(journeyObservable, queryUI.time0());
        DetailUI detailUI = DetailUI.create(summaryUI.selectStopIndexedJourneyO());

        // SplitPane divise la fenêtre en deux parties : summaryUI à gauche, detailUI à droite
        SplitPane mainView = new SplitPane(summaryUI.rootNode(), detailUI.rootNode());

        // BorderPane comme conteneur principal : en haut la requête, au centre le splitPane
        BorderPane root = new BorderPane();
        root.setTop(queryUI.rootNode());
        root.setCenter(mainView);

        // Création de la scène JavaFX
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(DEFAULT_MIN_WIDTH);
        primaryStage.setMinHeight(DEFAULT_MIN_HEIGHT);
        primaryStage.setTitle("ReCHor");

        Platform.runLater(() -> scene.lookup("#depStop").requestFocus());

        primaryStage.show();
    }

    // Méthode utilitaire qui récupère la liste des noms de stations
    private static List<String> stationList(Stations stations) {
        List<String> stationList = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            stationList.add(stations.name(i));
        }
        return stationList;
    }

    // Méthode utilitaire qui construit une map des alias -> noms de stations
    private static Map<String, String> stationAliasesList(StationAliases stationAliases) {
        Map<String, String> stationAliasesMap = new HashMap<>();
        for (int i = 0; i < stationAliases.size(); i++) {
            stationAliasesMap.put(stationAliases.alias(i), stationAliases.stationName(i));
        }
        return stationAliasesMap;
    }
}

