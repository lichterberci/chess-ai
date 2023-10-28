package chessai.chessai.lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitMapTest {

    @Test
    void getBit() {
        for (int i = 0; i < 64; i++) {
            assertTrue(new BitMap(0).setBit(i, true).getBit(i));
        }
    }

    @Test
    void shift() {

        BitMap base = new BitMap(0).setBit(new Square("D4").getIndex(), true);

        assertTrue(base.shift(1, 0).getBit(new Square("E4").getIndex()));
        assertTrue(base.shift(4, 0).getBit(new Square("H4").getIndex()));
        assertTrue(base.shift(0, 2).getBit(new Square("D6").getIndex()));
        assertTrue(base.shift(3, 3).getBit(new Square("G7").getIndex()));
        assertTrue(base.shift(-3, -3).getBit(new Square("A1").getIndex()));
        assertFalse(base.shift(-3, -4).getBit(new Square("A1").getIndex()));
        assertTrue(base.shift(4, -2).getBit(new Square("H2").getIndex()));

        BitMap whiteSquares = new BitMap(0);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                whiteSquares.setBitInPlace(i + j * 8, (i + j) % 2 == 0);
            }
        }

        assertEquals(16, whiteSquares.shift(4, 0).getIndexesOfOnes().size());
        assertEquals(16, whiteSquares.shift(-4, 0).getIndexesOfOnes().size());
        assertEquals(16, whiteSquares.shift(0, 4).getIndexesOfOnes().size());
        assertEquals(16, whiteSquares.shift(0, -4).getIndexesOfOnes().size());
    }

    @Test
    void getIndexesOfOnes() {

        BitMap bitMap = new BitMap(0);

        for (int i = 0; i < 64; i++) {
            if (i % 2 == 0)
                bitMap.setBitInPlace(i, true);
        }

        assertEquals(32, bitMap.getIndexesOfOnes().size());

    }
}