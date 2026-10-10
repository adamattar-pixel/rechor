package ch.epfl.rechor.timetable;

/**
 * Des courses de transport public indexées
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public interface Trips extends Indexed {

    /**
     * Retourne l'index de la ligne à laquelle la course d'index donné appartient
     *
     * @param id : l'index de la course
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'index de la ligne à laquelle la course d'index donné appartient
     */
    int routeId(int id);

    /**
     * Retourne le nom de la destination finale de la course
     *
     * @param id : l'index de la course
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return le nom de la destination finale de la course
     */
    String destination(int id);
}
