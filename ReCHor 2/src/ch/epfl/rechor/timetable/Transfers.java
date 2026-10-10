package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * Des changements indexés
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public interface Transfers extends Indexed  {

    /**
     * Retourne l'index de la gare de départ du changement d'index donné
     *
     * @param id : l'index du changement
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'index de la gare de départ du changement d'index donné
     */
    int depStationId(int id);

    /**
     * Retourne la durée, en minutes, du changement d'index donné
     *
     * @param id : l'index du changement
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return la durée du changement d'index donné
     */
    int minutes(int id);

    /**
     * Retourne l'intervalle empaqueté des index des changements dont la gare d'arrivée est celle d'index donné
     *
     * @param stationId : l'index de la gare d'arrivée
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'intervalle empaqueté des index des changements de la gare d'arrivée
     */
    int arrivingAt(int stationId);

    /**
     * Retourne la durée, en minutes, du changement entre les deux gares d'index donnés
     *
     * @param depStationId : l'index de la gare de départ
     * @param arrStationId : l'index de la gare d'arrivée
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @throws NoSuchElementException si aucun changement n'est possible entre ces deux gares
     * @return la durée du changement entre les deux gares d'index donnés
     */
    int minutesBetween(int depStationId, int arrStationId);
}
