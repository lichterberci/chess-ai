package chessai.chessai.lib;

import chessai.chessai.lib.pieces.*;

import java.util.Random;

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

}
