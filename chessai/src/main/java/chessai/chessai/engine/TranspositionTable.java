package chessai.chessai.engine;

import chessai.chessai.lib.Board;

/**
 * Implements a transposition table using Zobrist hashing
 */
public class TranspositionTable {

    private final int capacity;
    private final int[] table;

    public TranspositionTable() {
        this(100_000);
    }

    public TranspositionTable(int capacityInBytes) {
        this.capacity = capacityInBytes / 4;

        table = new int[this.capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = -1;
        }
    }

    public void put(Board board, int eval) {
        int index = getIndex(board);
        table[index] = eval;
    }

    public int get(Board board) {
        int index = getIndex(board);
        return table[index];
    }

    public boolean contains(Board board) {
        int index = getIndex(board);
        return table[index] != -1;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = -1;
        }
    }

    private int getIndex(Board board) {
        return Integer.remainderUnsigned(board.hashCode(), capacity);
    }
}
