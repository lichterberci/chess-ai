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
    boolean canBlackCastleKingSide;
    boolean canBlackCastleQueenSide;
    boolean canWhiteCastleKingSide;
    boolean canWhiteCastleQueenSide;
    Square enPassantTarget;

    public Board (String FENString) {

    }
    public Board (Collection<Piece> pieces) {
        this.squares = new Piece[64];
        pieces.forEach(piece -> this.squares[piece.getSquare().getIndex()] = piece);
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
                continue;
            }

            if (emptySquaresCount > 0) {
                sb.append(emptySquaresCount);
            }

            emptySquaresCount = 0;
            sb.append()
        }

        return sb.toString();
    }
}
