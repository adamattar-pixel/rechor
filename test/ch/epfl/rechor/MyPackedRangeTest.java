package ch.epfl.rechor;


import org.junit.jupiter.api.Test;
import static ch.epfl.rechor.PackedRange.pack;
import static org.junit.jupiter.api.Assertions.*;

public class MyPackedRangeTest {
    @Test
    void PackedRangePackCrash(){
        assertThrows(IllegalArgumentException.class, () -> pack(8388608 , 2));
        assertThrows(IllegalArgumentException.class, () -> pack(3 , 300));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0));
        assertDoesNotThrow(() -> pack(0b100000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1111111111111111111111111, 0));
        assertThrows(IllegalArgumentException.class, () -> pack(0b10000000000000000000000000, 0b1));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1, 0b1000000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b11111111111111111111111111, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b11111111111111111111111111, 0b0));
        assertThrows(IllegalArgumentException.class, () -> pack(0b11010010, 0b101010101010100));
    }

    @Test
    void PackedRangePackWork(){
        assertEquals(262 , pack(1 , 7));
    }

    @Test
    void PackedRangeEndExclusiveWorks(){
        assertEquals( 7, PackedRange.endExclusive(0b00000000000000000000001000000101));
        assertEquals( 55, PackedRange.endExclusive(0b00000000000000000011001000000101));
        assertEquals( 10, PackedRange.endExclusive(10));
        assertEquals( 255, PackedRange.endExclusive(0b00000000000000000000000011111111));
        assertEquals( 16777216, PackedRange.endExclusive(0b11111111111111111111111100000001));
        assertEquals( 16777470, PackedRange.endExclusive(0b11111111111111111111111111111111));
    }
}
