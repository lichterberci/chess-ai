package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;
import chessai.chessai.lib.Square;

import java.util.List;

public class Rook extends Piece {
    public Rook (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'R' : 'r';
    }

    @Override
    public List<Square> getLegalMoves(Board board) {
        return null;
    }
}

