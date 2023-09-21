package chessai.chessai.lib;

import java.util.Collection;

public class Board {

    /**
     * 0 = A8
     * 1 = B8
     * ...
     * 8 = A7
     * ...
     * 63 = H1
     *
     * */
    Piece[] squares;
    PieceColor colorToMove;
    boolean canBlackCastleKingSide;
    boolean canBlackCastleQueenSide;
    boolean canWhiteCastleKingSide;
    boolean canWhiteCastleQueenSide;
    Square enPassantTarget;
    int numHalfMovesMadeByWhiteSinceCaptureOrPawnMove;
    int numHalfMovesMadeByBlackSinceCaptureOrPawnMove;

    public Board (String FENString) {

    }

    public Piece get (Square square) {
        return squares[square.getIndex()];
    }
    public String getFENString () {
        StringBuilder sb = new StringBuilder(40);

        int emptySquaresCount = 0;
        for (int i = 0; i < 64; i++) {
            Piece piece = squares[i];

            if (piece == null) {
                emptySquaresCount++;

                if (i % 8 == 7) {
                    sb.append(emptySquaresCount);

                    if (i != 63)
                        sb.append('/');

                    emptySquaresCount = 0;
                }

                continue;
            }

            if (emptySquaresCount > 0) {
                sb.append(emptySquaresCount);
            }

            emptySquaresCount = 0;
            sb.append(squares[i].getFENChar());

            if (i % 8 == 7 && i != 63)
                sb.append('/');
        }

        sb.append(' ');

        sb.append(colorToMove == PieceColor.WHITE ? 'w' : 'b');

        sb.append(' ');

        if (canWhiteCastleKingSide)
            sb.append('K');
        if (canWhiteCastleQueenSide)
            sb.append('Q');
        if (canBlackCastleKingSide)
            sb.append('k');
        if (canBlackCastleQueenSide)
            sb.append('q');

        if (!canWhiteCastleQueenSide && !canWhiteCastleKingSide && !canBlackCastleKingSide && !canBlackCastleQueenSide)
            sb.append('-');

        sb.append(' ');

        if (enPassantTarget != null)
            sb.append(enPassantTarget);
        else
            sb.append('-');

        sb.append(' ');

        sb.append(numHalfMovesMadeByWhiteSinceCaptureOrPawnMove);

        sb.append(' ');

        sb.append(numHalfMovesMadeByBlackSinceCaptureOrPawnMove);

        return sb.toString();


    }
}
