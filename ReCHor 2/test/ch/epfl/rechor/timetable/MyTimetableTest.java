package ch.epfl.rechor.timetable;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MyTimetableTest implements TimeTable {
    @Test
    void isStationIdWorks() {
        assertFalse(isStationId(5));
        assertFalse(isStationId(10));
        assertFalse(isStationId(-1));
        assertTrue(isStationId(0));
        assertTrue(isStationId(1));
        assertTrue(isStationId(2));
        assertTrue(isStationId(3));
        assertTrue(isStationId(4));
    }
    @Test
    void isPlatformIdWorks() {
        assertFalse(isPlatformId(-1));
        assertFalse(isPlatformId(0));
        assertFalse(isPlatformId(1));
        assertFalse(isPlatformId(2));
        assertFalse(isPlatformId(3));
        assertFalse(isPlatformId(4));
        assertTrue(isPlatformId(5));
        assertTrue(isPlatformId(6));
        assertTrue(isPlatformId(7));
        assertTrue(isPlatformId(8));
        assertTrue(isPlatformId(9));
        assertFalse(isPlatformId(10));
        assertFalse(isPlatformId(11));
        assertFalse(isPlatformId(12));
        assertFalse(isPlatformId(13));
        assertFalse(isPlatformId(14));
    }

    @Test
    void stationIdWorks() {
        assertEquals(0, stationId(0));
        assertEquals(1, stationId(1));
        assertEquals(2, stationId(2));
        assertEquals(3, stationId(3));
        assertEquals(4, stationId(4));
        assertEquals(2, stationId(5));
        assertEquals(4, stationId(6));
        assertEquals(1, stationId(7));
        assertEquals(0, stationId(8));
        assertEquals(3, stationId(9));
    }

    @Test
    void platformNameWorks() {
        assertNull(platformName(0));
        assertNull(platformName(1));
        assertNull(platformName(2));
        assertNull(platformName(3));
        assertNull(platformName(4));
        assertEquals("Ouchy", platformName(5));
        assertEquals("Lausanne", platformName(6));
        assertEquals("Flon", platformName(7));
        assertEquals("Renens VD", platformName(8));
        assertEquals("St Sulpice", platformName(9));
    }

    @Test
    void stationIdThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stationId(10);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stationId(11);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stationId(12);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stationId(13);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stationId(-1);
        });
    }

    @Test
    void platformNameThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            platformName(10);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            platformName(11);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            platformName(12);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            platformName(13);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            platformName(-1);
        });
    }

    @Override
    public Stations stations() {
        return new StatTest();
    }

    @Override
    public Platforms platforms() {
        return new PlatformTest();
    }

    public class StatTest implements Stations {
        private String[] name = {"Ouchy", "Lausanne", "Flon", "Renens VD", "St Sulpice"};
        private double[] longitude = {15, 12, 6, 18, 19};
        private double[] latitude = {25, 5, 14, 4, 20};


        @Override
        public String name(int id) {
            if (id >= name.length) {
                throw new IndexOutOfBoundsException();
            }
            return name[id];
        }

        @Override
        public double longitude(int id) {
            if (id >= longitude.length) {
                throw new IndexOutOfBoundsException();
            }
            return longitude[id];
        }

        @Override
        public double latitude(int id) {
            if (id > latitude.length) {
                throw new IndexOutOfBoundsException();
            }
            return latitude[id];
        }

        @Override
        public int size() {
            return name.length;
        }
    }

    public class PlatformTest implements Platforms {
        private String[] name = {"Ouchy", "Lausanne", "Flon", "Renens VD", "St Sulpice"};
        private int[] stationId = {2, 4, 1, 0 ,3};

        @Override
        public String name(int id) {
            if (id > name.length) {
                throw new IndexOutOfBoundsException();
            }
            return name[id];
        }

        @Override
        public int stationId(int id) {
            if (id > stationId.length) {
                throw new IndexOutOfBoundsException();
            }
            return stationId[id];
        }

        @Override
        public int size() {
            return name.length;
        }
    }

    @Override
    public StationAliases stationAliases() {
        return null;
    }

    @Override
    public Routes routes() {
        return null;
    }

    @Override
    public Transfers transfers() {
        return null;
    }

    @Override
    public Trips tripsFor(LocalDate date) {
        return null;
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        return null;
    }
}
