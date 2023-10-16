package chessai.chessai.lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    }
}