package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Représente une implémentation des correspondances (transfers) basée sur un tampon structuré.
 * Cette classe utilise un StructuredBuffer pour accéder aux données des correspondances,
 * et construit un tableau permettant de retrouver rapidement l'intervalle des correspondances
 * arrivant à une station donnée.
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class BufferedTransfers implements Transfers {
    private static final int DEP_STATION_ID = 0;
    private static final int ARR_STATION_ID = 1;
    private static final int TRANSFER_MINUTES = 2;
    private final StructuredBuffer transfers;
    private final int[] arrivals;

    /**
     * Construit une instance de BufferedTransfers à partir d'un ByteBuffer.
     * Le tampon doit contenir les données structurées selon trois champs :
     * l'identifiant de la station de départ (U16), l'identifiant de la station d'arrivée (U16)
     * et le nombre de minutes de transfert (U8).
     * Ce constructeur prépare également un tableau permettant de retrouver, pour chaque station,
     * l'intervalle des correspondances qui y arrivent.
     *
     * @param buffer : le tampon de données contenant les correspondances
     */
    public BufferedTransfers(ByteBuffer buffer) {
        Structure structure = new Structure(
                field(DEP_STATION_ID, Structure.FieldType.U16),
                field(ARR_STATION_ID, Structure.FieldType.U16),
                field(TRANSFER_MINUTES, Structure.FieldType.U8)
        );
        transfers = new StructuredBuffer(structure, buffer);

        //crée une table de taille : le nombre de stations d'arrivée différentes
        int differentArrival = -1;
        for (int i = 0; i < size(); i++) {
            if (differentArrival < transfers.getU16(ARR_STATION_ID, i)) {
                differentArrival = transfers.getU16(ARR_STATION_ID, i);
            }
        }
        arrivals = new int[differentArrival + 1];

        //Remplie le tableau en empaquetant les intervales contenant les index des changements arrivant à la gare d'index i
        //Elle empaquette l'intervale dès que l'indice de la station suivante est différent de la précédente et gère le cas
        //particulier du dernier élément
        int begginingId = 0;
        for (int i = 0; i < transfers.size(); i++) {
            if (i == transfers.size() - 1 || (transfers.getU16(ARR_STATION_ID, i)
                    != transfers.getU16(ARR_STATION_ID, i + 1))) {
                arrivals[transfers.getU16(ARR_STATION_ID, begginingId)]
                        = PackedRange.pack(begginingId, i + 1);
                begginingId = i + 1;
            }
        }
    }

    /**
     * Retourne l'identifiant de la station de départ pour la correspondance spécifiée.
     *
     * @param id : l'identifiant de la correspondance
     * @return l'identifiant de la station de départ
     */
    @Override
    public int depStationId(int id) {
        return transfers.getU16(DEP_STATION_ID, id);
    }

    /**
     * Retourne le nombre de minutes de transfert pour la correspondance spécifiée.
     *
     * @param id : l'identifiant de la correspondance
     * @return le nombre de minutes de transfert
     */
    @Override
    public int minutes(int id) {
        return transfers.getU8(TRANSFER_MINUTES, id);
    }

    /**
     * Retourne l'intervalle empaqueté des correspondances arrivant à la station spécifiée.
     * L'intervalle est empaqueté à l'aide de PackedRange, et permet de connaître
     * l'indice du premier et du dernier élément correspondant.
     *
     * @param stationId l'identifiant de la station d'arrivée
     * @return l'intervalle empaqueté des correspondances arrivant à la station
     */
    @Override
    public int arrivingAt(int stationId) {
        return arrivals[stationId];
    }

    /**
     * Retourne le nombre de minutes de transfert entre une station de départ et une station d'arrivée données.
     * Parcourt les correspondances arrivant à la station d'arrivée et retourne le nombre de minutes
     * pour la correspondance dont l'identifiant de station de départ correspond à celui spécifié.
     *
     * @param depStationId : l'identifiant de la station de départ
     * @param arrStationId : l'identifiant de la station d'arrivée
     * @return le nombre de minutes de transfert
     * @throws NoSuchElementException si aucune correspondance ne correspond aux critères
     */
    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        int start = PackedRange.startInclusive(arrivingAt(arrStationId));
        int end = PackedRange.endExclusive(arrivingAt(arrStationId));
        for (int i = start; i < end; i++) {
            if (depStationId(i) == depStationId) {
                return minutes(i);
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Retourne le nombre total de correspondances stockées.
     *
     * @return le nombre d'éléments dans le tampon des correspondances
     */
    @Override
    public int size() {
        return transfers.size();
    }
}

