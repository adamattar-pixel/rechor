package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;


import java.nio.ByteBuffer;

/**
 * Représente un tableau de données structuré permettant l'accès à des valeurs organisées en fonction
 * d'une structure prédéfinie.
 * Ce tableau stocke des éléments de taille fixe définis par une structure, permettant ainsi
 * de récupérer des valeurs sous forme d'entiers non signés (8 et 16 bits) ou d'entiers signés (32 bits)
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public class StructuredBuffer {
    private final ByteBuffer buffer;
    private final Structure structure;

    /**
     * Construit un tableau structuré à partir d'une structure et d'un tableau de données
     *
     * @param structure : la structure définissant l'organisation des données
     * @param buffer : le tableau contenant les données
     * @throws IllegalArgumentException si la capacité du tableau n'est pas un multiple de la taille totale d'un élément
     */
    public StructuredBuffer(Structure structure, ByteBuffer buffer) {
        Preconditions.checkArgument(buffer.capacity() % structure.totalSize() == 0);
        this.buffer = buffer;
        this.structure = structure;
    }

    /**
     * Retourne le nombre d'éléments stockés dans le tableau
     *
     * @return le nombre d'éléments présents dans le tableau
     */
    public int size() {
        return buffer.capacity() / structure.totalSize();
    }

    /**
     * Récupère une valeur entière non signée sur 8 bits à un indice donné dans un champ spécifique
     *
     * @param fieldIndex : l'index du champ dans la structure
     * @param elementIndex : l'index de l'élément dans le tableau
     * @return la valeur sous forme d'un entier non signé sur 8 bits
     */
    public int getU8(int fieldIndex, int elementIndex) {
        return Byte.toUnsignedInt(buffer.get(structure.offset(fieldIndex, elementIndex)));
    }

    /**
     * Récupère une valeur entière non signée sur 16 bits à un indice donné dans un champ spécifique
     *
     * @param fieldIndex : l'index du champ dans la structure
     * @param elementIndex : l'index de l'élément dans le tableau
     * @return la valeur sous forme d'un entier non signé sur 16 bits
     */
    public int getU16(int fieldIndex, int elementIndex) {
        return Short.toUnsignedInt(buffer.getShort(structure.offset(fieldIndex, elementIndex)));
    }

    /**
     * Récupère une valeur entière signée sur 32 bits à un indice donné dans un champ spécifique
     *
     * @param fieldIndex : l'index du champ dans la structure
     * @param elementIndex : l'index de l'élément dans le tableau
     * @return la valeur sous forme d'un entier signé sur 32 bits
     */
    public int getS32(int fieldIndex, int elementIndex) {
        return buffer.getInt(structure.offset(fieldIndex, elementIndex));
    }
}
