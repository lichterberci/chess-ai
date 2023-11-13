package chessai.chessai.engine;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;

import static org.junit.jupiter.api.Assertions.*;

class TranspositionTableTest {

    @Test
    void putAndGet() throws InvalidKeyException {

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

        var table = new TranspositionTable(1024);

        table.put(10, -1);
        assertTrue(table.contains(10));
        assertFalse(table.contains(9));

        table.put(138, -1);
        assertTrue(table.contains(138));
        assertTrue(table.contains(10));
        assertFalse(table.contains(-1));

        table.put(-100, 20);
        assertTrue(table.contains(-100));
        assertTrue(table.contains(138));
        assertTrue(table.contains(10));
        assertFalse(table.contains(0));

    }
}