package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;

import java.util.*;

/**
 * Classe non instanciable permettant de convertir une Journey
 * en une représentation minimale GeoJSON de son tracé.
 *
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public class JourneyGeoJsonConverter {
    private final static double ROUND_TO_FIVE = Math.pow(10, 5);

    /**
     * Convertit un Journey en un document GeoJSON au format String.
     *
     * Le tracé inclut tous les arrêts du voyage, avec des coordonnées arrondies
     * à 5 décimales et sans répétition consécutive de points identiques.
     *
     * @param journey le voyage à convertir
     * @return une chaîne JSON minimale représentant le tracé du voyage
     */
    public static String toGeoJson(Journey journey) {
        List<Json> coords = new ArrayList<>();
        Map<Double, Double> oldPos = new HashMap<>();

        // Ajout des arrêts de départ et intermédiaires pour chaque étape
        journey.legs().forEach(leg -> {
            addToJson(leg.depStop(), coords, oldPos);
            leg.intermediateStops().forEach(intStop -> addToJson(intStop.stop(), coords, oldPos));
        });

        // Ajout de l’arrêt final
        addToJson(journey.arrStop(), coords, oldPos);

        // Construction de l’objet JSON final
        Map<Json, String> properties = new LinkedHashMap<>();
        properties.put(new Json.JString("type"), new Json.JString("LineString").toString());
        properties.put(new Json.JString("coordinates"), new Json.JArray(coords).toString());
        Json typeName = new Json.JObject(properties);

        return typeName.toString();
    }

    /**
     * Ajoute les coordonnées de l’arrêt donné à la liste JSON, en évitant les doublons consécutifs.
     *
     * @param stop   l’arrêt à ajouter
     * @param coords la liste accumulée des coordonnées GeoJSON
     * @param oldPos la dernière position ajoutée, pour comparaison
     */
    private static void addToJson(Stop stop, List<Json> coords, Map<Double, Double> oldPos) {
        double lat = stop.latitude();
        double lon = stop.longitude();

        // Vérifie que la position actuelle est différente de la précédente
        Map<Double, Double> newPos = Map.of(lon, lat);
        if (oldPos.equals(newPos)) {
            return;
        }
        oldPos.clear();
        oldPos.putAll(newPos);

        // Ajoute les coordonnées arrondies
        List<Json> coordinate = new ArrayList<>();
        coordinate.add(new Json.JNumber(roundToFiveDecimals(lon)));
        coordinate.add(new Json.JNumber(roundToFiveDecimals(lat)));
        coords.add(new Json.JArray(coordinate));
    }

    /**
     * Arrondit une valeur réelle à 5 décimales.
     *
     * @param value la valeur à arrondir
     * @return la valeur arrondie à 5 décimales
     */
    private static double roundToFiveDecimals(double value) {
        return Math.round(value * ROUND_TO_FIVE) / ROUND_TO_FIVE;
    }

}
