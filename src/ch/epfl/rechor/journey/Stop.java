package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import static java.util.Objects.requireNonNull;

/**
 * Un arrêt de transport public
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public record Stop(String name, String platformName, double longitude, double latitude) {

    /**
     * Construit un arrêt à l'aide de ses différents paramètres : nom de la gare, nom de la voie/quai, longitude et latitude de la position d'arrêt
     *
     * @param name         : le nom de l'arrêt, ou le nom de la gare à laquelle appartient l'arrêt s'il représente une voie ou un quai
     * @param platformName : le nom de la voie ou du quai de l'arrêt, ou null si l'arrêt correspond à une gare
     * @param longitude    : la longitude de la position de l'arrêt, en degrés
     * @param latitude     : la latitude de la position de l'arrêt, en degrés
     */
    public Stop {
        requireNonNull(name);
        Preconditions.checkArgument((Math.abs(longitude) <= 180)
                && (Math.abs(latitude) <= 90));
    }
}
