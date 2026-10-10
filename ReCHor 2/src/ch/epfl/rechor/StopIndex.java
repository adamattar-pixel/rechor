package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


import java.util.stream.*;

/**
 * Classe publique et finale, immuable, représentant un index de noms d'arrêts.
 *
 * @author AMRAOUI SAAD
 * @author ATTAR ADAM
 */
public final class StopIndex {
    private final List<String> stopNames;
    private final Map<String, String> aliasToStop;
    private static final int BEGGINING_OF_WORD = 4;
    private static final int END_OF_WORD = 2;

    private static final Map<Character, String> EQUIVALENCES = Map.of(
            'c', "cç",
            'a', "aáàâä",
            'e', "eéèêë",
            'i', "iíìîï",
            'o', "oóòôö",
            'u', "uúùûü"
    );

    /**
     * Construit un index immuable à partir d'une liste de noms d'arrêts
     * et d'une table des alias vers les noms principaux.
     *
     * @param names   liste des noms principaux d'arrêts à indexer
     * @param aliases table associant chaque nom alternatif à son nom principal
     */
    public StopIndex(List<String> names, Map<String, String> aliases) {
        this.stopNames = List.copyOf(names);
        this.aliasToStop = Map.copyOf(aliases);
    }

    /**
     * Recherche les noms d'arrêts correspondant à une requête donnée,
     * triés par pertinence décroissante et sans doublons.
     *
     * Les noms alternatifs sont pris en compte pour la recherche, mais
     * seuls les noms principaux sont retournés.
     *
     * @param query      la chaîne de recherche (peut contenir plusieurs mots)
     * @param maxResults nombre maximal de résultats à retourner
     * @return liste des noms principaux d'arrêts correspondant à la requête
     */
    public List<String> stopsMatching(String query, int maxResults) {
        if (query == null || query.isBlank()) {
            return stopNames.stream()
                    .limit(maxResults)
                    .toList();
        }

        return Stream.concat(
                        stopNames.stream()
                                .map(name -> Map.entry(name, score(name, query))),
                        aliasToStop.entrySet().stream()
                                .map(entry -> Map.entry(entry.getValue(), score(entry.getKey(), query)))
                )
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .distinct()
                .limit(maxResults)
                .toList();
    }

    /**
     * Calcule le score de correspondance entre un texte et une requête composée de mots.
     *
     * Si un des mots de la requête ne correspond pas au texte, le score est 0.
     *
     * @param text  le texte à comparer (nom d'arrêt ou alias)
     * @param query la requête complète à rechercher
     * @return un score entier positif, ou 0 si aucun match
     */
    private int score(String text, String query) {
        int[] scores = Arrays.stream(query.split("\\s+"))
                .mapToInt(part -> matchScore(text, part))
                .toArray();
        return Arrays.stream(scores).anyMatch(s -> s == -1) ? 0 : Arrays.stream(scores).sum();
    }

    /**
     * Calcule un score de correspondance partielle entre un texte et une partie de la requête.
     *
     * Le score tient compte de la position du match dans le texte (début/fin de mot)
     * et applique une pondération en conséquence.
     *
     * @param text      le texte à comparer
     * @param queryPart une partie (mot) de la requête
     * @return un score positif si match, sinon -1
     */
    private int matchScore(String text, String queryPart) {
        boolean isCaseSensitive = !queryPart.equals(queryPart.toLowerCase());
        Pattern pattern = Pattern.compile(
                toRegexPattern(queryPart),
                isCaseSensitive ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) return -1;

        int start = matcher.start();
        int end = matcher.end();
        int baseScore = (int) ((100.0 * queryPart.length()) / text.length());

        //multiplier par des coeffs
        if (start == 0 || !Character.isLetter(text.charAt(start - 1))) {
            baseScore *= BEGGINING_OF_WORD;
        }
        if (end == text.length() ||
            (end < text.length() && !Character.isLetter(text.charAt(end)))) {
            baseScore *= END_OF_WORD;
        }

        return baseScore;
    }

    /**
     * Transforme une chaîne de caractères en une expression régulière
     * prenant en compte les équivalences de caractères accentués.
     *
     * @param input chaîne à transformer
     * @return chaîne regex correspondante
     */
    private String toRegexPattern(String input) {
        return input.chars()
                .mapToObj(c -> {
                    char ch = (char) c;
                    return EQUIVALENCES.getOrDefault(ch, Pattern.quote(String.valueOf(ch)));
                })
                .map(s -> s.length() > 1 ? "[" + s + "]" : s)
                .collect(Collectors.joining());
    }
}
