package ch.epfl.rechor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import ch.epfl.rechor.journey.Stop;
import ch.epfl.rechor.journey.Journey;

/**
 * Classe utilitaire pour formater diverses valeurs liées aux trajets en chaînes de caractères lisibles.
 * Cette classe fournit des méthodes statiques pour formater les durées, les horaires,
 * les noms de quais et de voies, ainsi que les descriptions d'étapes de trajet
 * selon les conventions françaises spécifiées.
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class FormatterFr {

    //private FormatterFr() {}

    /**
     * Formate une durée en chaîne de caractères.
     * Si la durée est d'une heure ou plus, le format est "H h M min", sinon "M min".
     * Par exemple, une durée de 1 heure et 3 minutes sera formatée en "1 h 3 min",
     * tandis qu'une durée de 12 minutes sera affichée sous la forme "12 min".
     *
     * @param duration : la durée à formater
     * @return une chaîne représentant la durée
     */
    public static String formatDuration(Duration duration) {
        if (duration.toMinutes() >= 60) {
            return duration.toHours() + " h " + (duration.toMinutes() - duration.toHours() * 60) + " min";
        } else {
            return duration.toMinutes() + " min";
        }
    }

    /**
     * Formate un LocalDateTime en une chaîne au format "Hhmm".
     * Par exemple, une heure de 16:30 sera formatée en "16h30".
     *
     * @param dateTime : la date/heure à formater
     * @return une chaîne représentant l'heure formatée
     */

    //mess je vien de modifier le code en desous a cause du corriger a check
    private static DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY)       // Append the hour (0-23).
            .appendLiteral('h')                           // Insert the literal 'h' as a separator.
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)     // Append minutes, formatted as two digits.
            .toFormatter();
    public static String formatTime(LocalDateTime dateTime) {
        return fmt.format(dateTime);
    }

     /**
     * Formate le nom du quai ou de la voie d'un arrêt donné.
     * Si le nom de la voie n'est pas disponible ou vide, retourne une chaîne vide.
     * Sinon, si le nom commence par un chiffre, il est préfixé par "voie" ; sinon, par "quai".
     *
     * @param stop : l'arrêt dont la voie ou le quai doit être formaté
     * @return une chaîne représentant le nom de la voie ou du quai formaté, ou une chaîne vide si non disponible
     */
    public static String formatPlatformName(Stop stop) {
        if (stop.name() == null || stop.platformName() == null || stop.name().isEmpty() || stop.platformName().isEmpty()) {
            return "";
        }
        if (stop.platformName().charAt(0) >= '0' && stop.platformName().charAt(0) <= '9') {
            return "voie " + stop.platformName();
        }
        return "quai " + stop.platformName();
    }

    /**
     * Formate une étape de marche d'un trajet en une chaîne descriptive.
     * Si l'étape représente une correspondance (marche entre deux arrêts d'une même station),
     * elle est étiquetée comme "changement" ; sinon, comme "trajet à pied".
     * La durée est ajoutée entre parenthèses.
     *
     * @param footLeg l'étape de marche à formater
     * @return une chaîne représentant l'étape de marche formatée
     */
    public static String formatLeg(Journey.Leg.Foot footLeg) {
        if (footLeg.isTransfer()) {
            return "changement (" + formatDuration(footLeg.duration()) + ")";
        } else {
            return "trajet à pied (" + formatDuration(footLeg.duration()) + ")";
        }
    }

    /**
     * Formate une étape de transport d'un trajet en une chaîne descriptive détaillée.
     * La chaîne formatée inclut l'heure de départ, le nom de l'arrêt de départ
     * (avec la voie ou le quai si disponible), une flèche "→", et l'arrêt d'arrivée
     * avec son heure et sa voie ou son quai si disponible.
     *
     * @param leg l'étape de transport à formater
     * @return une chaîne représentant l'étape de transport formatée
     */
    public static String formatLeg(Journey.Leg.Transport leg) {
        StringBuilder text = new StringBuilder(formatTime(leg.depTime()) + " " + leg.depStop().name());
        if (formatPlatformName(leg.depStop()).isEmpty()) {
            text.append(" → ");
        } else {
            text.append(" (")
                .append(formatPlatformName(leg.depStop()))
                .append(") → ");
        }
        if (formatPlatformName(leg.arrStop()).isEmpty()) {
            text.append(leg.arrStop().name())
                .append(" (arr. ")
                .append(formatTime(leg.arrTime()))
                .append(")");
        } else {
            text.append(leg.arrStop().name())
                .append(" (arr. ")
                .append(formatTime(leg.arrTime()))
                .append(" ")
                .append(formatPlatformName(leg.arrStop()))
                .append(")");
        }
        return text.toString();
    }

    /**
     * Formate les informations de la ligne et de la destination pour une étape de transport.
     * La chaîne formatée suit le modèle "route Direction destination".
     *
     * @param transportLeg : l'étape de transport à partir de laquelle extraire la ligne et la destination
     * @return une chaîne représentant la ligne et la destination formatées
     */
    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        return transportLeg.route() + " Direction " + transportLeg.destination();
    }
}
