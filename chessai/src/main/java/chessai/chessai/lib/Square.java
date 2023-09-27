package chessai.chessai.lib;


/**
 * @param file 0 indexed, 0 = A
 * @param row 0 indexed, 0 = 1
 */
public record Square(int file, int row) {
    public int getIndex() {
        return file + (7 - row) * 8;
    }
    @Override
    public String toString () {
        return "%s%d".formatted(String.valueOf((char) ('a' + file)), row + 1);
    }

    public Square (String name) {
        this( name.charAt(0) - 'a', name.charAt(1) - '1');
    }
    public Square (int index) {
        this (index % 8, 7 - index / 8);
    }
    public Square copy() {
        return new Square(getIndex());
    }

    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Square otherSquare)) return false;
        return otherSquare.row == row && otherSquare.file == file;
    }
}
