package chessai.chessai.lib;

public class Piece {
    private PieceType type;
    private PieceColor color;
    private Square square;

    public Piece(PieceType type, PieceColor color) {
        this.type = type;
        this.color = color;
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

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }
}
