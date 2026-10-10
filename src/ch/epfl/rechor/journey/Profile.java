package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.Trips;
import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un profil de recherche de trajet basé sur un horaire donné, une date spécifique
 * et une station d'arrivée.
 *
 * @param timeTable    l'horaire utilisé pour construire le profil
 * @param date         la date du trajet
 * @param arrStationId l'identifiant de la station d'arrivée
 * @param stationFront la liste des fronts de Pareto associés aux stations
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */

public record Profile(TimeTable timeTable, LocalDate date, int arrStationId,
                      List<ParetoFront> stationFront) {

    /**
     * Construit un profil de recherche de trajet.
     *
     * @param timeTable    l'horaire utilisé pour le calcul
     * @param date         la date considérée pour le trajet
     * @param arrStationId l'identifiant de la station d'arrivée
     * @param stationFront la liste des fronts de Pareto pour chaque station
     */
    public Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {
        this.timeTable = timeTable;
        this.date = date;
        this.arrStationId = arrStationId;
        this.stationFront = List.copyOf(stationFront);
    }

    /**
     * Retourne les connexions disponibles pour la date donnée.
     *
     * @return les connexions de l'horaire pour la date spécifiée
     */
    public Connections connections() {
        return timeTable().connectionsFor(date());
    }

    /**
     * Retourne les trajets disponibles pour la date donnée.
     *
     * @return les trajets de l'horaire pour la date spécifiée
     */
    public Trips trips() {
        return timeTable().tripsFor(date());
    }

    /**
     * Retourne le front de Pareto associé à une station donnée.
     *
     * @param stationId l'identifiant de la station
     * @return le front de Pareto correspondant à la station
     */
    public ParetoFront forStation(int stationId) {
        return stationFront().get(stationId);
    }

    /**
     * Constructeur pour construire progressivement un profil.
     */
    public final static class Builder {
        private TimeTable timeTable;
        private LocalDate date;
        private int arrStationId;
        private ParetoFront.Builder[] stationFrontBuilder;
        private ParetoFront.Builder[] tripsFrontBuilder;

        /**
         * Initialise un constructeur de profil avec les paramètres de base.
         *
         * @param timeTable    l'horaire utilisé
         * @param date         la date considérée
         * @param arrStationId l'identifiant de la station d'arrivée
         */
        public Builder(TimeTable timeTable, LocalDate date, int arrStationId) {
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;
            this.stationFrontBuilder = new ParetoFront.Builder[timeTable.stations().size()];
            this.tripsFrontBuilder = new ParetoFront.Builder[timeTable.tripsFor(date).size()];
        }

        /**
         * Retourne le constructeur de front de Pareto pour une station donnée.
         *
         * @param stationId l'identifiant de la station
         * @return le constructeur de front de Pareto de la station
         */
        public ParetoFront.Builder forStation(int stationId) {
            return stationFrontBuilder[stationId];
        }

        /**
         * Définit le constructeur de front de Pareto pour une station donnée.
         *
         * @param stationId l'identifiant de la station
         * @param builder   le constructeur du front de Pareto à associer
         */
        public void setForStation(int stationId, ParetoFront.Builder builder) {
            stationFrontBuilder[stationId] = builder;
        }

        /**
         * Retourne le constructeur de front de Pareto pour un trajet donné.
         *
         * @param tripId l'identifiant du trajet
         * @return le constructeur de front de Pareto du trajet
         */
        public ParetoFront.Builder forTrip(int tripId) {
            return tripsFrontBuilder[tripId];
        }

        /**
         * Définit le constructeur de front de Pareto pour un trajet donné.
         *
         * @param tripId  l'identifiant du trajet
         * @param builder le constructeur du front de Pareto à associer
         */
        public void setForTrip(int tripId, ParetoFront.Builder builder) {
            tripsFrontBuilder[tripId] = builder;
        }

        /**
         * Construit un profil à partir des données accumulées.
         *
         * @return une instance de Profile
         */
        public Profile build() {
            List<ParetoFront> stationFrontResult = new ArrayList<>();
            for (int i = 0; i < stationFrontBuilder.length; i++) {
                if (stationFrontBuilder[i] != null) {
                    stationFrontResult.add(stationFrontBuilder[i].build());
                } else {
                    stationFrontResult.add(ParetoFront.EMPTY);
                }
            }
            return new Profile(timeTable, date, arrStationId, stationFrontResult);
        }
    }
}