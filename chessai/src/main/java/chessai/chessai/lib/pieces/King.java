package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.List;

public class King extends Piece {
    public King (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'K' : 'k';
    }

    @Override
    public List<Move> getAllPossibleMoves(Board board) {
        return null;
    }
}

