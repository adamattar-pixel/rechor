package ch.epfl.rechor.journey;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;

import static ch.epfl.rechor.journey.JourneyIcalConverter.toIcalendar;

public class MyJourneyGeoJsonConverterTest {
    @Test
    public void test() {
        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        var s1 = new Stop("A", null, 7, 47);
        var s2 = new Stop("B", null, 8, 47);
        var s3 = new Stop("C", null, 10, 25);
        var dT = d.atTime(8, 0);
        var dT2 = d.atTime(8, 10);
        var dT3 = d.atTime(8, 15);
        var l = (Journey.Leg) new Journey.Leg.Foot(s1, dT, s2, dT.plusMinutes(5));
        var t = (Journey.Leg) new Journey.Leg.Transport(s2, dT2, s3, dT3, List.of(), Vehicle.TRAIN, "IC 1", "Rorschach");
        List<Journey.Leg> immutableLegs = List.of(l, t);
        var j = new Journey(immutableLegs);
        JourneyGeoJsonConverter converter = new JourneyGeoJsonConverter();
        System.out.println(converter.toGeoJson(j));
        System.out.println(toIcalendar(j));
    }

    @Test
    public void test2() {
        var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);
        var s4 = new Stop("Lausanne", "5", 6.629092, 46.516792);
        var s5 = new Stop("Lausanne", "1", 6.629092, 46.516792);
        var s6 = new Stop("Romont FR", "2", 6.911811, 46.693508);
        var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);

        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        var l1 = new Journey.Leg.Transport(
                s1,
                d.atTime(16, 13),
                s2,
                d.atTime(16, 19),
                List.of(),
                Vehicle.METRO,
                "m1",
                "Renens VD, gare");

        var l2 = new Journey.Leg.Foot(s2, d.atTime(16, 19), s3, d.atTime(16, 22));

        var l3 = new Journey.Leg.Transport(
                s3,
                d.atTime(16, 26),
                s4,
                d.atTime(16, 33),
                List.of(),
                Vehicle.TRAIN,
                "R4",
                "Bex");

        var l4 = new Journey.Leg.Foot(s4, d.atTime(16, 33), s5, d.atTime(16, 38));

        var l5 = new Journey.Leg.Transport(
                s5,
                d.atTime(16, 40),
                s6,
                d.atTime(17, 13),
                List.of(),
                Vehicle.TRAIN,
                "IR15",
                "Luzern");

        Journey temp = new Journey(List.of(l1, l2, l3, l4, l5));
        System.out.println(JourneyGeoJsonConverter.toGeoJson(temp));
        System.out.println(toIcalendar(temp));
    }
}
