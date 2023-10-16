package chessai.chessai.lib;

public class Square {

    /**
     * 16 bits: |--file--||--row--|
     * file: 0 = A
     * row: 0 = 1
     */
    private final short squareData;

    public Square(int file, int row) {
        if (file > 7 || file < 0 || row > 7 || row < 0)
            throw new IllegalStateException("File or row is not valid!");

        this.squareData = (short) (((file & 0xFF) << 8) | (row & 0xFF));
    }

    public int getIndex() {
        return (squareData >>> 8) + ((7 - (squareData & 0xFF)) * 8);
    }

    @Override
    public String toString() {
        return String.valueOf((char) ('A' + (squareData >>> 8) & 0xFF)) + (((squareData) & 0xFF) + 1);
    }

    public static int getIndex(int file, int row) {
        if (file > 7 || file < 0 || row > 7 || row < 0)
            return -1;

        return file + ((7 - row) * 8);
    }

    public Square(String name) {
        this(Character.toLowerCase(name.charAt(0)) - 'a', name.charAt(1) - '1');
    }

    public Square(int index) {
        this(index % 8, 7 - (index / 8));
    }

    public Square copy() {
        return new Square(getIndex());
    }

    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Square otherSquare)) return false;
        return otherSquare.squareData == this.squareData;
    }

    public int row() {
        return (squareData) & 0xFF;
    }

    public int file() {
        return (squareData >>> 8) & 0xFF;
    }
}
