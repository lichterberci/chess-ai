package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.List;

public class Bishop extends Piece {
    public Bishop (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'B' : 'b';
    }

    @Override
    public List<Move> getAllPossibleMoves(Board board) {
        return null;
    }
}

