package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class MyStopIndexTest {

    @Test
    void proftest() {
        List<String> stations = List.of("Mézières FR, village", "Mézières VD, village", "Mézery-près-Donneloye, village", "Charleville-Mézières", "Lausanne");
        Map<String, String> mp = Map.of("Losanne", "Lausanne");
        StopIndex si = new StopIndex(stations, mp);
        List<String> ls = si.stopsMatching("mez vil", 5);
        System.out.println(si.score("Mézières FR, village", "mez vil"));
        System.out.println(si.score( "Mézières VD, village", "mez vil"));
        System.out.println(si.score( "Mézery-près-Donneloye, village", "mez vil"));
        System.out.println(si.score( "Charleville-Mézières", "mez vil"));
        System.out.println(si.score( "Lausanne, gare", "lausanne gare")); //680
        System.out.println(si.score("Lausanne", "los"));

        System.out.println(ls);
        List<String> ly = si.stopsMatching("los", 5);
        System.out.println(ly);
        List<String> lx = si.stopsMatching("Los", 5);
        System.out.println(lx);
        List<String> lz = si.stopsMatching("lOs", 5);
        System.out.println(lz);
    }
}
