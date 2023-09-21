package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;
import chessai.chessai.lib.Square;

import java.util.List;

public class Queen extends Piece {
    public Queen (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'Q' : 'q';
    }

    @Override
    public List<Square> getLegalMoves(Board board) {
        return null;
    }
}

