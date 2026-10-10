package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représente une implémentation des trajets basée sur un tampon structuré.
 * Cette classe utilise un StructuredBuffer pour accéder aux données des trajets
 * et une table de chaînes pour stocker les noms associés aux trajets.
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class BufferedRoutes implements Routes {
    private static final int NAME_ID = 0;
    private static final int KIND = 1;
    private final StructuredBuffer routes;
    private final List<String> stringTable;

    /**
     * Construit une instance de BufferedRoutes à partir d'une table de chaînes et d'un ByteBuffer.
     *
     * @param stringTable : la table contenant les noms des trajets
     * @param buffer : le tampon de données contenant les informations structurées sur les trajets
     */
    public BufferedRoutes(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        Structure structure = new Structure(
                field(NAME_ID, Structure.FieldType.U16),
                field(KIND, Structure.FieldType.U8)
        );
        routes = new StructuredBuffer(structure, buffer);
    }

    /**
     * Retourne le véhicule associé au trajet pour l'élément spécifié.
     *
     * @param id : l'identifiant du trajet
     * @return le véhicule correspondant, extrait de la liste immuable des véhicules
     */
    @Override
    public Vehicle vehicle(int id) {
        return Vehicle.ALL.get(routes.getU8(KIND, id));
    }

    /**
     * Retourne le nom du trajet pour l'élément spécifié.
     *
     * @param id : l'identifiant du trajet
     * @return le nom du trajet
     */
    @Override
    public String name(int id) {
        return stringTable.get(routes.getU16(NAME_ID, id));
    }

    /**
     * Retourne le nombre total de trajets stockés.
     *
     * @return le nombre d'éléments dans le tampon des trajets
     */
    @Override
    public int size() {
        return routes.size();
    }
}
