package chessai.chessai.engine;

import chessai.chessai.lib.Board;

import java.security.InvalidKeyException;

/**
 * Implements a transposition table using Zobrist hashing
 */
public class TranspositionTable {

    public final int capacity;
    /**
     * we store the value in the first 32 bit and the key in the second 32 bit
     */
    public final long[] table;

    public TranspositionTable() {
        this(100_000);
    }

    public TranspositionTable(int capacityInBytes) {
        this.capacity = capacityInBytes / 8;

        table = new long[this.capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = 0;
        }
    }

    public void put(Board board, int eval) {
        put(board.hashCode(), eval);
    }

    public void put(int hash, int eval) {

        int index = getInitialIndex(hash);

        while (table[index] != 0) {
            index++;
            if (index >= capacity)
                index -= capacity;
        }

        table[index] = ((long) eval << 32) | ((long) hash & 0xFFFF_FFFFL);
    }

    public int get(Board board) throws InvalidKeyException {
        return get(board.hashCode());
    }

    public int get(int hash) throws InvalidKeyException {
        for (int i = getInitialIndex(hash); table[i] != 0; i = Integer.remainderUnsigned(i + 1, capacity)) {
            if ((int) (table[i] & 0xFFFF_FFFFL) == hash)
                return (int) (table[i] >>> 32);
        }

        throw new InvalidKeyException("Key does not exist!");
    }

    public boolean contains(Board board) {
        int hash = board.hashCode();

        for (int i = getInitialIndex(hash); table[i] != 0; i = Integer.remainderUnsigned(i + 1, capacity)) {
            if ((int) (table[i] & 0xFFFF_FFFFL) == hash)
                return true;
        }

        return false;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = 0;
        }
    }

    private int getInitialIndex(int hash) {
        return Integer.remainderUnsigned(hash, capacity);
    }
}
