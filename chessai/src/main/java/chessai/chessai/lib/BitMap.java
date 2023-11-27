package chessai.chessai.lib;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Encapsulates a long that stores a 64 bit bitmap.
 * bit 0 (lsb) : A8
 * ...
 * bit 63 (msb) : H1
 */
public class BitMap {

    /**
     * Iterator used for iterating through the bits of a bitmap
     */
    static class BitMapIterator implements Iterator<Boolean> {
        private final BitMap bitMap;
        private byte index = 0;

        BitMapIterator(BitMap bitMap) {
            this.bitMap = bitMap;
        }

        @Override
        public boolean hasNext() {
            return index < 63;
        }

        @Override
        public Boolean next() throws NoSuchElementException {
            return bitMap.getBit(index++);
        }
    }

    /*

    bit 0 (lsb) : A8
    ...
    bit 63 (msb) : H1

     */
    private long data;

    public long getData() {
        return data;
    }

    public BitMap(boolean bitValue) {
        data = bitValue ? 0xFFFF_FFFF_FFFF_FFFFL : 0;
    }

    public BitMap(long data) {
        this.data = data;
    }

    public BitMap(String inputString) {
        this(inputString, true);
    }

    public BitMap(String inputString, boolean startWithZeroIndex) {
        if (inputString.length() != 64)
            throw new IllegalArgumentException("inputString must have 64 characters!");

        data = 0L;

        if (startWithZeroIndex) {
            for (int i = 0; i < 64; i++) {
                data |= (long) (inputString.charAt(i) == '1' ? 1 : 0) << i;
            }
        } else {
            for (int i = 63; i >= 0; i--) {
                data |= (long) (inputString.charAt(i) == '1' ? 1 : 0) << i;
            }
        }
    }

    public boolean getBit(int index) {
        return ((data >>> index) & 1L) == 1L;
    }

    public BitMap setBit(int index, boolean value) {
        long result = data;
        long mask = ~(1L << index);
        result &= mask;
        if (value)
            result |= 1L << index;
        return new BitMap(result);
    }

    /**
     * @param rowIndex 0 = row 8, 1 = row 7, ..., 7 = row 1
     */
    public byte getRow(int rowIndex) {
        return (byte) (data & (0xFFL << rowIndex));
    }

    /**
     * @param fileIndex 0 = A, 1 = B, ..., 7 = H
     */
    public byte getFile(int fileIndex) {
        return (byte) (data & (0x0101_0101_0101_0101L << fileIndex));
    }

    public BitMap shiftFilesLeft(int offset) {

        long result = data;

        // delete bits from the files, we want to remove
        for (byte i = 0; i < offset; i++)
            // mask for every row: 11111110
            result = result & (0xFEFE_FEFE_FEFE_FEFEL << i);

        // shift the whole thing
        result >>>= offset; // >>> inserts a 0

        return new BitMap(result);
    }

    public BitMap shiftFilesRight(int offset) {

        long result = data;

        // delete bits from the files, we want to remove
        for (byte i = 0; i < offset; i++)
            // mask for every row: 01111111
            result = result & (0x7F7F_7F7F_7F7F_7F7FL >>> i);

        // shift the whole thing
        result <<= offset;

        return new BitMap(result);
    }

    public BitMap shiftRowsDown(int offset) {

        long result = data;

        result <<= ((long) offset << 3); // offset * 8

        return new BitMap(result);
    }

    public BitMap shiftRowsUp(int offset) {

        long result = data;

        result >>>= ((long) offset << 3); // offset * 8

        return new BitMap(result);
    }

    public BitMap shift(int fileOffset, int rowOffset) {

        BitMap fileShiftedBitMap = this;

        if (fileOffset != 0)
            fileShiftedBitMap = fileOffset > 0 ? shiftFilesRight(fileOffset) : shiftFilesLeft(-fileOffset);

        @SuppressWarnings("UnnecessaryLocalVariable")
        BitMap rowShiftedBitMap = rowOffset > 0 ? fileShiftedBitMap.shiftRowsUp(rowOffset) : fileShiftedBitMap.shiftRowsDown(-rowOffset);

        return rowShiftedBitMap;
    }

    public Iterator<Boolean> iterator() {
        return new BitMapIterator(this);
    }

    public List<Integer> getIndexesOfOnes() {

        LinkedList<Integer> result = new LinkedList<>();

        long shiftedData = data;
        for (int i = 0; i < 64; i++) {
            if ((shiftedData & 1L) != 0)
                result.add(i);
            shiftedData >>>= 1;
        }

        return result;
    }

    public int getFirstIndexOfOne() {
        long shiftedData = data;
        for (int i = 0; i < 64; i++) {
            if ((shiftedData & 1L) != 0)
                return i;
            shiftedData >>>= 1;
        }
        return -1;
    }

    public BitMap and(BitMap other) {
        return new BitMap(data & other.data);
    }

    public BitMap or(BitMap other) {
        return new BitMap(data | other.data);
    }

    public BitMap xor(BitMap other) {
        return new BitMap(data ^ other.data);
    }

    public BitMap and(boolean value) {
        return new BitMap(data & (value ? 0xFFFF_FFFF_FFFF_FFFFL : 0L));
    }

    public BitMap or(boolean value) {
        return new BitMap(data | (value ? 0xFFFF_FFFF_FFFF_FFFFL : 0L));
    }

    public BitMap xor(boolean value) {
        return new BitMap(data ^ (value ? 0xFFFF_FFFF_FFFF_FFFFL : 0L));
    }

    public BitMap invert() {
        return new BitMap(~data);
    }

    public BitMap copy() {
        return new BitMap(data);
    }

    public boolean isNonZero() {
        return data != 0;
    }

    public boolean isZero() {
        return data == 0;
    }

    public void setBitInPlace(int index, boolean value) {
        long result = data;
        long mask = ~(1L << index);
        result &= mask;
        if (value)
            result |= 1L << index;
        data = result;
    }

    public void orInPlace(BitMap other) {
        data |= other.data;
    }

    public void andInPlace(BitMap other) {
        data &= other.data;
    }

    public void xorInPlace(BitMap other) {
        data ^= other.data;
    }

    public static BitMap getLineThroughSquares(int index1, int index2) {

        int row1 = Square.getRow(index1);
        int row2 = Square.getRow(index2);
        int file1 = Square.getFile(index1);
        int file2 = Square.getFile(index2);

        int deltaRow = row1 - row2;
        int deltaFile = file1 - file2;

        int absDeltaRow = Math.abs(deltaRow);
        int absDeltaFile = Math.abs(deltaFile);

        BitMap result = new BitMap(0);

        if (absDeltaRow == 0) {
            // it is a file
            for (int i = 0; i < 8; i++) {
                result.setBitInPlace(Square.getIndex(i, row1), true);
            }
        } else if (absDeltaFile == 0) {
            // it is a row
            for (int i = 0; i < 8; i++) {
                result.setBitInPlace(Square.getIndex(file1, i), true);
            }
        } else if (absDeltaFile == absDeltaRow) {
            // it is a diagonal
            int rowSign = (int) Math.signum(deltaRow);
            int fileSign = (int) Math.signum(deltaFile);

            for (int i = -8; i < 8; i++) {
                int index = Square.getIndex(file1 + i * fileSign, row1 + i * rowSign);

                if (index != -1)
                    result.setBitInPlace(index, true);
            }
        } else {
            throw new IllegalArgumentException("There are no good lines connecting these indexes!");
        }

        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof BitMap otherBitMap)) return false;
        return otherBitMap.data == data;
    }

    @Override
    public String toString() {
        final String dataString = Long.toUnsignedString(data, 2);
        // get the data in binary form
        StringBuilder sb = new StringBuilder();
        // pad left with 0s
        sb.append("0".repeat(Math.max(0, 64 - dataString.length())));
        sb.append(dataString);
        // split rows neatly
        return String.format(
                "BitMap(%s-%s-%s-%s-%s-%s-%s-%s)",
                sb.substring(0, 8),
                sb.substring(8, 16),
                sb.substring(16, 24),
                sb.substring(24, 32),
                sb.substring(32, 40),
                sb.substring(40, 48),
                sb.substring(48, 56),
                sb.substring(56, 64)
        );
    }

    public String toMultilineString() {
        final String dataString = Long.toUnsignedString(data, 2);
        // get the data in binary form
        StringBuilder sb = new StringBuilder();
        // pad left with 0s
        sb.append("0".repeat(Math.max(0, 64 - dataString.length())));
        sb.append(dataString);
        // split rows neatly
        return String.format(
                "%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s",
                sb.substring(0, 8),
                sb.substring(8, 16),
                sb.substring(16, 24),
                sb.substring(24, 32),
                sb.substring(32, 40),
                sb.substring(40, 48),
                sb.substring(48, 56),
                sb.substring(56, 64)
        );
    }
}
