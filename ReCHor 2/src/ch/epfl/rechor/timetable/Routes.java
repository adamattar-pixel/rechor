package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 * Des lignes de transport public indexées
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public interface Routes extends Indexed {

    /**
     * Retourne le type de véhicule desservant la ligne d'index donné
     *
     * @param id : l'index de la ligne
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return le type de véhicule desservant la ligne d'index donné
     */
    Vehicle vehicle(int id);

    /**
     * Retourne le nom de la ligne d'index donné (p. ex. IR 15)
     *
     * @param id : l'index de la ligne
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return le nom de la ligne d'index donné
     */
    String name(int id);
}
