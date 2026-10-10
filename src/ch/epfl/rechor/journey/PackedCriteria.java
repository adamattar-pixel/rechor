package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import static java.lang.Integer.toUnsignedLong;

/**
 * Classe permettant de manipuler des critères d'optimisation augmentés, empaquetés dans des valeurs de type long
 * Elle manipule des heures de départ ou d'arrivée exprimées en minutes écoulées depuis minuit, comprises entre -240 et 2879, et sont « translatées » lors de l'empaquetage
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public class PackedCriteria {

    /**
     * Constructeur privé rendant la classe non instanciable
     */
    private PackedCriteria() {
    }

    /**
     * Retourne la valeur de type long résultant de l'empaquetage de l'heure d'arrivée, du nombre de changements et de la charge utile donnés
     * L'heure d'arrivée occupe les bits 50 à 39 du long
     * Le nombre de changements occupe les bits 38 à 32 du long
     * La charge utile occupe les bits 31 à 0 du long
     *
     * @param arrMins : l'heure d'arrivée à empaqueter
     * @param changes : le nombre de changements à empaqueter
     * @param payload : la charge utile à empaqueter
     * @return la valeur de type long empaquetée à l'aide des trois valeurs reçues en paramètre
     * @throws IllegalArgumentException si l'heure d'arrivée est invalide, ou si le nombre de changements ne tient pas dans 7 bits
     */
    public static long pack(int arrMins, int changes, int payload) {
        Preconditions.checkArgument((changes >> 7) == 0);
        Preconditions.checkArgument(arrMins >= -240);
        Preconditions.checkArgument(arrMins < 2880);
        return (toUnsignedLong(arrMins + 240) << 39)
                | (toUnsignedLong(changes) << 32)
                | (toUnsignedLong(payload));
    }

    /**
     * Retourne vrai ssi les critères empaquetés donnés incluent une heure de départ
     *
     * @param criteria : les critères qui possèdent ou pas une heure de départ
     * @return vrai si le critère possède une heure de départ, faux sinon
     */
    public static boolean hasDepMins(long criteria) {
        return (criteria & 0b0111111111111000000000000000000000000000000000000000000000000000L) != 0;
    }

    /**
     * Retourne l'heure de départ (en minutes après minuit) des critères empaquetés donnés
     *
     * @param criteria : les critères empaquetés dont on souhaite extraire l'heure de départ
     * @return l'heure de départ des critères empaquetés donnés
     * @throws IllegalArgumentException si ces critères n'incluent pas une heure de départ
     */
    public static int depMins(long criteria) {
        Preconditions.checkArgument(hasDepMins(criteria));
        return (int) (~(criteria >> 51) & 0b111111111111) - 240;
    }

    /**
     * Retourne l'heure d'arrivée (en minutes après minuit) des critères empaquetés donnés
     *
     * @param criteria : les critères empaquetés dont on souhaite extraire l'heure d'arrivée
     * @return l'heure d'arrivée des critères empaquetés donnés
     */
    public static int arrMins(long criteria) {
        return (int) ((criteria >> 39) & 0b111111111111) - 240;
    }

    /**
     * Retourne le nombre de changements des critères empaquetés donnés
     *
     * @param criteria : les critères empaquetés dont on souhaite extraire le nombre de changements
     * @return le nombre de changements des critères empaquetés donnés
     */
    public static int changes(long criteria) {
        return (int) ((criteria >> 32) & 0b1111111);
    }

    /**
     * Retourne la charge utile associée aux critères empaquetés donnés
     *
     * @param criteria : les critères empaquetés dont on souhaite extraire la charge utile
     * @return la charge utile associée aux critères empaquetés donnés
     */
    public static int payload(long criteria) {
        return (int) (criteria);
    }

    /**
     * Retourne vrai si et seulement si les premiers critères empaquetés dominent ou sont égaux aux seconds
     *
     * @param criteria1 : les critères qui sont censés dominer (ou pas)
     * @param criteria2 : les critères qui sont censés se faire dominer (ou pas)
     * @return vrai si les premiers critères dominent les seconds, faux sinon
     * @throws IllegalArgumentException si l'un des ensembles de critères possède une heure de départ mais l'autre pas
     */
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
        Preconditions.checkArgument(hasDepMins(criteria1) == hasDepMins(criteria2));
        if (!hasDepMins(criteria1)) {
            return (arrMins(criteria1) <= arrMins(criteria2))
                    && (changes(criteria1) <= changes(criteria2));
        }
        return (depMins(criteria1) >= depMins(criteria2))
                && (arrMins(criteria1) <= arrMins(criteria2))
                && (changes(criteria1) <= changes(criteria2));
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés, mais sans heure de départ
     *
     * @param criteria : les critères empaquetés dont on souhaite retirer l'heure de départ
     * @return des critères empaquetés identiques à ceux donnés, mais sans heure de départ
     */
    public static long withoutDepMins(long criteria) {
        return (criteria & 0b1000000000000111111111111111111111111111111111111111111111111111L);
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés, mais avec l'heure de départ donnée
     *
     * @param criteria : les critères dont on souhaite modifier l'heure de départ
     * @param depMins1 : l'heure de départ à ajouter aux critères donnés
     * @return des critères empaquetés avec l'heure de départ donnée
     */
    public static long withDepMins(long criteria, int depMins1) {
        return (withoutDepMins(criteria)
                | ((~toUnsignedLong(depMins1 + 240)) & 0b111111111111) << 51);
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés, mais avec un changement de plus
     *
     * @param criteria : les critères dont on souhaite rajouter un changement
     * @return des critères empaquetés avec un changement de plus
     */
    public static long withAdditionalChange(long criteria) {
        return criteria + (0b100000000000000000000000000000000L);
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés, mais avec la charge utile donnée
     *
     * @param criteria : les critères dont on souhaite modifier la charge utile
     * @param payload1 : la charge utile que l'on souhaite attribuer
     * @return des critères empaquetés avec la charge utile donnée
     */
    public static long withPayload(long criteria, int payload1) {
        return ((criteria & 0b1111111111111111111111111111111100000000000000000000000000000000L)
                | toUnsignedLong(payload1));
    }
}
