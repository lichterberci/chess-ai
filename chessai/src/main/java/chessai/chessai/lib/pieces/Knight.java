package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;
import chessai.chessai.lib.Square;

import java.util.List;

public class Knight extends Piece {
    public Knight (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'N' : 'n';
    }

    @Override
    public List<Square> getLegalMoves(Board board) {
        return null;
    }
}

