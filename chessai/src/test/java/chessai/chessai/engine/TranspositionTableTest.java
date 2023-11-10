package chessai.chessai.engine;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TranspositionTableTest {

    @Test
    void put() {

        var table = new TranspositionTable(1024);

        table.put(10, -1);

        assertEquals((long) -1 << 32 | 10, table.table[10]);
    }

    @Test
    void get() throws InvalidKeyException {

        var table = new TranspositionTable(1024);

        table.put(10, -1);
        assertEquals(-1, table.get(10));

        // this will clash with 10
        table.put(138, -1);
        assertEquals(-1, table.get(138));

        table.put(-100, 20);
        assertEquals(20, table.get(-100));
    }

    @Test
    void contains() {
    }

    @Test
    void clear() {
    }
}