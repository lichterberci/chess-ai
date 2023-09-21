package chessai.chessai.lib;

import org.jetbrains.annotations.Contract;

public record Square(int file, int row) {
    public int toIndex() {
        return row * 8 + file;
    }
    public String toName () {
        return "%s%d".formatted(String.valueOf((char) ('A' + file)), (row + 1));
    }

    public Square (String name) {
        this( name.charAt(0) - 'A', name.charAt(1) - '1');
    }
}
