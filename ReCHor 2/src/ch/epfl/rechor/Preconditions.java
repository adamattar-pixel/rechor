package ch.epfl.rechor;

/**
 * Une classe facilitant l'écriture de préconditions
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class Preconditions {
    /**
     * Constructeur privé rendant la classe non instanciable
     */
    private Preconditions() {}

    /**
     * Lève l'exception IllegalArgumentException si son argument est faux, et ne fait rien sinon
     *
     * @param shouldBeTrue : l'argument qui doit être vrai pour ne pas lancer d'exceptions
     * @throws IllegalArgumentException si l'argument est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
