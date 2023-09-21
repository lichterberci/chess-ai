package chessai.chessai.lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @Test
    void toIndex() {
        for (int i = 0; i < 64; i++) {
            Square square = new Square(i);
            assertEquals(i, square.getIndex());
        }
    }

    @Test
    void testToString() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String name = "%s%d".formatted(String.valueOf((char)(('A') + i)), j + 1);
                Square square = new Square(name);
                assertEquals(name, square.toString());
            }
        }
    }
}