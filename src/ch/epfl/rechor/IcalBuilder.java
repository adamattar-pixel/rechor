package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

/**
 * Un bâtisseur d'événement au format iCalendar
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public final class IcalBuilder {
    private final ArrayList<Component> currentComponents = new ArrayList<>();
    private final StringBuilder wholeTextCalendar = new StringBuilder();

    /**
     * Représente un composant ou un objet
     */
    public enum Component {
        VCALENDAR,
        VEVENT;
    }

    /**
     * Représente un nom d'une ligne
     */
    public enum Name {
        BEGIN,
        END,
        PRODID,
        VERSION,
        UID,
        DTSTAMP,
        DTSTART,
        DTEND,
        SUMMARY,
        DESCRIPTION;
    }

    /**
     * Ajoute à l'événement en cours de construction une ligne dont le nom et la valeur sont ceux donnés
     * Plie la ligne si elle dépasse 75 caractères
     *
     * @param name : le nom de l'élément à ajouter
     * @param value : la valeur de l'élément à ajouter
     * @return l'évènement en cours de construction après l'ajout de la ligne désirée
     */
    public IcalBuilder add(Name name, String value) {
        StringBuilder valueText = new StringBuilder();
        valueText.append(name.toString())
                 .append(":")
                 .append(value);
        for (int i = 75; i < valueText.length(); i += 77) { //77 car on prend en compte le CRLF et l'espace au début de chaque ligne
            valueText.insert(i, "\r\n ");
        }
        wholeTextCalendar.append(valueText)
                         .append("\r\n");
        return this;
    }

    /**
     * Ajoute à l'événement en cours de construction une ligne dont le nom est celui donné et la valeur est la représentation textuelle de la date/heure donnée
     * Par ex. : le 18 février 2025 à 16h13 est représenté par : 20250218T161300
     *
     * @param name : le nom de l'élément à ajouter
     * @param dateTime : la date/heure de l'élément à ajouter
     * @return l'évènement en cours de construction après l'ajout de l'élément désiré
     */
    //mess a check remarque 12
    private static DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4)
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter();
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        wholeTextCalendar.append(name.toString())
                         .append(":")
                         .append(fmt.format(dateTime))
                         .append("\r\n");
        return this;
    }

    /**
     * Commence un composant en ajoutant une ligne dont le nom est BEGIN et la valeur est le nom du composant donné
     *
     * @param component : la valeur du composant donné
     * @return l'évènement en cours de construction après avoir commencé le composant
     */
    public IcalBuilder begin(Component component) {
        wholeTextCalendar.append("BEGIN:")
                         .append(component.toString())
                         .append("\r\n");
        currentComponents.add(component);
        return this;
    }

    /**
     * Termine le dernier composant qui a été commencé précédemment par begin mais pas encore terminé par un appel à end précédent
     *
     * @throws IllegalArgumentException s'il n'y a pas de composant commencé
     * @return l'évènement en cours de construction après avoir terminé le composant
     */
    public IcalBuilder end() {
        Preconditions.checkArgument(!currentComponents.isEmpty());
        wholeTextCalendar.append("END:")
                         .append(currentComponents.get(currentComponents.size() - 1)
                         .toString());
        wholeTextCalendar.append("\r\n");
        currentComponents.removeLast();
        return this;
    }

    /**
     * Retourne la chaîne de caractères au format iCalendar représentant l'événement en cours de construction
     *
     * @throws IllegalArgumentException si un composant qui a été commencé par un appel à begin n'a pas été terminé par un appel à end
     * @return l'évènement en cours de construction sous le format texte
     */
    public String build() {
        Preconditions.checkArgument(currentComponents.isEmpty());
        return wholeTextCalendar.toString();
    }
}
