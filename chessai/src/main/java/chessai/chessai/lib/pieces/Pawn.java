package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.List;

public class Pawn extends Piece {
    public Pawn (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'P' : 'p';
    }

    @Override
    public List<Move> getAllPossibleMoves(Board board) {
        return null;
    }
}
