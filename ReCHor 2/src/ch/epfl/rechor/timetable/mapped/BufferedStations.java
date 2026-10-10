package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static java.lang.Math.scalb;

/**
 * Représente une implémentation des stations basée sur un tableau structuré
 * Elle utilise un StructuredBuffer pour accéder aux données des stations
 * et une table de chaînes pour stocker les noms associés aux identifiants de station
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class BufferedStations implements Stations {
    private static final int NAME_ID = 0;
    private static final int LON = 1;
    private static final int LAT = 2;
    private final StructuredBuffer stations;
    private final List<String> stringTable;

    /**
     * Construit une instance de BufferedStations à partir d'une table de chaînes et d'un ByteBuffer
     *
     * @param stringTable : la table contenant les noms des stations
     * @param buffer : le tableau de données contenant les informations structurées sur les stations
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        Structure structure = new Structure(
                field(NAME_ID, Structure.FieldType.U16),
                field(LON, Structure.FieldType.S32),
                field(LAT, Structure.FieldType.S32)
        );
        stations = new StructuredBuffer(structure, buffer);
    }

    /**
     * Retourne le nom de la station correspondant à l'identifiant donné
     *
     * @param id : l'identifiant de la station
     * @return le nom de la station
     */
    @Override
    public String name(int id) {
        return stringTable.get(stations.getU16(NAME_ID, id));
    }

    /**
     * Retourne la longitude de la station correspondant à l'identifiant donné
     *
     * @param id : l'identifiant de la station
     * @return la longitude de la station en degrés
     */
    @Override
    public double longitude(int id) {
        return scalb((stations.getS32(LON, id) * 360.0), -32);
    }

    /**
     * Retourne la latitude de la station correspondant à l'identifiant donné
     *
     * @param id : l'identifiant de la station
     * @return la latitude de la station en degrés
     */
    @Override
    public double latitude(int id) {
        return scalb((stations.getS32(LAT, id) * 360.0), -32);
    }

    /**
     * Retourne le nombre total de stations stockées
     *
     * @return le nombre d'éléments dans le tableau de stations
     */
    @Override
    public int size() {
        return stations.size();
    }
}