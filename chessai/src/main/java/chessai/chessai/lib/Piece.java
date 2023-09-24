package chessai.chessai.lib;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    protected PieceColor color;
    protected Square square;

    public Piece(PieceColor color) {
        this.color = color;
    }
    public abstract char getFENChar ();
    public abstract List<Move> getAllPossibleMoves(Board board);
    public List<Move> getLegalMoves (Board board) {
        // todo: effectively filter possible moves to get legal ones
        return new ArrayList<>();
    }
    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = square;
    }


    public PieceColor getColor() {
        return color;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

}
