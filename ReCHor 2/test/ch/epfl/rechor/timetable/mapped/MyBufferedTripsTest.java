package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyBufferedTripsTest {

    List<String> example() {
        List<String> stringTable = new ArrayList<>();
        stringTable.add("1");
        stringTable.add("70");
        stringTable.add("Anet");
        stringTable.add("Ins");
        stringTable.add("Lausanne");
        stringTable.add("Losanna");
        stringTable.add("Palézieux");
        return stringTable;
    }

    @Test
    void BufferedTripsWorks() {
        byte[] bite = {00, 03, 00, 04, 00, 07, 00, 00, 05, 99, 99, 00};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        BufferedTrips bf = new BufferedTrips(example(), buffer);
        assertEquals(3 , bf.routeId(0));
        assertEquals("Lausanne" , bf.destination(0));
        assertEquals(3 , bf.size());
        assertEquals(7 , bf.routeId(1));
        assertEquals("1" , bf.destination(1));
    }
}
