package ch.epfl.rechor;


/**
 * Classe offrant des méthodes permettant de manipuler des intervalles d'entiers empaquetés dans un entier de type int
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public class PackedRange {

    /**
     * Constructeur privé rendant la classe non instanciable
     */
    private PackedRange() {}

    /**
     * Retourne la valeur de type int représentant l'intervalle d'entiers allant de startInclusive (inclus) à endExclusive (exclu)
     * Lève une IllegalArgumentException si sa borne inférieure ne peut être représentée avec 24 bits, ou sa longueur ne peut être représentée avec 8 bits
     *
     * @param startInclusive : début de l'intervalle que l'on souhaite empaqueter
     * @param endExclusive : fin de l'intervalle que l'on souhaite empaqueter
     * @throws IllegalArgumentException si cet intervalle ne peut pas être empaqueté
     * @return la valeur de type int représentant l'intervalle d'entiers à empaqueter
     */
    public static int pack(int startInclusive, int endExclusive){
        int duration = endExclusive - startInclusive;
        return Bits32_24_8.pack(startInclusive, duration);
    }

    /**
     * Retourne la longueur de l'intervalle d'entiers empaqueté donné
     *
     * @param interval : l'intervalle dont on souhaite connaître la longueur
     * @return la longueur de l'intervalle d'entiers empaqueté donné
     */
    public static int length(int interval){
        return Bits32_24_8.unpack8(interval);
    }

    /**
     * Retourne le début de l'intervalle d'entiers empaqueté donné, c.-à-d. le plus petit entier qui en fait partie
     *
     * @param interval : l'intervalle dont on souhaite extraire le début
     * @return le début de l'intervalle d'entiers empaqueté donné
     */
    public static int startInclusive(int interval){
        return Bits32_24_8.unpack24(interval);
    }

    /**
     * Retourne le plus petit entier strictement supérieur à tous les entiers de l'intervalle
     *
     * @param interval : l'intervalle dont on souhaite extraire la fin
     * @return le plus petit entier strictement supérieur à tous les entiers de l'intervalle
     */
    public static int endExclusive(int interval){
        return startInclusive(interval) + length(interval);
    }
}
