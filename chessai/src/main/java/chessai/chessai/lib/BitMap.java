package chessai.chessai.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class BitMap {

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

    public BitMap(boolean bitValue) {
        data = bitValue ? 0xFFFF_FFFF_FFFF_FFFFL : 0;
    }

    public BitMap(long data) {
        this.data = data;
    }

    public BitMap(String inputString) {
        if (inputString.length() != 64)
            throw new IllegalArgumentException("inputString must have 64 characters!");

        data = 0L;

        for (int i = 0; i < 64; i++) {
            data |= (long) (inputString.charAt(i) == '1' ? 1 : 0) << i;
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

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            if (getBit(i))
                result.add(i);
        }

        return result;
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
}
