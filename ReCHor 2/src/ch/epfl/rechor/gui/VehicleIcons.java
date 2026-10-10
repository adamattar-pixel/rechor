package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Vehicle;
import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.Map;

/**
 * Classe fournissant un accès aux images (icônes) représentant les différents types de véhicules.
 *
 * Cette classe garantit que chaque image est chargée une seule fois en mémoire puis mise en cache
 *
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public class VehicleIcons {

    //Cache statique associant chaque type de véhicule à son icône image.
    private static Map<Vehicle, Image> myMap = new EnumMap<>(Vehicle.class);

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private VehicleIcons() {
    }

    /**
     * Retourne l'icône (image JavaFX) correspondant au véhicule donné.
     *
     * Il est garanti que pour un même type de véhicule,
     * la même instance d'image est retournée à chaque appel.
     *
     * @param v le type de véhicule dont on veut l'icône
     * @return l'image JavaFX correspondant à ce véhicule
     */
    public static Image iconFor(Vehicle v) {
        myMap.computeIfAbsent(v, k -> new Image(k.toString() + ".png"));
        return myMap.get(v);
    }
}
