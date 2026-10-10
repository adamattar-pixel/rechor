package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représente une implémentation des plateformes basée sur un tableau structuré
 * Cette classe utilise un StructuredBuffer pour accéder aux données des plateformes
 * et une table de chaînes pour stocker les noms associés aux plateformes
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class BufferedPlatforms implements Platforms {
    private static final int NAME_ID = 0;
    private static final int STATION_ID = 1;
    private final StructuredBuffer platforms;
    private final List<String> stringTable;

    /**
     * Construit une instance de BufferedPlatforms à partir d'une table de chaînes et d'un ByteBuffer
     *
     * @param stringTable : la table contenant les noms des plateformes
     * @param buffer : le tableau de données contenant les informations structurées sur les plateformes
     */
    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = List.copyOf(stringTable);
        Structure structure = new Structure(
                field(NAME_ID, Structure.FieldType.U16),
                field(STATION_ID, Structure.FieldType.U16)
        );
        platforms = new StructuredBuffer(structure, buffer);
    }

    /**
     * Retourne l'identifiant de la station associée à la plateforme pour l'élément spécifié
     *
     * @param id : l'identifiant de la plateforme
     * @return l'identifiant de la station associée
     */
    @Override
    public int stationId(int id) {
        return platforms.getU16(STATION_ID, id);
    }

    /**
     * Retourne le nom de la plateforme pour l'élément spécifié
     *
     * @param id : l'identifiant de la plateforme
     * @return le nom de la plateforme
     */
    @Override
    public String name(int id) {
        return stringTable.get(platforms.getU16(NAME_ID, id));
    }

    /**
     * Retourne le nombre total de plateformes stockées
     *
     * @return le nombre d'éléments dans le tableau des plateformes
     */
    @Override
    public int size() {
        return platforms.size();
    }
}

