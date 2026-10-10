package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.TimeTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyFileTimeTable {

    List<String> example() throws IOException{
        Path testDirectory = Path.of("timetable");
        List<String> stringTable = List.copyOf(Files.readAllLines(testDirectory.resolve("strings.txt")));
        return stringTable;
    }


    @Test
    public void test() throws IOException {
        Path testDirectory = Path.of("timetable");
        TimeTable test1 = FileTimeTable.in(testDirectory);
        LocalDate localDateTime1 = LocalDate.of(2025, 3, 23);
        LocalDate localDateTime2 = LocalDate.of(2025, 3, 19);
        try (FileChannel tripsChannel = FileChannel.open(testDirectory.resolve("2025-03-23/trips.bin"));){
            assertEquals(new BufferedTrips(List.copyOf(example()), tripsChannel.map(READ_ONLY, 0, tripsChannel.size())).size() , test1.tripsFor(localDateTime1).size());
            assertEquals( 2607, test1.tripsFor(localDateTime1).routeId(0));
            assertEquals(1539 , test1.tripsFor(localDateTime1).routeId(1));
            assertEquals(1550 , test1.tripsFor(localDateTime1).routeId(4));
            assertEquals("Basel Bad Bf" , test1.tripsFor(localDateTime1).destination(0));
            assertEquals( 2595 , test1.tripsFor(localDateTime2).routeId(0));
            assertEquals( 2607 , test1.tripsFor(localDateTime2).routeId(1));
            assertEquals(Vehicle.TRAIN , test1.routes().vehicle(0));
            assertEquals(Vehicle.TRAIN , test1.routes().vehicle(1));
            assertEquals(Vehicle.TRAIN , test1.routes().vehicle(2));
            assertEquals("001A", test1.routes().name(0));
            assertEquals(2531, test1.platforms().stationId(0));
            assertEquals(33273, test1.platforms().stationId(1));
            assertEquals(0 , test1.transfers().depStationId(0));
            assertEquals(1 , test1.transfers().depStationId(1));
            assertEquals("Aach (Hegau), Aachquelle" , test1.stations().name(0));
            assertEquals("Aach (Hegau), Autohaus Gohm" , test1.stations().name(1));
            assertEquals(2700 , test1.connectionsFor(localDateTime1).depMins(0));
            assertEquals(1 , test1.connectionsFor(localDateTime1).nextConnectionId(0));
            assertEquals(0 , test1.connectionsFor(localDateTime2).nextConnectionId(1));
        } catch (IOException e) {

        }


    }
}
