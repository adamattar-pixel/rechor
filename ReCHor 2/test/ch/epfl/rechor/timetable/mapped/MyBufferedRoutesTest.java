package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class MyBufferedRoutesTest {

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
    void vehicleWorks() {
        byte[] bite = {99, 99, 01, 99, 99, 02, 99, 99, 05, 99, 99, 00};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        BufferedRoutes bf = new BufferedRoutes(example(), buffer);
        assertEquals(Vehicle.METRO, bf.vehicle(0));
        assertEquals(Vehicle.TRAIN, bf.vehicle(1));
        assertEquals(Vehicle.AERIAL_LIFT, bf.vehicle(2));
        assertEquals(Vehicle.TRAM, bf.vehicle(3));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.vehicle(7));
    }

    @Test
    void nameWorks() {
        byte[] bite = {00, 00, 99, 00, 06, 99, 00, 04, 99, 00, 03, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        BufferedRoutes bf = new BufferedRoutes(example(), buffer);
        assertEquals("1", bf.name(0));
        assertEquals("Palézieux", bf.name(1));
        assertEquals("Lausanne", bf.name(2));
        assertEquals("Ins", bf.name(3));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.name(7));
    }

    @Test
    void sizeWorks() {
        byte[] bite = {00, 00, 99, 00, 06, 99, 00, 04, 99, 00, 03, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        BufferedRoutes bf = new BufferedRoutes(example(), buffer);
        assertEquals(4, bf.size());
        byte[] bite2 = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bite2);
        BufferedRoutes bf2 = new BufferedRoutes(example(), buffer2);
        assertEquals(0, bf2.size());
    }
}
