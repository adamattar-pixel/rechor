package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * Un horaire de transport public avec ses différentes entités indexées
 *
 * @autor AMRAOUI Saad
 * @autor ATTAR Adam
 */
public interface TimeTable {

    /**
     * Retourne les gares indexées de l'horaire
     *
     * @return les gares indexées
     */
    Stations stations();

    /**
     * Retourne les noms alternatifs indexés des gares de l'horaire
     *
     * @return les alias des gares
     */
    StationAliases stationAliases();

    /**
     * Retourne les voies/quais indexés de l'horaire
     *
     * @return les quais indexés
     */
    Platforms platforms();

    /**
     * Retourne les lignes indexées de l'horaire
     *
     * @return les lignes indexées
     */
    Routes routes();

    /**
     * Retourne les changements indexés de l'horaire
     *
     * @return les changements indexés
     */
    Transfers transfers();

    /**
     * Retourne les courses indexées de l'horaire actives le jour donné
     *
     * @param date : la date concernée
     * @return les courses actives ce jour-là
     */
    Trips tripsFor(LocalDate date);

    /**
     * Retourne les liaisons indexées de l'horaire actives le jour donné
     *
     * @param date : la date concernée
     * @return les liaisons actives ce jour-là
     */
    Connections connectionsFor(LocalDate date);

    /**
     * Retourne vrai si et seulement si l'index d'arrêt donné est un index de gare
     *
     * @param stopId : l'index de l'arrêt
     * @return vrai si l'arrêt est une gare, faux sinon
     */
    default boolean isStationId(int stopId) {
        return stopId < stations().size() && stopId >= 0;
    }

    /**
     * Retourne vrai si et seulement si l'index d'arrêt donné est un index de voie ou de quai
     *
     * @param stopId : l'index de l'arrêt
     * @return vrai si l'arrêt est une voie ou un quai, faux sinon
     */
    default boolean isPlatformId(int stopId) {
        return !isStationId(stopId) && stopId < (platforms().size() + stations().size()) && stopId >= 0;
    }

    /**
     * Retourne l'index de la gare de l'arrêt d'index donné
     *
     * @param stopId : l'index de l'arrêt
     * @return l'index de la gare correspondante
     */
    default int stationId(int stopId) {
        if (isStationId(stopId)) {
            return stopId;
        }
        return platforms().stationId(stopId - stations().size());
    }

    /**
     * Retourne le nom de la voie ou du quai de l'arrêt donné, ou null si l'arrêt est une gare
     *
     * @param stopId : l'index de l'arrêt
     * @return le nom de la voie ou du quai, ou null
     */
    default String platformName(int stopId) {
        if (isStationId(stopId)) {
            return null;
        }
        return platforms().name(stopId - stations().size());
    }
}
