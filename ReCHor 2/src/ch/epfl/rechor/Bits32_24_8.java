package ch.epfl.rechor;

/**
 * Classe contenant des méthodes statiques permettant de manipuler une paire de valeurs
 * empaquetées dans un entier de 32 bits.
 * La paire de valeur est de 24 bits et 8 bits respectivement
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public class Bits32_24_8 {

    /**
     * Constructeur privé rendant la classe non instanciable
     */
    private Bits32_24_8() {}

    /**
     * Retourne le vecteur de 32 bits dont les 24 bits de poids fort sont bits24
     * et les 8 bits de poids faible sont bits8
     *
     * @param bits24 : les 24 bits de poids fort du vecteur
     * @param bits8 : les 8 bits de poids faible du vecteur
     * @throws IllegalArgumentException si l'une des deux valeurs nécessite plus de bits qu'elle ne devrait
     * @return le vecteur de 32 bits avec la paire de valeur empaquetée
     */
    public static int pack(int bits24, int bits8) {
        Preconditions.checkArgument((bits24 >> 24) == 0);
        Preconditions.checkArgument((bits8 >> 8) == 0);
        return (bits24 << 8) | bits8;
    }

    /**
     * Retourne les 24 bits de poids fort du vecteur de 32 bits donné
     *
     * @param bits32 : le vecteur de 32 bits dont on désire extraire les 24 bits de poids fort
     * @return les 24 bits de poids fort du vecteur de 32 bits donné
     */
    public static int unpack24(int bits32) {
        return (bits32 >>> 8) & 0b111111111111111111111111;
    }

    /**
     * Retourne les 8 bits de poids fort du vecteur de 32 bits donné
     *
     * @param bits32 : le vecteur de 32 bits dont on désire extraire les 8 bits de poids fort
     * @return les 8 bits de poids fort du vecteur de 32 bits donné
     */
    public static int unpack8(int bits32) {
        return (bits32 & 0b11111111);
    }

}
