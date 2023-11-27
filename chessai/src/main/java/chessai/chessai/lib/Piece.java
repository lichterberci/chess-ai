package chessai.chessai.lib;

/**
 * Represents a piece on the board
 */
public abstract class Piece {
    protected PieceColor color;
    protected Square square;

    protected Piece(PieceColor color) {
        this.color = color;
    }

    /**
     * Returns the character representing this piece in a FEN string
     *
     * @return the FEN character of this piece
     */
    public abstract char getFENChar();

    /**
     * Generates all the pseudo-legal moves by this piece
     *
     * @param board the current position
     * @return all the data about the pseudo-legal moves
     */
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
