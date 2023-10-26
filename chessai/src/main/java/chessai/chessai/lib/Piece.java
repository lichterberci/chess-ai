package chessai.chessai.lib;

public abstract class Piece {
    protected PieceColor color;
    protected Square square;

    protected Piece(PieceColor color) {
        this.color = color;
    }

    public abstract char getFENChar();

    public abstract MoveResult getPseudoLegalMoves(Board board);

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = new Square(square.getIndex());
    }

    public PieceColor getColor() {
        return color;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public abstract Piece copy();
}
