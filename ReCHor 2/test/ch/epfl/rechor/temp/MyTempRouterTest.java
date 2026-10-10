package ch.epfl.rechor.temp;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.ParetoFront;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;

public class MyTempRouterTest {

    Profile readProfile(TimeTable timeTable, LocalDate date, int arrStationId) throws IOException {
        Path path =
                Path.of("timetable/profile_" + date + "_" + arrStationId + ".txt");
        try (BufferedReader r = Files.newBufferedReader(path)) {
            Profile.Builder profileB =
                    new Profile.Builder(timeTable, date, arrStationId);
            int stationId = -1;
            String line;
            while ((line = r.readLine()) != null) {
                stationId += 1;
                if (line.isEmpty()) continue;
                ParetoFront.Builder frontB = new ParetoFront.Builder();
                for (String t : line.split(","))
                    frontB.add(Long.parseLong(t, 16));
                profileB.setForStation(stationId, frontB);
            }
            return profileB.build();
        }
    }

    @Test
    void sameAsExample() throws IOException {
        TimeTable t = FileTimeTable.in(Path.of("timetable"));
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        Router r = new Router(t);
        Profile p = r.profile(date, 11486);
    }

    @Test
    void testDunRandom() throws IOException {
        TimeTable timeTable = new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));
        Stations stations = timeTable.stations();
        int test = stationId(stations, "Renens VD, gare");
        int arrivingat = timeTable.transfers().arrivingAt(test);
        for (int i = PackedRange.startInclusive(arrivingat); i < PackedRange.endExclusive(arrivingat); i ++){
            System.out.println("transfer nb: "+i + ": from: \"" + stations.name(timeTable.transfers().depStationId(i)) + "\" to: \"" + stations.name(test) + "\"");
            System.out.println("transfer nb: " + i + ": from \"" + timeTable.transfers().depStationId(i)  + "\" to: \"" + test + "\"");
            System.out.print(timeTable.transfers().minutes(i) + " ");
            System.out.println(timeTable.transfers().minutesBetween(timeTable.transfers().depStationId(i), test));
        }
    }

    private int stationId(Stations stations, String s) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.name(i).equals(s)) return i;
        }
        return -1;
    }
    /*  transfer nb: 27309: from: "Renens VD" to: "Renens VD, gare"
        transfer nb: 27309: from "22612" to: "22627"
        3 3
        transfer nb: 27310: from: "Renens VD, 14 Avril" to: "Renens VD, gare"
        transfer nb: 27310: from "22613" to: "22627"
        6 6
        transfer nb: 27311: from: "Renens VD, gare" to: "Renens VD, gare"
        transfer nb: 27311: from "22627" to: "22627"
        2 2
        transfer nb: 27312: from: "Renens VD, gare nord" to: "Renens VD, gare"
        transfer nb: 27312: from "22628" to: "22627"
        4 4
        transfer nb: 27313: from: "Renens VD, gare nord" to: "Renens VD, gare"
        transfer nb: 27313: from "22628" to: "22627"
        3 4
        transfer nb: 27314: from: "Renens VD, gare sud" to: "Renens VD, gare"
        transfer nb: 27314: from "22629" to: "22627"
        3 3
        transfer nb: 27315: from: "Renens VD, gare sud" to: "Renens VD, gare"
        transfer nb: 27315: from "22629" to: "22627"
        1 3
     */


}
