package ch.epfl.rechor.journey;


import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

import static ch.epfl.rechor.IcalBuilder.Component;
import static ch.epfl.rechor.IcalBuilder.Name;

/**
 * Classe qui permet de convertir un voyage en un évènement
 *
 * @author AMRAOUI Saad
 * @author ATTAR Adam
 */
public class JourneyIcalConverter {

    /**
     * Constructeur privé rendant la classe non instanciable
     */
    private JourneyIcalConverter() {
    }

    /**
     * Retourne une chaîne de caractères au format iCalendar représentant
     * l'événement qui correspond au voyage donné
     *
     * @param journey : le voyage que l'on désire convertir au format texte
     * @return une chaîne de caractères au format iCalendar représentant l'événement
     * qui correspond au voyage donné
     */
    public static String toIcalendar(Journey journey) {
        //Initialise l'IcalBuilder et ajoute tous les composants nécessaires à sa structure
        IcalBuilder textBuilding = new IcalBuilder();
        textBuilding.begin(Component.VCALENDAR)
                .add(Name.VERSION, "2.0")
                .add(Name.PRODID, "ReCHor")
                .begin(Component.VEVENT)
                .add(Name.UID, UUID.randomUUID().toString())
                .add(Name.DTSTAMP, LocalDateTime.now())
                .add(Name.DTSTART, journey.depTime())
                .add(Name.DTEND, journey.arrTime())
                .add(Name.SUMMARY, journey.depStop().name() + " → " + journey.arrStop().name());

        //Itère sur l'ensemble des étapes du trajet pour le texte de la description
        StringJoiner descriptionText = new StringJoiner("\\n");
        for (int i = 0; i < journey.legs().size(); i++) {
            switch (journey.legs().get(i)) {
                case Journey.Leg.Foot f -> descriptionText.add(FormatterFr.formatLeg(f));
                case Journey.Leg.Transport t -> descriptionText.add(FormatterFr.formatLeg(t));
            }
        }
        //rajoute le composant Description et ferme les composants VCalendar et Vevent
        textBuilding.add(Name.DESCRIPTION, descriptionText.toString())
                .end()
                .end();
        return textBuilding.build();
    }
}
