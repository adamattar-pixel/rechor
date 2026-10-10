package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représente une implémentation des voyages (trips) basée sur un tampon structuré.
 * Cette classe utilise un StructuredBuffer pour accéder aux données des voyages
 * et une table de chaînes pour stocker les noms associés aux destinations.
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class BufferedTrips implements Trips {
    private static final int ROUTE_ID = 0;
    private static final int DESTINATION_ID = 1;
    private final StructuredBuffer trips;
    private final List<String> stringTable;

    /**
     * Construit une instance de BufferedTrips à partir d'une table de chaînes et d'un ByteBuffer.
     *
     * @param stringTable : la table contenant les chaînes utilisées pour les destinations
     * @param buffer : le tampon de données contenant les informations structurées sur les voyages
     */
    public BufferedTrips(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        Structure structure = new Structure(
                field(ROUTE_ID, Structure.FieldType.U16),
                field(DESTINATION_ID, Structure.FieldType.U16)
        );
        trips = new StructuredBuffer(structure, buffer);
    }

    /**
     * Retourne l'identifiant de la route pour le voyage spécifié.
     *
     * @param id : l'identifiant du voyage
     * @return l'identifiant de la route associée
     */
    @Override
    public int routeId(int id) {
        return trips.getU16(ROUTE_ID, id);
    }

    /**
     * Retourne la destination du voyage spécifié.
     *
     * @param id : l'identifiant du voyage
     * @return la destination sous forme de chaîne de caractères
     */
    @Override
    public String destination(int id) {
        return stringTable.get(trips.getU16(DESTINATION_ID, id));
    }

    /**
     * Retourne le nombre total de voyages stockés.
     *
     * @return le nombre d'éléments dans le tampon des voyages
     */
    @Override
    public int size() {
        return trips.size();
    }
}
