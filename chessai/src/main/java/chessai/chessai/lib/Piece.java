package chessai.chessai.lib;

import java.util.List;

public abstract class Piece {
    private PieceColor color;
    private Square square;

    public Piece(PieceColor color) {
        this.color = color;
    }
    public abstract char getFENChar ();
    public abstract List<Square> getLegalMoves (Board board);
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
