package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Représente un document JSON sous forme d'une structure arborescente.
 *
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public sealed interface Json {

    /**
     * Retourne la représentation textuelle au format JSON de l'objet.
     * Cette méthode est redéfinie par chaque sous-type de Json.
     *
     * @return la représentation JSON sous forme de chaîne
     */
    @Override
    String toString();

    /**
     * Représente un tableau JSON.
     * Contient une liste de valeurs de type Json.
     */
    record JArray(List<Json> list) implements Json {

        /**
         * Retourne la représentation JSON du tableau,
         * en concaténant les représentations des éléments entre crochets.
         *
         * @return une chaîne représentant le tableau JSON
         */
        @Override
        public String toString() {
            return list.stream()
                    .map(Json::toString)
                    .collect(Collectors.joining(",", "[", "]"));
        }
    }

    /**
     * Représente un objet JSON.
     * Contient une table associative de paires clé-valeur,
     * où la clé est un Json et la valeur est un String.
     */
    record JObject(Map<Json, String> map) implements Json {

        /**
         * Retourne la représentation JSON de l'objet,
         * en concaténant les paires clé:valeur entre accolades.
         *
         * @return une chaîne représentant l'objet JSON
         */
        @Override
        public String toString() {
            return map.entrySet().stream()
                    .map(e -> e.getKey().toString() + ":" + e.getValue())
                    .collect(Collectors.joining(",", "{", "}"));
        }
    }

    /**
     * Représente une chaîne JSON.
     * Contient une chaîne Java représentant le contenu de la chaîne JSON.
     */
    record JString(String s) implements Json {

        /**
         * Retourne la chaîne entre guillemets, comme en JSON.
         *
         * @return une chaîne représentant la chaîne JSON
         */
        @Override
        public String toString() {
            return "\"" + s + "\"";
        }
    }

    /**
     * Représente un nombre JSON.
     * Contient une valeur de type double.
     */
    record JNumber(double number) implements Json {

        /**
         * Retourne la représentation en chaîne du nombre JSON.
         *
         * @return une chaîne représentant le nombre JSON
         */
        @Override
        public String toString() {
            return number + "";
        }
    }
}
