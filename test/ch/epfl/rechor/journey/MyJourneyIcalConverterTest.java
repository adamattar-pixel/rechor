package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static ch.epfl.rechor.journey.JourneyIcalConverter.toIcalendar;

public class MyJourneyIcalConverterTest {
    @Test
    void JourneyIcalConverterWorksWithTrivialJourney() {
        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        var s1 = new Stop("A", null, 7, 47);
        var s2 = new Stop("B", null, 7, 47);
        var s3 = new Stop("C", null, 7, 47);
        var dT = d.atTime(8, 0);
        var dT2 = d.atTime(8, 10);
        var dT3 = d.atTime(8, 15);
        var l = (Journey.Leg) new Journey.Leg.Foot(s1, dT, s2, dT.plusMinutes(5));
        var t = (Journey.Leg) new Journey.Leg.Transport(s2, dT2, s3, dT3, List.of(), Vehicle.TRAIN, "IC 1", "Rorschach");
        List<Journey.Leg> immutableLegs = List.of(l, t);
        var j = new Journey(immutableLegs);
        //System.out.println(toIcalendar(j));
    }

    //@Test
}
