package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

class AdRouterTest {

    //example etape 1
    @Test
    void profileWorks() throws IOException {

        TimeTable TT = FileTimeTable.in(Path.of("timetable"));
        Router router = new Router(TT);
        Profile profile = router.profile(LocalDate.of(2025, 5, 15), stationId(TT.stations(), "Gruyères"));
        //ParetoFront front = profile.forStation(stationId(TT.stations(), "Ecublens VD, EPFL"));
        //String f = front.toString();
        //assertTrue(f.contains("16h11, 17h59") && f.contains("16h36, 18h30") && f.contains("16h51, 18h53"));
        List<Journey> journeys = JourneyExtractor.journeys(profile, stationId(TT.stations(), "Ecublens VD, EPFL"));






    }



    //Test donné par le prof
    @Test
    void testGlobal() throws IOException {
        long tStart = System.nanoTime();

        TimeTable timeTable = new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));

        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.APRIL, 1);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        Journey journey = JourneyExtractor
                .journeys(profile, depStationId)
                .get(32);

        double elapsed = (System.nanoTime() - tStart) * 1e-9;

        assertTrue(elapsed < 2.5);

        System.out.printf("Temps écoulé : %.3f s%n", elapsed);
        assertEquals( "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\n" +
                "DESCRIPTION:16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\ntrajet \n" +
                " à pied (3 min)\\n16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\nc\n" +
                " hangement (5 min)\\n16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\n" +
                " \\nchangement (3 min)\\n17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\n" +
                " \\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(journey));
    }


    //Trajet CFF Chavannes Concorde à Ouchy
    @Test
    void comparingToCFF() throws IOException{
        TimeTable TT = new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));
        Router router = new Router(TT);
        int concorde = stationId(TT.stations(), "Chavannes-R., Concorde");
        int ouchy = stationId(TT.stations(), "Lausanne, Ouchy-Olympique");

        Profile p = router.profile(LocalDate.of(2025, 4, 24), ouchy);

        List<Journey> j = JourneyExtractor.journeys(p, concorde);

        j.forEach(i -> System.out.println(JourneyIcalConverter.toIcalendar(i)));

        //Some trips are identical to those of CFF, others are very different. Weird.
    }


    @Test
    void comparingProfile() throws IOException {
        TimeTable TT = new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));
        Router router = new Router(TT);
        Profile p = router.profile(LocalDate.of(2025, 3, 18), stationId(TT.stations(), "Gruyères"));


        try(Writer o = new FileWriter("testProfile");){
            for (int i = 0; i < TT.stations().size(); i++) {
                ParetoFront front = p.forStation(i);
                StringJoiner a = new StringJoiner(",");
                front.forEach( l -> a.add(Long.toHexString(l)));
                o.write(a.toString());
                o.write("\n");
            }
        }
        catch (IOException e){
            System.out.print("Error");
        }

    }
    public static int stationId(Stations stations, String name) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.name(i).equals(name)) return i;
        }
        return -1;
    }

    String splitString(Journey journey){
        List<String> actual = new ArrayList<>(List.of(JourneyIcalConverter.toIcalendar(journey).split("\\r\\n")));
        //remove the DTSTAMP and UID AND Dates to test only the journey computed by the CSA Algorithm
        actual.remove(7); actual.remove(6);
        actual.remove(5); actual.remove(4);
        return String.join("\n", actual);
    }

}