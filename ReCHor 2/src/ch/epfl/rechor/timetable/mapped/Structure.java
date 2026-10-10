package ch.epfl.rechor.timetable.mapped;


import ch.epfl.rechor.Preconditions;

/**
 * Classe qui a pour but de faciliter la description de la structure des données aplaties
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public class Structure {
    private Field[] fields;
    private int[] indexTable;

    /**
     * Construit une structure dont les champs sont ceux donnés
     *
     * @param fields : les champs que l'on souhaite attribuer à la structure
     * @throws IllegalArgumentException si ces champs ne sont pas donnés dans l'ordre
     */
    public Structure(Field... fields) {
        this.fields = new Field[fields.length];
        this.indexTable = new int[fields.length];
        int currentSize = 0;

        for (int i = 0 ; i < fields.length ; i++){
            Preconditions.checkArgument(fields[i].index() == i);
            this.indexTable[i] = currentSize;
            switch (fields[i].type()) {
                case U8 :
                    currentSize += 1;
                    break;
                case U16 :
                    currentSize += 2;
                    break;
                case S32 :
                    currentSize += 4;
                    break;
            }
            this.fields[i] = fields[i];
        }
    }

    /**
     * Retourne la taille totale, en octets, de la structure
     * p. ex. 10 pour celle correspondant aux gares car elle est constituée de 10 octets
     *
     * @return la taille totale de la structure
     */
    public int totalSize(){
        int numByte = 0;
        for (int i = 0 ; i < fields.length ; i++){
            switch (fields[i].type()) {
                case U8 :
                    numByte += 1;
                    break;
                case U16 :
                    numByte += 2;
                    break;
                case S32 :
                    numByte += 4;
                    break;
            }
        }
        return numByte;
    }

    /**
     * Retourne l'index, dans le tableau d'octets contenant les données aplaties, du premier octet du champ d'index fieldIndex de l'élément d'index elementIndex
     *
     * @param fieldIndex : l'index du champ contentant l'octet désiré
     * @param elementIndex : l'index de l'élément contenant le champ désiré
     * @throws IndexOutOfBoundsException si l'index du champ est invalide
     * @return l'index du premier octet du champ d'index fieldIndex de l'élément d'index elementIndex
     */
    public int offset(int fieldIndex, int elementIndex){
        return elementIndex * totalSize() + indexTable[fieldIndex];
    }

    /**
     * Les différents types de champs de la structure
     */
    public enum FieldType {
        U8 ,
        U16,
        S32;
    }

    /**
     * Construit un champ possédant un type et une position dans la structure
     *
     * @param index : l'index du champ dans la structure
     * @param type : le type du champ
     */
    public record Field(int index, FieldType type){
        public Field {
            if (type == null) {
                throw new NullPointerException();
            }
        }

    }

    /**
     * Retourne une instance de Field avec les attributs donnés
     *
     * @param index : l'index du champ
     * @param type : le type du champ
     * @return une instance de Field avec les attributs donnés
     */
    public static Field field(int index, FieldType type) {
        return new Field(index, type);
    }
}
