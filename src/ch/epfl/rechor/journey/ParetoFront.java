package ch.epfl.rechor.journey;

import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static java.lang.System.arraycopy;

/**
 * Représente une frontière de Pareto de critères d'optimisation
 *
 * @autor AMRAOUI Saad
 * @autor ATTAR Adam
 */

public class ParetoFront {

    /**
     * Représente un bâtisseur de frontière de Pareto
     */
    public static class Builder {
        private long[] currentPareto;
        private int effectiveCapacity;
        private final static int initialCapacity = 2;

        /**
         * Construit un bâtisseur dont la frontière en cours de construction est vide
         */
        public Builder() {
            currentPareto = new long[initialCapacity];
            effectiveCapacity = 0;
        }

        /**
         * Construit un nouveau bâtisseur avec les mêmes attributs que celui reçu en argument
         *
         * @param that : le bâtisseur que l'on désire copier
         */
        public Builder(Builder that) {
            effectiveCapacity = that.effectiveCapacity;
            currentPareto = that.currentPareto.clone();
        }

        /**
         * Retourne vrai si et seulement si la frontière en cours de construction est vide
         *
         * @return vrai si la frontière en cours de construction est vide, sinon faux
         */
        public boolean isEmpty() {
            return effectiveCapacity == 0;
        }

        /**
         * Vide la frontière en cours de construction en supprimant tous ses éléments
         *
         * @return la frontière en cours de construction vidée
         */
        //mess check remarque 27
        public Builder clear() {
            return new Builder();
        }

        /**
         * Ajoute à la frontière le tuple de critères empaquetés donnés
         * Supprime les éventuels éléments dominés dans la frontière
         *
         * @param packedTuple : le tuple éventuellement ajouté à la frontière
         * @return la frontière en cours de construction après l'ajout
         */
        public Builder add(long packedTuple) {
            //Trouver le premier element
            long maxPayloadPackedTuple = withPayload(packedTuple, Integer.MAX_VALUE);
            int insertIndex = effectiveCapacity;
            for (int currentIndex = 0; currentIndex < effectiveCapacity; currentIndex++) {
                if (dominatesOrIsEqual(currentPareto[currentIndex], packedTuple)) return this;
                if (maxPayloadPackedTuple < currentPareto[currentIndex]) {
                    insertIndex = currentIndex;
                    break;
                }
            }

            //Laisser les non-dominés
            int notDominatedTuples = insertIndex;
            for (int currentIndex = insertIndex; currentIndex < effectiveCapacity; ++currentIndex) {
                if (dominatesOrIsEqual(packedTuple, currentPareto[currentIndex])) continue;
                if (notDominatedTuples != currentIndex) {
                    currentPareto[notDominatedTuples] = currentPareto[currentIndex];
                }
                notDominatedTuples += 1;
            }
            effectiveCapacity = notDominatedTuples;

            // Insérer l'élément de manière optimisée
            if (effectiveCapacity >= currentPareto.length) {
                long[] biggerTable = new long[(int) (currentPareto.length * 1.5)];
                System.arraycopy(currentPareto, 0, biggerTable, 0, insertIndex);
                System.arraycopy(currentPareto, insertIndex, biggerTable, insertIndex + 1, effectiveCapacity - insertIndex);
                currentPareto = biggerTable;
            } else {
                System.arraycopy(currentPareto, insertIndex, currentPareto, insertIndex + 1, effectiveCapacity - insertIndex);
            }
            currentPareto[insertIndex] = packedTuple;
            effectiveCapacity++;
            return this;
        }

        /**
         * Ajoute à la frontière un tuple de critères augmentés sans d'heure de départ
         *
         * @param arrMins : l'heure d'arrivée du critère à ajouter
         * @param changes : le nombre de changements du critère à ajouter
         * @param payload : la charge utile donnée du critère à ajouter
         * @return la frontière en cours de construction après l'ajout
         */
        public Builder add(int arrMins, int changes, int payload) {
            add(pack(arrMins, changes, payload));
            return this;
        }

        /**
         * Ajoute à la frontière tous les tuples présents dans la frontière en cours de construction par le bâtisseur donné
         *
         * @param that : le bâtisseur dont on souhaite extraire les tuples à ajouter
         * @return la frontière en cours de construction après l'ajout
         */
        public Builder addAll(Builder that) {
            for (int i = 0; i < that.effectiveCapacity; i++) {
                add(that.currentPareto[i]);
            }
            return this;
        }

        /**
         * Retourne vrai si et seulement si chacun des tuples de la frontière donnée est dominé par, ou égal à, au moins un tuple du récepteur
         * On fixe aux tuples qu'on souhaite comparer une heure de départ commune
         *
         * @param that    : le bâtisseur dont on souhaite extraire les tuples à comparer
         * @param depMins : l'heure de départ que l'on fixe sur les tuple
         * @return vrai si chacun des tuples de la frontière donnée est dominé par au moins un tuple du récepteur
         */
        public boolean fullyDominates(Builder that, int depMins) {
            long[] thatWithDepMins = new long[that.effectiveCapacity];
            for (int i = 0; i < that.effectiveCapacity; i++) {
                thatWithDepMins[i] = withDepMins(that.currentPareto[i], depMins);
            }

            boolean dominates = true;
            for (int i = 0; i < thatWithDepMins.length; i++) {
                dominates = false;
                for (int j = 0; j < effectiveCapacity; j++) {
                    if (dominatesOrIsEqual(currentPareto[j], thatWithDepMins[i])) {
                        dominates = true;
                    }
                }
                if (!dominates) {
                    return false;
                }
            }
            return dominates;
        }

        /**
         * Appelle la méthode accept de l'action donnée avec chacun des critères de la frontière, dans l'ordre
         *
         * @param action : action de type LongConsumer
         */
        public void forEach(LongConsumer action) {
            for (int i = 0; i < effectiveCapacity; i++) {
                action.accept(currentPareto[i]);
            }
        }

        /**
         * Retourne la frontière de Pareto en cours de construction par ce bâtisseur
         *
         * @return la frontière de Pareto en cours de construction par ce bâtisseur
         */
        public ParetoFront build() {
            long[] finalPareto = new long[effectiveCapacity];
            arraycopy(currentPareto, 0, finalPareto, 0, effectiveCapacity);
            return new ParetoFront(finalPareto);
        }

        /**
         * Retourne une représentation textuelle de la frontière de Pareto avec l'heure de départ, d'arrivée le nombre de changements, et la charge utile concatenés respectivement en les séparant d'un espace
         *
         * @return la représentation textuelle de la frontière de Pareto
         */
        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            forEach(value -> {
                if (!hasDepMins(value)) {
                    s.append(arrMins(value)).append(" ").append(changes(value)).append(" ").append(payload(value)).append("\r\n");
                } else {
                    s.append(depMins(value)).append(" ").append(arrMins(value)).append(" ").append(changes(value)).append(" ").append(payload(value)).append("\r\n");
                }
            });
            return s.toString();
        }
    }

    private final long[] tuple;
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);

    /**
     * Construit une frontière de pareto privée
     *
     * @param tuple : un tableau de type long[] contenant les critères empaquetés
     */
    private ParetoFront(long[] tuple) {
        this.tuple = tuple;
    }

    /**
     * Retourne la taille de la frontière de Pareto, c.-à-d. le nombre de tuples qu'elle contient
     *
     * @return la taille de la frontière de Pareto
     */
    public int size() {
        return tuple.length;
    }

    /**
     * Retourne les critères d'optimisation empaquetés dont l'heure d'arrivée et le nombre de changements sont ceux donnés
     *
     * @param arrMins : l'heure d'arrivée
     * @param changes : le nombre de changements
     * @return les critères d'optimisation empaquetés de l'heure d'arrivée et du nombre de changements donnés
     * @throws NoSuchElementException si ces critères ne font pas partie de la frontière
     */
    public long get(int arrMins, int changes) {
        for (int i = 0; i < size(); i++) {
            if (arrMins(tuple[i]) == arrMins && changes(tuple[i]) == changes) {
                return tuple[i];
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Appelle la méthode accept de l'action donnée avec chacun des critères de la frontière, dans l'ordre
     *
     * @param action : action de type LongConsumer
     */
    public void forEach(LongConsumer action) {
        for (int i = 0; i < size(); i++) {
            action.accept(tuple[i]);
        }
    }
}
