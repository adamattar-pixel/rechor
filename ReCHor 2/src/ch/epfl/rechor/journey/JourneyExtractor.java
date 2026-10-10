package ch.epfl.rechor.journey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import ch.epfl.rechor.timetable.*;

import static ch.epfl.rechor.Bits32_24_8.unpack24;
import static ch.epfl.rechor.Bits32_24_8.unpack8;
import static ch.epfl.rechor.journey.Journey.Leg;
import static ch.epfl.rechor.journey.Journey.Leg.Transport;
import static ch.epfl.rechor.journey.Journey.Leg.Foot;
import static ch.epfl.rechor.journey.Journey.Leg.IntermediateStop;
import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 * Classe utilitaire pour extraire des trajets (Journeys) basés sur un profil donné.
 * Cette classe ne peut pas être instanciée directement car elle est conçue pour
 * fournir des méthodes statiques d'extraction de trajets.
 */
public class JourneyExtractor {

    /**
     * Constructeur privé pour empêcher l'instanciation directe de cette classe.
     */
    private JourneyExtractor() {
    }

    /**
     * Extrait et retourne une liste de trajets possibles à partir d'un profil donné
     * et d'une station de départ. Les trajets sont triés par heure de départ, puis
     * par heure d'arrivée.
     *
     * @param profile      le profil de recherche contenant les informations nécessaires
     *                     pour extraire les trajets, y compris l'horaire, la date,
     *                     et les fronts de Pareto pour les stations.
     * @param depStationId l'identifiant de la station de départ à partir de laquelle
     *                     les trajets doivent être calculés.
     * @return une liste de trajets possibles triés par heure de départ puis par
     * heure d'arrivée.
     */
    public static List<Journey> journeys(Profile profile, int depStationId) {
        ParetoFront pareto = profile.forStation(depStationId);
        List<Journey> journeys = new ArrayList<>();
        TimeTable timeTable = profile.timeTable();
        LocalDateTime startOfTheDay = profile.date().atTime(0, 0);


        //itère sur chaque tuple du ParetoFront
        pareto.forEach((long criteria) -> {
            //Initialise les Stations, Connections, Transfers, et paramètres du critère de départ
            long currentCriteria = criteria;
            int currentConnectionId = unpack24(payload(currentCriteria));
            int intermediateStopsNum = unpack8(payload(currentCriteria));
            Stations stations = timeTable.stations();
            Connections connections = profile.connections();
            Transfers transfers = timeTable.transfers();
            LocalDateTime currentConnectionDepTime = startOfTheDay.plusMinutes(depMins(currentCriteria));
            ;
            int changes = changes(currentCriteria);
            int depStationLegId;
            int arrStationLegId = depStationId;

            //rajoute une étape à pied si la station de départ de la première connection n'est pas la même que celle de départ
            boolean isFootLeg = false;
            if (timeTable.stationId(connections.depStopId(currentConnectionId)) != depStationId) {
                isFootLeg = true;
            }

            //initialise la liste de leg à donner en paramètre à chaque Journey créé
            List<Leg> legs = new ArrayList<>();
            while (changes >= 0) {
                Leg leg;
                //Créé une étape en transport
                if (!isFootLeg) {
                    //Décrémente les changes si et seulement si on passe par une étape en Transport
                    changes--;

                    //Créé la liste d'Intermediate Stops
                    List<IntermediateStop> intermediateStops = new ArrayList<>();
                    int arrStopId = currentConnectionId;
                    for (int i = 0; i < intermediateStopsNum; i++) {
                        LocalDateTime arrTime = startOfTheDay.plusMinutes(connections.arrMins(arrStopId));
                        arrStopId = connections.nextConnectionId(arrStopId);
                        depStationLegId = timeTable.stationId(connections.depStopId(arrStopId));
                        Stop depStop = createStop(stations, timeTable, depStationLegId, connections.depStopId(arrStopId));
                        LocalDateTime depTime = startOfTheDay.plusMinutes(connections.depMins(arrStopId));
                        intermediateStops.add(new IntermediateStop(depStop, arrTime, depTime));
                    }

                    //Appelle la méthode createFullLeg qui créé une étape en transport grace a la présence d'arrêts intermédiaires dans les parametres.
                    depStationLegId = timeTable.stationId(connections.depStopId(currentConnectionId));
                    arrStationLegId = timeTable.stationId(connections.arrStopId(arrStopId));
                    LocalDateTime depTime = startOfTheDay.plusMinutes(connections.depMins(currentConnectionId));
                    leg = createFullLeg(depStationLegId, arrStationLegId, profile, timeTable
                            , connections, stations, transfers, currentConnectionId
                            , depTime, intermediateStops, arrStopId);
                    currentConnectionDepTime = leg.arrTime();

                    //Condition nécessaire afin d'éviter de créer un critère avec un nombre de changes négatif (l.74)
                    if (changes >= 0) {
                        currentCriteria = profile.forStation(arrStationLegId)
                                .get(arrMins(currentCriteria), changes);
                        currentConnectionId = unpack24(payload(currentCriteria));
                        intermediateStopsNum = unpack8(payload(currentCriteria));
                    }
                    isFootLeg = true;
                } else {
                    //créé une étape à pied en appellant la méthode createFullLeg qui créé une étape à pied a l'absence de présence d'arrêts intermédiaires dans les parametres.
                    depStationLegId = arrStationLegId;
                    arrStationLegId = timeTable.stationId(connections.depStopId(currentConnectionId));
                    leg = createFullLeg(depStationLegId, arrStationLegId, profile, timeTable
                            , connections, stations, transfers, currentConnectionId
                            , currentConnectionDepTime, null, 0);
                    isFootLeg = false;
                }
                //Ajoute le leg nouvellement créé
                legs.add(leg);
                //condition nécessaire afin de créer une étape à pied si on arrive pas à la station d'arrivée souhaitée
                if (changes == -1 && profile.arrStationId() != arrStationLegId) {
                    leg = createFullLeg(arrStationLegId, profile.arrStationId(), profile, timeTable
                            , connections, stations, transfers, currentConnectionId
                            , currentConnectionDepTime, null, 0);
                    legs.add(leg);
                }
            }
            journeys.add(new Journey(legs));
        });

        // Tri des trajets par heure de départ, puis par heure d'arrivée
        journeys.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));
        return journeys;
    }

    /**
     * Crée une étape complète (Leg) d'un trajet, soit à pied (Foot), soit en transport (Transport),
     * en fonction de la présence ou non d'arrêts intermédiaires.
     *
     * @param depStationLegId   identifiant de la station de départ de l'étape
     * @param arrStationLegId   identifiant de la station d'arrivée de l'étape
     * @param profile           profil de recherche contenant les données de l'horaire
     * @param connections       ensemble des connexions disponibles
     * @param stations          ensemble des stations avec leurs coordonnées et noms
     * @param transfers         structure contenant les durées de transfert à pied entre stations
     * @param connectionId      identifiant de la connexion utilisée pour cette étape
     * @param depTime           heure de départ de l'étape
     * @param intermediateStops liste des arrêts intermédiaires si c’est un trajet en transport (null pour un trajet à pied)
     * @param arrStopId         identifiant de l'arrêt d'arrivée pour un trajet en transport
     * @return un objet Leg représentant soit une étape à pied Leg.Foot,
     * soit une étape en transport Leg.Transport
     */
    private static Leg createFullLeg(int depStationLegId, int arrStationLegId, Profile profile,
                                     TimeTable timeTable, Connections connections, Stations stations, Transfers transfers
            , int connectionId, LocalDateTime depTime, List<IntermediateStop> intermediateStops
            , int arrStopId) {
        Stop depStop = createStop(stations, timeTable,
                depStationLegId, connections.depStopId(connectionId));
        if (intermediateStops == null) {
            Stop arrStop = createStop(stations, timeTable, arrStationLegId,
                    connections.arrStopId(connectionId));
            LocalDateTime arrTime = depTime.plusMinutes(transfers
                    .minutesBetween(depStationLegId, arrStationLegId));
            return new Foot(depStop, depTime, arrStop, arrTime);
        } else {
            LocalDateTime arrTime = profile.date().atStartOfDay()
                    .plusMinutes(connections.arrMins(arrStopId));
            Stop arrStop = createStop(stations, timeTable,
                    arrStationLegId, connections.arrStopId(arrStopId));
            Routes routes = timeTable.routes();
            Trips trips = timeTable.tripsFor(profile.date());
            Vehicle vehicle = routes.vehicle(trips.routeId(connections.tripId(connectionId)));
            String route = routes.name(trips.routeId(connections.tripId(connectionId)));
            String destination = trips.destination(connections.tripId(connectionId));
            return new Transport(depStop, depTime, arrStop, arrTime
                    , intermediateStops, vehicle, route, destination);
        }
    }

    /**
     * Crée et retourne un Stop à partir des informations fournies
     * par les interfaces Stations et TimeTable
     *
     * Cette méthode lit le nom de la gare, le nom éventuel de la voie/quai
     * (s'il existe), ainsi que la longitude et la latitude, pour construire
     * un nouvel objet Stop.
     *
     * @param stations   l'interface donnant accès aux données des gares
     * @param timetable  l'horaire fournissant, entre autres, les noms de voies/quais
     * @param nameId     l'identifiant de la gare dont on veut récupérer
     *                   le nom et les coordonnées
     * @param platformId l'identifiant de l'arrêt (voie/quai) dont on veut
     *                   récupérer le nom de quai/voie
     * @return un nouvel objet Stop représentatif de la gare
     * ou de la voie/quai associé
     */
    private static Stop createStop(Stations stations, TimeTable timetable, int nameId, int platformId) {
        String name = stations.name(nameId);
        String platformName = timetable.platformName(platformId);
        double longitude = stations.longitude(nameId);
        double latitude = stations.latitude(nameId);
        return new Stop(name, platformName, longitude, latitude);
    }
}
