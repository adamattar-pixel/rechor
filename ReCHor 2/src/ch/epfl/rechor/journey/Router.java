package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.*;

import static ch.epfl.rechor.PackedRange.startInclusive;
import static ch.epfl.rechor.PackedRange.endExclusive;

import java.time.LocalDate;
import java.util.Arrays;

import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 * Enregistrement public et immuable représentant un routeur de voyages.
 * Elle met en œuvre l'algorithme CSA (Connection Scan Algorithm), optimisé pour un grand nombre de liaisons.
 *
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public record Router(TimeTable timeTable) {

    /**
     * Calcule le profil de tous les voyages optimaux pour se rendre à la gare d'arrivée spécifiée, à la date donnée.
     *
     * @param date         la date pour laquelle les profils de voyage sont calculés
     * @param arrStationId l'identifiant de la gare d'arrivée
     * @return un Profile contenant pour chaque gare le profil des trajets optimaux vers la destination
     */
    public Profile profile(LocalDate date, int arrStationId) {
        // Création du profil de résultats pour le jour et la destination donnés
        Profile.Builder profile = new Profile.Builder(timeTable, date, arrStationId);
        Transfers transfers = timeTable.transfers();
        Connections connections = timeTable.connectionsFor(date);
        Stations stations = timeTable.stations();

        // Initialisation du tableau des temps de marche vers la gare d'arrivée
        int[] tripDurations = new int[stations.size()];
        Arrays.fill(tripDurations, -1);

        // Remplissage du tableau des durées de marche vers la gare d’arrivée
        int toArrStation = transfers.arrivingAt(arrStationId);
        for (int i = startInclusive(toArrStation); i < endExclusive(toArrStation); i++) {
            tripDurations[transfers.depStationId(i)] = transfers.minutes(i);
        }

        // Parcours de toutes les liaisons de la journée (triées par ordre décroissant d’heure de départ)
        for (int l = 0; l < connections.size(); l++) {
            final int finalL = l; // Nécessaire pour les lambdas

            int tripId = connections.tripId(finalL);
            int arrStopId = connections.arrStopId(finalL);
            int arrMins = connections.arrMins(finalL);
            int depStopId = connections.depStopId(finalL);
            int depMins = connections.depMins(finalL);
            int stationId = timeTable.stationId(arrStopId);

            // Création d'une frontière pareto pour cette liaison
            ParetoFront.Builder frontier = new ParetoFront.Builder();

            // Option 1 : marcher depuis l'arrêt d'arrivée
            if (tripDurations[stationId] != -1) {
                frontier.add(arrMins + tripDurations[stationId], 0, finalL);
            }

            // Option 2 : continuer avec la même liaison
            ParetoFront.Builder tripFrontier = profile.forTrip(tripId);
            if (tripFrontier != null) {
                frontier.addAll(tripFrontier);
            }

            // Option 3 : changement de véhicule
            ParetoFront.Builder stationFrontier = profile.forStation(stationId);
            if (stationFrontier != null) {
                stationFrontier.forEach(tuple -> {
                    if (depMins(tuple) >= arrMins) {
                        frontier.add(arrMins(tuple), changes(tuple) + 1, finalL);
                    }
                });
            }

            if (frontier.isEmpty()) continue;

            // Mise à jour de la frontière de la liaison
            if (tripFrontier == null) {
                profile.setForTrip(tripId, new ParetoFront.Builder(frontier));
            } else {
                tripFrontier.addAll(frontier);
            }

            // Mise à jour des frontières des gares
            int depStationId = timeTable.stationId(depStopId);
            var depStationFrontier = profile.forStation(depStationId);

            // Optimisation : si la gare domine déjà cette frontière, on ignore
            if (depStationFrontier != null && depStationFrontier.fullyDominates(frontier, depMins))
                continue;

            int transferInterval = transfers.arrivingAt(depStationId);
            for (int k = startInclusive(transferInterval); k < endExclusive(transferInterval);
                 k++) {
                int d = depMins - transfers.minutes(k);
                int fromStationId = transfers.depStationId(k);

                // Ajout des nouveaux tuples à la frontière de la gare de départ (transfert)
                frontier.forEach(tuple -> {
                    ParetoFront.Builder fromFrontier = profile.forStation(fromStationId);
                    long packed = withDepMins(pack(arrMins(tuple), changes(tuple),
                            Bits32_24_8.pack(finalL, connections.tripPos(payload(tuple)) -
                                                     connections.tripPos(finalL))), d);

                    if (fromFrontier != null) {
                        fromFrontier.add(packed);
                    } else {
                        ParetoFront.Builder empty = new ParetoFront.Builder();
                        empty.add(packed);
                        profile.setForStation(fromStationId, empty);
                    }
                });
            }
        }
        return profile.build();
    }
}

