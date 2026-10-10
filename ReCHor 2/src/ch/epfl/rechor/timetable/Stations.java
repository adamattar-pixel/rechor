package ch.epfl.rechor.timetable;

/**
 * Des gares indexées
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public interface Stations extends Indexed {

    /**
     * Retourne le nom de la gare d'index donné
     *
     * @param id : l'index de la gare dans la table des gares
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return le nom de la gare d'index donné
     */
    String name(int id);

    /**
     * Retourne la longitude, en degrés, de la gare d'index donné
     *
     * @param id : l'index de la gare dans la table des gares
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return la longitude de la gare d'index donné
     */
    double longitude(int id);

    /**
     * Retourne la latitude, en degrés, de la gare d'index donné
     *
     * @param id : l'index de la gare dans la table des gares
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return la latitude de la gare d'index donné
     */
    double latitude(int id);
}
