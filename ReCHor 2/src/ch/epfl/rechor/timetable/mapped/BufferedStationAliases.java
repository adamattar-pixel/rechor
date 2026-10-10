package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représente une implémentation des alias de stations basée sur un tableau structuré
 * Cette classe utilise un StructuredBuffer pour accéder aux données des alias
 * et une table de chaînes pour stocker les noms associés aux alias et aux noms de stations
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class BufferedStationAliases implements StationAliases {
    private static final int ALIAS_ID = 0;
    private static final int STATION_NAME_ID = 1;
    private final StructuredBuffer stationsAliases;
    private final List<String> stringTable;

    /**
     * Construit une instance de BufferedStationAliases à partir d'une table de chaînes et d'un ByteBuffer
     *
     * @param stringTable : la table contenant les alias et les noms des stations
     * @param buffer : le tableau de données contenant les informations structurées sur les alias
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        Structure structure = new Structure(
                field(ALIAS_ID, Structure.FieldType.U16),
                field(STATION_NAME_ID, Structure.FieldType.U16)
        );
        stationsAliases = new StructuredBuffer(structure, buffer);
    }

    /**
     * Retourne l'alias de la station correspondant à l'identifiant donné
     *
     * @param id : l'identifiant de l'alias
     * @return l'alias de la station
     */
    @Override
    public String alias(int id) {
        return stringTable.get(stationsAliases.getU16(ALIAS_ID, id));
    }

    /**
     * Retourne le nom de la station associé à l'alias correspondant à l'identifiant donné
     *
     * @param id : l'identifiant de l'alias
     * @return le nom de la station associé
     */
    @Override
    public String stationName(int id) {
        return stringTable.get(stationsAliases.getU16(STATION_NAME_ID, id));
    }

    /**
     * Retourne le nombre total d'alias stockés
     *
     * @return le nombre d'éléments dans le tableau des alias
     */
    @Override
    public int size() {
        return stationsAliases.size();
    }
}
