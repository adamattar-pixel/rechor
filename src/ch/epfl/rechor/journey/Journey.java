package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Représente un trajet complet composé d'une ou plusieurs étapes.
 * Un trajet est immuable et validé lors de sa construction
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public record Journey(List<Leg> legs) {

    /**
     * Représente un segment d'un trajet.
     * Chaque étape possède un arrêt de départ et d'arrivée ainsi qu'une heure de départ et d'arrivée,
     * et peut contenir des arrêts intermédiaires.
     * Une méthode par défaut est fournie pour calculer la durée de l'étape.
     */
    public sealed interface Leg {

        /**
         * Retourne l'arrêt de départ de cette étape.
         *
         * @return l'arrêt de départ
         */
        Stop depStop();

        /**
         * Retourne l'heure de départ de cette étape.
         *
         * @return l'heure de départ
         */
        LocalDateTime depTime();

        /**
         * Retourne l'arrêt d'arrivée de cette étape.
         *
         * @return l'arrêt d'arrivée
         */
        Stop arrStop();

        /**
         * Retourne l'heure d'arrivée de cette étape.
         *
         * @return l'heure d'arrivée
         */
        LocalDateTime arrTime();

        /**
         * Retourne la liste des arrêts intermédiaires de cette étape.
         *
         * @return la liste des arrêts intermédiaires
         */
        List<IntermediateStop> intermediateStops();

        /**
         * Retourne la durée de cette étape.
         *
         * @return la durée entre l'heure de départ et l'heure d'arrivée
         */
        default Duration duration() {
            return Duration.between(depTime(), arrTime());
        }

        /**
         * Représente un arrêt intermédiaire entre le départ et l'arrivée.
         */
        record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime) {

            /**
             * Construit un arrêt intermédiaire.
             *
             * @param stop : l'arrêt
             * @param arrTime : l'heure d'arrivée à cet arrêt
             * @param depTime : l'heure de départ de cet arrêt ; ne doit pas être antérieure à arrTime
             * @throws NullPointerException si stop, arrTime ou depTime est null
             * @throws IllegalArgumentException si depTime est avant arrTime
             */
            public IntermediateStop {
                requireNonNull(stop);
                requireNonNull(arrTime);
                requireNonNull(depTime);
                Preconditions.checkArgument(!depTime.isBefore(arrTime));
            }
        }

        /**
         * Représente une étape effectuée par transport public.
         */
        record Transport(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime,
                         List<IntermediateStop> intermediateStops, Vehicle vehicle,
                         String route, String destination) implements Leg {
            /**
             * Construit une étape de transport.
             *
             * @param depStop : l'arrêt de départ
             * @param depTime : l'heure de départ
             * @param arrStop : l'arrêt d'arrivée
             * @param arrTime : l'heure d'arrivée ; ne doit pas être avant depTime
             * @param intermediateStops : la liste des arrêts intermédiaires (copiée pour assurer l'immutabilité)
             * @param vehicle : le véhicule utilisé
             * @param route : le nom de la ligne ou du trajet
             * @param destination : la destination finale affichée sur le véhicule
             * @throws NullPointerException si un paramètre (sauf intermediateStops) est null
             * @throws IllegalArgumentException si arrTime est avant depTime
             */
            public Transport {
                requireNonNull(depStop);
                requireNonNull(depTime);
                requireNonNull(arrStop);
                requireNonNull(arrTime);
                requireNonNull(vehicle);
                requireNonNull(route);
                requireNonNull(destination);
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
                intermediateStops = List.copyOf(intermediateStops);
            }
        }

        /**
         * Représente une étape effectuée à pied
         */
        record Foot(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime) implements Leg {
            /**
             * Construit une étape à pied
             *
             * @param depStop : l'arrêt de départ
             * @param depTime : l'heure de départ
             * @param arrStop : l'arrêt d'arrivée
             * @param arrTime : l'heure d'arrivée ; ne doit pas être avant depTime
             * @throws NullPointerException si un paramètre est null
             * @throws IllegalArgumentException si arrTime est avant depTime
             */
            public Foot {
                requireNonNull(depStop);
                requireNonNull(depTime);
                requireNonNull(arrStop);
                requireNonNull(arrTime);
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
            }

            /**
             * Retourne une liste vide car les étapes à pied n'ont pas d'arrêts intermédiaires
             *
             * @return une liste vide d'arrêts intermédiaires
             */
            public List<IntermediateStop> intermediateStops() {
                return Collections.emptyList();
            }

            /**
             * Retourne vrai si cette étape à pied représente une correspondance dans la même station
             *
             * @return vrai si les arrêts de départ et d'arrivée ont le même nom, faux sinon
             */
            public boolean isTransfer() {
                return depStop.name().equals(arrStop.name());
            }
        }
    }

    /**
     * Construit un trajet.
     * Valide que la liste des étapes n'est ni null ni vide, que les étapes consécutives alternent en type,
     * et que l'heure de départ de chaque étape n'est pas avant l'heure d'arrivée de l'étape précédente
     *
     * @param legs la liste des étapes composant le trajet
     * @throws IllegalArgumentException si legs est null, vide, ou si une contrainte de temps ou d'alternance de type est violée
     */
    public Journey {
        Preconditions.checkArgument(legs != null);
        Preconditions.checkArgument(!legs.isEmpty());
        for (int i = 1; i < legs.size(); i++) {
            Preconditions.checkArgument(!legs.get(i - 1).getClass().equals(legs.get(i).getClass()));
            Preconditions.checkArgument(!legs.get(i).depTime().isBefore(legs.get(i - 1).arrTime()));
        }
        legs = List.copyOf(legs);
    }

    /**
     * Retourne l'arrêt de départ du trajet, qui est l'arrêt de départ de la première étape
     *
     * @return l'arrêt de départ du trajet
     */
    public Stop depStop() {
        return legs.getFirst().depStop();
    }

    /**
     * Retourne l'arrêt d'arrivée du trajet, qui est l'arrêt d'arrivée de la dernière étape
     *
     * @return l'arrêt d'arrivée du trajet
     */
    public Stop arrStop() {
        return legs.getLast().arrStop();
    }

    /**
     * Retourne l'heure de départ du trajet, prise de la première étape
     *
     * @return l'heure de départ du trajet
     */
    public LocalDateTime depTime() {
        return legs.getFirst().depTime();
    }

    /**
     * Retourne l'heure d'arrivée du trajet, prise de la dernière étape
     *
     * @return l'heure d'arrivée du trajet
     */
    public LocalDateTime arrTime() {
        return legs.getLast().arrTime();
    }

    /**
     * Retourne la durée totale du trajet
     *
     * @return la durée entre l'heure de départ et l'heure d'arrivée du trajet
     */
    public Duration duration() {
        return Duration.between(depTime(), arrTime());
    }
}