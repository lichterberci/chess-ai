package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

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
    public List<Move> getAllPossibleMoves(Board board) {
        return null;
    }
}

