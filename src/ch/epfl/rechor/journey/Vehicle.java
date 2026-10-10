package ch.epfl.rechor.journey;

import java.util.List;

/**
 * Les différents types de transport utilisés
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public enum Vehicle {
    TRAM, METRO, TRAIN, BUS, FERRY, AERIAL_LIFT, FUNICULAR;

    /**
     * Liste immuable contenant la totalité des valeurs de véhicule possible, dans leur ordre de définition
     *
     * @return la liste contenant la totalité des valeurs du type énuméré
     */
    public static final List<Vehicle> ALL = List.of(values());
}
