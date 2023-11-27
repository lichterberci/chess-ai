package chessai.chessai.lib;

import chessai.chessai.lib.pieces.*;

import java.util.Random;

/**
 * This is a special kind of hashing function, commonly used in chess engines.
 * More on the algorithm: <a href="https://en.wikipedia.org/wiki/Zobrist_hashing">Wikipedia article</a>
 */
public class ZobristHash {

    static int[][] pieceBitStrings;
    static int blackToMoveBitString;

    static {
        initRandomBitStrings();
    }

    private static void initRandomBitStrings() {

        Random random = new Random(System.currentTimeMillis());

        pieceBitStrings = new int[64][12];

        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 12; j++) {
                pieceBitStrings[i][j] = random.nextInt();
            }
        }

        blackToMoveBitString = random.nextInt();
    }

    /**
     * Computes the hash of the given position (board).
     *
     * @param board the position
     * @return the zobrist hash value
     */
    public static int computeHash(Board board) {

        int hash = 0;

        if (board.colorToMove == PieceColor.BLACK)
            hash ^= blackToMoveBitString;

        for (int i = 0; i < 64; i++) {

            int indexOfPiece = 0;

            Piece piece = board.get(i);

            if (piece == null)
                continue;

            // default is 0 for pawn
            if (piece instanceof Knight)
                indexOfPiece = 1;
            else if (piece instanceof Bishop)
                indexOfPiece = 2;
            else if (piece instanceof Rook)
                indexOfPiece = 3;
            else if (piece instanceof Queen)
                indexOfPiece = 4;
            else if (piece instanceof King)
                indexOfPiece = 5;

            if (piece.getColor() == PieceColor.BLACK)
                indexOfPiece += 6;

            hash ^= pieceBitStrings[i][indexOfPiece];
        }

        return hash;
    }

    /**
     * Can be used to incrementally update hashes
     *
     * @return the updated hash
     */
    public static int xorPiece(int hash, int squareIndex, Class<? extends Piece> pieceType, PieceColor color) {

        int indexOfPiece = 0;

        // default is 0 for pawn
        if (pieceType == Knight.class)
            indexOfPiece = 1;
        else if (pieceType == Bishop.class)
            indexOfPiece = 2;
        else if (pieceType == Rook.class)
            indexOfPiece = 3;
        else if (pieceType == Queen.class)
            indexOfPiece = 4;
        else if (pieceType == King.class)
            indexOfPiece = 5;

        if (color == PieceColor.BLACK)
            indexOfPiece += 6;

        return hash ^ pieceBitStrings[squareIndex][indexOfPiece];
    }
}
