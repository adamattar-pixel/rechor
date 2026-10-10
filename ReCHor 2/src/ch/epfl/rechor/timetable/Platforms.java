package ch.epfl.rechor.timetable;

/**
 * Des voies/quais indexés
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public interface Platforms extends Indexed {

    /**
     * Retourne le nom de la voie (p. ex. 70) ou du quai (p. ex. A), qui peut être vide
     *
     * @param id : l'index de la voie/quai
     * @return le nom de la voie/quai
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     */
    String name(int id);

    /**
     * Retourne l'index de la gare à laquelle cette voie ou ce quai appartient
     *
     * @param id : l'index de la voie/quai
     * @return l'index de la gare à laquelle cette voie ou ce quai appartient
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     */
    int stationId(int id);
}
