package ch.epfl.rechor.timetable;

/**
 * Des noms alternatifs de gares
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public interface StationAliases extends Indexed {

    /**
     * Retourne le nom alternatif d'index donné (p. ex. "Losanna")
     *
     * @param id : l'index de la gare dans la table des gares
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return le nom alternatif d'index donné
     */
    String alias(int id);

    /**
     * Retourne le nom de la gare à laquelle correspond le nom alternatif d'index donné (p. ex. "Lausanne")
     *
     * @param id : l'index de la gare dans la table des gares
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return le nom de la gare à laquelle correspond le nom alternatif d'index donné
     */
    String stationName(int id);
}
