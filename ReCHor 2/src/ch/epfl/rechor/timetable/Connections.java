package ch.epfl.rechor.timetable;

/**
 * Des liaisons indexées
 *
 * @autor AMRAOUI Saad
 * @autor ATTAR Adam
 */
public interface Connections extends Indexed {

    /**
     * Retourne l'index de l'arrêt de départ de la liaison d'index donné
     *
     * @param id : l'index de la liaison
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'index de l'arrêt de départ
     */
    int depStopId(int id);

    /**
     * Retourne l'heure de départ de la liaison d'index donné, exprimée en minutes après minuit
     *
     * @param id : l'index de la liaison
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'heure de départ en minutes après minuit
     */
    int depMins(int id);

    /**
     * Retourne l'index de l'arrêt d'arrivée de la liaison d'index donné
     *
     * @param id : l'index de la liaison
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'index de l'arrêt d'arrivée
     */
    int arrStopId(int id);

    /**
     * Retourne l'heure d'arrivée de la liaison d'index donné, exprimée en minutes après minuit
     *
     * @param id : l'index de la liaison
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'heure d'arrivée en minutes après minuit
     */
    int arrMins(int id);

    /**
     * Retourne l'index de la course à laquelle appartient la liaison d'index donné
     *
     * @param id : l'index de la liaison
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'index de la course correspondante
     */
    int tripId(int id);

    /**
     * Retourne la position de la liaison d'index donné dans la course à laquelle elle appartient
     * La première liaison d'une course à l'index 0.
     *
     * @param id : l'index de la liaison
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return la position de la liaison dans sa course
     */
    int tripPos(int id);

    /**
     * Retourne l'index de la liaison suivante dans la course, ou l'index de la première liaison de la course si la liaison actuelle est la dernière
     *
     * @param id : l'index de la liaison
     * @throws IndexOutOfBoundsException si l'index passé est inférieur à 0 ou supérieur à la taille retournée par size()
     * @return l'index de la liaison suivante
     */
    int nextConnectionId(int id);
}

