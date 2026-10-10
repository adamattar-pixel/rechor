package ch.epfl.rechor;


import org.junit.jupiter.api.Test;

import static ch.epfl.rechor.Bits32_24_8.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyBits32_24_8Test {

    @Test
    void packWorksWithNonPermittedBits() {
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0));
        assertThrows(IllegalArgumentException.class, () -> pack(0b100000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1000000000000000000000000, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1111111111111111111111111, 0));
        assertThrows(IllegalArgumentException.class, () -> pack(0b10000000000000000000000000, 0b1));
        assertThrows(IllegalArgumentException.class, () -> pack(0b1, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b11111111111111111111111111, 0b100000000));
        assertThrows(IllegalArgumentException.class, () -> pack(0b11111111111111111111111111, 0b0));
        assertThrows(IllegalArgumentException.class, () -> pack(0b11010010, 0b101010101010100));
    }

    @Test
    void packWorksWithTrivialBits() {
        assertEquals(0b11111111111111111111111111111111, pack(0b111111111111111111111111, 0b11111111));
        assertEquals(0b100000000, pack(0b1, 0));
        assertEquals(0b11001100110011001100110011001100, pack(0b110011001100110011001100, 0b11001100));
        assertEquals(0b111100000000, pack(0b1111, 0));
        assertEquals(0b10101010101010101010101000001010, pack(0b101010101010101010101010, 0b1010));
        assertEquals(0b100000000, pack(0b1, 0));
        assertEquals(0b1001001100001000, pack(0b10010011, 0b1000));
    }

    @Test
    void unpack24WorksWithTrivialBits() {
        assertEquals(0b000000001111111110101010, unpack24(0b00000000111111111010101001010101));
        assertEquals(0b000000000000000000000000, unpack24(0b0000000000000000000000000000000));
        assertEquals(0b000000001111111111111111, unpack24(0b00000000111111111111111111111111));
        assertEquals(0b000100100011010011111111, unpack24(0b00010010001101001111111110101010));
        assertEquals(0b1, unpack24(0b100000000));
    }

    @Test
    void unpack8WorksWithTrivialBits() {
        assertEquals(0b01010101, unpack8(0b00000000111111111010101001010101));
        assertEquals(0b0, unpack8(0b00000000000000000000000000000000));
        assertEquals(0b11111111, unpack8(0b00000000111111111111111111111111));
        assertEquals(0b10101010, unpack8(0b00010010001101001111111110101010));
        assertEquals(0b10101010, unpack8(0b1111110101010));
    }
}
