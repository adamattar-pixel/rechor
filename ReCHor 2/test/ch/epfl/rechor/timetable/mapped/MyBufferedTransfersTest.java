package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyBufferedTransfersTest {
    @Test
    void WorkClassicExemple() {
        //System.out.println(PackedRange.pack());
        byte[] bite = {00, 03, 00, 02, 20, 00, 05, 00, 02, 30, 00, 02, 00, 01, 07, 00, 40, 00, 00, 77};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        BufferedTransfers bt = new BufferedTransfers(buffer);
        assertEquals(4, bt.size());
        assertEquals(3, bt.depStationId(0));
        assertEquals(5, bt.depStationId(1));
        assertEquals(2, bt.depStationId(2));
        assertEquals(40, bt.depStationId(3));
        assertEquals(20, bt.minutes(0));
        assertEquals(30, bt.minutes(1));
        assertEquals(07, bt.minutes(2));
        assertEquals(77, bt.minutes(3));
        assertEquals(PackedRange.pack(0, 2), bt.arrivingAt(02));
        assertEquals(PackedRange.pack(2, 3), bt.arrivingAt(01));
        assertEquals(PackedRange.pack(3, 4), bt.arrivingAt(00));
        assertEquals(30, bt.minutesBetween(05, 02));
        assertEquals(07, bt.minutesBetween(02, 01));
        assertEquals(77, bt.minutesBetween(40, 00));
    }

    @Test
    void WorkClassicExemple2() {
        byte[] bite = {99, 03, 99, 00, 20};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        BufferedTransfers bt = new BufferedTransfers(buffer);
        assertEquals(PackedRange.pack(0, 0), bt.arrivingAt(00));
    }

}
