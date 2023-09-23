package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

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
    public List<Move> getAllPossibleMoves(Board board) {
        return null;
    }
}

