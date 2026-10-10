package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * Cette classe met en cache les objets Connections et Trips associés à une date,
 * afin d'éviter de les recharger plusieurs fois lorsqu'ils sont demandés de manière répétée pour la même date.
 *
 * Toutes les autres méthodes sont déléguées à l'horaire sous-jacent.
 *
 * @author ATTAR ADAM
 * @author AMRAOUI SAAD
 */
public class CachedTimeTable implements TimeTable {
    private Connections connections;
    private Trips trips;
    private LocalDate dateConnections;
    private LocalDate dateTrips;
    private TimeTable timeTable;

    /**
     * Construit un CachedTimeTable à partir d'un horaire sous-jacent.
     *
     * @param timeTableInput l'horaire sous-jacent à utiliser pour déléguer les appels
     */
    public CachedTimeTable(TimeTable timeTableInput) {
        timeTable = timeTableInput;
    }

    /**
     * Retourne les stations de l'horaire, déléguées à l'horaire sous-jacent.
     *
     * @return les stations connues de l'horaire
     */
    @Override
    public Stations stations() {
        return timeTable.stations();
    }

    /**
     * Retourne les alias de stations, délégués à l'horaire sous-jacent.
     *
     * @return les alias de stations
     */
    @Override
    public StationAliases stationAliases() {
        return timeTable.stationAliases();
    }

    /**
     * Retourne les quais de l'horaire, délégués à l'horaire sous-jacent.
     *
     * @return les quais
     */
    @Override
    public Platforms platforms() {
        return timeTable.platforms();
    }

    /**
     * Retourne les lignes ou itinéraires de transport, délégués à l'horaire sous-jacent.
     *
     * @return les lignes de transport
     */
    @Override
    public Routes routes() {
        return timeTable.routes();
    }

    /**
     * Retourne les correspondances entre quais ou lignes, déléguées à l'horaire sous-jacent.
     *
     * @return les correspondances
     */
    @Override
    public Transfers transfers() {
        return timeTable.transfers();
    }

    /**
     * Retourne les courses disponibles pour une date donnée.
     * Si la date est différente de celle du cache, recharge les données depuis l'horaire sous-jacent
     * et met à jour le cache. Sinon, retourne simplement les données mises en cache.
     *
     * @param date la date pour laquelle les courses sont demandées
     * @return les courses pour cette date
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        if (!date.equals(dateTrips)) {
            dateTrips = date;
            trips = timeTable.tripsFor(date);
        }
        return trips;
    }

    /**
     * Retourne les connexions (liaisons) disponibles pour une date donnée.
     * Si la date est différente de celle du cache, recharge les données depuis l'horaire sous-jacent
     * et met à jour le cache. Sinon, retourne simplement les données mises en cache.
     *
     * @param date la date pour laquelle les connexions sont demandées
     * @return les connexions pour cette date
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        if (!date.equals(dateConnections)) {
            dateConnections = date;
            connections = timeTable.connectionsFor(date);
        }
        return connections;
    }
}
