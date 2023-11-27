package chessai.chessai.lib;

/**
 * Represents a square on the board.
 */
public class Square {

    /**
     * 16 bits: |--file--||--row--|
     * file: 0 = A
     * row: 0 = 1
     */
    private final short squareData;

    public Square(int file, int row) {
        this.squareData = (short) (((file & 0xFF) << 8) | (row & 0xFF));
    }

    public static String toString(int index) {
        return toString(index, true);
    }

    public static String toString(int index, boolean upperCase) {
        return String.valueOf((char) ((upperCase ? 'A' : 'a') + getFile(index))) + (getRow(index) + 1);
    }

    /**
     * Gets the index of the square on the board.
     * 0 = A8,
     * 1 = B8,
     * ...,
     * 8 = A7,
     * ...,
     * 63 = H1
     *
     * @return the index
     */
    public int getIndex() {
        int result = (squareData >>> 8) + ((7 - (squareData & 0xFF)) * 8);
        if (result > 63 || result < 0)
            return -1;
        return result;
    }

    public static int getRow(int index) {
        return 7 - (index / 8);
    }

    public static int getFile(int index) {
        return index % 8;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean upperCase) {
        return toString(getIndex(), upperCase);
    }

    public static int getIndex(String name) {
        int file = Character.toLowerCase(name.charAt(0)) - 'a';
        int row = name.charAt(1) - '1';

        if (file > 7 || file < 0 || row > 7 || row < 0)
            return -1;

        return file + ((7 - row) * 8);
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

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Square otherSquare)) return false;
        return otherSquare.squareData == this.squareData;
    }

    @Override
    public int hashCode() {
        return squareData;
    }

    public int row() {
        return (squareData) & 0xFF;
    }

    public int file() {
        return (squareData >>> 8) & 0xFF;
    }
}
