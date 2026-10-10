package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représente une implémentation des connexions basée sur un tampon structuré.
 * Cette classe gère l'accès aux données des connexions à l'aide d'un StructuredBuffer
 * pour les informations principales et d'un IntBuffer pour les identifiants des connexions suivantes.
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class BufferedConnections implements Connections {
    private static final int DEP_STOP_ID = 0;
    private static final int DEP_MINUTES = 1;
    private static final int ARR_STOP_ID = 2;
    private static final int ARR_MINUTES = 3;
    private static final int TRIP_POS_ID = 4;
    private final StructuredBuffer flattenedConnections;
    private final IntBuffer nextConnection;

    /**
     * Construit une instance de BufferedConnections à partir d'un tampon de données et d'un tampon
     * de connexions successeurs.
     *
     * @param buffer : le tampon de données contenant les informations structurées sur les connexions
     * @param succBuffer : le tampon contenant les identifiants des connexions suivantes
     */
    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer) {
        Structure structure1 = new Structure(
                field(DEP_STOP_ID, Structure.FieldType.U16),
                field(DEP_MINUTES, Structure.FieldType.U16),
                field(ARR_STOP_ID, Structure.FieldType.U16),
                field(ARR_MINUTES, Structure.FieldType.U16),
                field(TRIP_POS_ID, Structure.FieldType.S32)
        );
        flattenedConnections = new StructuredBuffer(structure1, buffer);
        nextConnection = succBuffer.asIntBuffer();
    }

    /**
     * Retourne l'identifiant de l'arrêt de départ pour la connexion spécifiée.
     *
     * @param id : l'identifiant de la connexion
     * @return l'identifiant de l'arrêt de départ
     */
    @Override
    public int depStopId(int id) {
        return flattenedConnections.getU16(DEP_STOP_ID, id);
    }

    /**
     * Retourne le nombre de minutes de départ pour la connexion spécifiée.
     *
     * @param id : l'identifiant de la connexion
     * @return le nombre de minutes indiquant l'heure de départ
     */
    @Override
    public int depMins(int id) {
        return flattenedConnections.getU16(DEP_MINUTES, id);
    }

    /**
     * Retourne l'identifiant de l'arrêt d'arrivée pour la connexion spécifiée.
     *
     * @param id : l'identifiant de la connexion
     * @return l'identifiant de l'arrêt d'arrivée
     */
    @Override
    public int arrStopId(int id) {
        return flattenedConnections.getU16(ARR_STOP_ID, id);
    }

    /**
     * Retourne le nombre de minutes d'arrivée pour la connexion spécifiée.
     *
     * @param id : l'identifiant de la connexion
     * @return le nombre de minutes indiquant l'heure d'arrivée
     */
    @Override
    public int arrMins(int id) {
        return flattenedConnections.getU16(ARR_MINUTES, id);
    }

    /**
     * Retourne l'identifiant de la route (trip) pour la connexion spécifiée.
     * L'identifiant est extrait en décompressant les 24 bits les plus significatifs d'un entier 32 bits.
     *
     * @param id : l'identifiant de la connexion
     * @return l'identifiant de la route associé
     */
    @Override
    public int tripId(int id) {
        return Bits32_24_8.unpack24(flattenedConnections.getS32(TRIP_POS_ID, id));
    }

    /**
     * Retourne la position du trip dans l'élément empaqueté pour la connexion spécifiée.
     * Cette position est extraite en décompressant les 8 bits de poids faible d'un entier 32 bits.
     *
     * @param id : l'identifiant de la connexion
     * @return la position du trip associé
     */
    @Override
    public int tripPos(int id) {
        return Bits32_24_8.unpack8(flattenedConnections.getS32(TRIP_POS_ID, id));
    }

    /**
     * Retourne l'identifiant de la connexion suivante pour la connexion spécifiée.
     *
     * @param id l'identifiant de la connexion
     * @return l'identifiant de la connexion suivante
     */
    @Override
    public int nextConnectionId(int id) {
        return nextConnection.get(id);
    }

    /**
     * Retourne le nombre total de connexions stockées.
     *
     * @return le nombre d'éléments dans le tampon des connexions
     */
    @Override
    public int size() {
        return flattenedConnections.size();
    }
}
