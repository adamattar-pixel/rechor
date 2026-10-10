package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedConnectionsTest {

    @Test
    void depStopIdWorks() {
        byte[] bite = {00, 00, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 0b01111000, (byte) 0b10101010, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        assertEquals(0, bf.depStopId(0));
        assertEquals(0b01111000_10101010, bf.depStopId(1));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.depStopId(9));
    }

    @Test
    void depMinsWorks() {
        byte[] bite = {99, 99, 00, 00, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 0b01111000, (byte) 0b10101010, 99, 99, 99, 99, 99, 99, 99, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        assertEquals(0, bf.depMins(0));
        assertEquals(0b01111000_10101010, bf.depMins(1));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.depMins(9));
    }

    @Test
    void arrStopIdWorks() {
        byte[] bite = {99, 99, 99, 99, 00, 00, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 0b01111000, (byte) 0b10101010, 99, 99, 99, 99, 99, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        assertEquals(0, bf.arrStopId(0));
        assertEquals(0b01111000_10101010, bf.arrStopId(1));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.arrStopId(9));
    }

    @Test
    void arrMinsWorks() {
        byte[] bite = {99, 99, 99, 99, 99, 99, 00, 00, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 0b01111000, (byte) 0b10101010, 99, 99, 99, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        assertEquals(0, bf.arrMins(0));
        assertEquals(0b01111000_10101010, bf.arrMins(1));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.arrMins(9));
    }

    @Test
    void tripIdWorks() {
        byte[] bite = {99, 99, 99, 99, 99, 99, 99, 99, 00, 00, 00, 99, 99, 99, 99, 99, 99, 99, 99, 99, 0b01111000, (byte) 0b10101010, (byte) 0b10101010, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        assertEquals(0, bf.tripId(0));
        assertEquals(0b01111000_10101010_10101010, bf.tripId(1));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.tripId(9));
    }

    @Test
    void tripPosWorks() {
        byte[] bite = {99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 00, 99, 99, 99, 99, 99, 99, 99, 99, 0b01111000, (byte) 0b10101010, (byte) 0b10101010, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        assertEquals(0, bf.tripPos(0));
        assertEquals(99, bf.tripPos(1));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.tripPos(9));
    }

    @Test
    void nextConnectionIdWorks() {
        byte[] bite = {};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {00, 00, 00, 10, 00, 00, 00, 20, 00, 00, 00, 30, 00, 00, 00, 40, 00, 00, 00, 50, 00, 00, 00, 60, 00, 00, 00, 70, 00, 00, 00, 80, 00, 00, 00, 90};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        assertEquals(10, bf.nextConnectionId(0));
        assertEquals(20, bf.nextConnectionId(1));
        assertEquals(30, bf.nextConnectionId(2));
        assertEquals(40, bf.nextConnectionId(3));
        assertEquals(50, bf.nextConnectionId(4));
        assertEquals(60, bf.nextConnectionId(5));
        assertEquals(70, bf.nextConnectionId(6));
        assertEquals(80, bf.nextConnectionId(7));
        assertEquals(90, bf.nextConnectionId(8));
        assertThrows(IndexOutOfBoundsException.class, () -> bf.nextConnectionId(9));
    }

    @Test
    void sizeWorks() {
        byte[] bite = {99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 00, 99, 99, 99, 99, 99, 99, 99, 99, 0b01111000, (byte) 0b10101010, (byte) 0b10101010, 99};
        ByteBuffer buffer = ByteBuffer.wrap(bite);
        byte[] bites = {};
        ByteBuffer buffer2 = ByteBuffer.wrap(bites);
        BufferedConnections bf = new BufferedConnections(buffer, buffer2);
        byte[] bitei = {};
        ByteBuffer bufferi = ByteBuffer.wrap(bitei);
        byte[] bitesi = {00, 00, 00, 10, 00, 00, 00, 20, 00, 00, 00, 30, 00, 00, 00, 40, 00, 00, 00, 50, 00, 00, 00, 60, 00, 00, 00, 70, 00, 00, 00, 80, 00, 00, 00, 90};
        ByteBuffer buffer2i = ByteBuffer.wrap(bitesi);
        BufferedConnections bfi = new BufferedConnections(bufferi, buffer2i);
        assertEquals(2, bf.size());
        assertEquals(0, bfi.size());
    }
}
